package com.sdk.diplomacy.turnadmin.conflict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.map.GameMap;
import com.sdk.diplomacy.turnadmin.map.Region;
import com.sdk.diplomacy.turnadmin.map.Region.RegionType;

public class TurnOrderResolver {

	/*
	 * The key to the maps are the current/starting location of the primary piece in
	 * the order
	 */
	protected Map<String, Order> allOrders = new HashMap<String, Order>();
	protected Map<String, Order> validOrders = new HashMap<String, Order>();
	protected Map<String, Order> inValidOrders = new HashMap<String, Order>();

	/*
	 * The key is the id of the order that the results are for.
	 */
	protected Map<String, OrderResolutionResults> orderResults = new HashMap<String, OrderResolutionResults>();

	/*
	 * The key is current location of the primary piece in the order
	 */
	protected Map<String, Piece> pieces = new HashMap<String, Piece>();

	protected GameMap myGameMap = new GameMap();
	protected OrderValidator myOrderValidator = new OrderValidator();

	public TurnOrderResolver() {
		super();
		myGameMap.initialize();
	}

	protected void clearState() {

		allOrders.clear();
		validOrders.clear();
		inValidOrders.clear();
		orderResults.clear();
		pieces.clear();
		myOrderValidator = new OrderValidator();
	}

	protected void setMyOrderValidator(OrderValidator aValidator) {
		myOrderValidator = aValidator;
	}

	/*
	 * The key to both maps is the current/starting location of the piece or primary
	 * piece in the order.
	 * 
	 * Note that you should resolve all convoy orders first, then moves and holds in
	 * any order.
	 */
	public void resolve(Map<String, Order> orders, Map<String, Piece> existingPieces) {

		if (orders != null && orders.size() > 0 && existingPieces != null && existingPieces.size() > 0) {
			allOrders = orders;
			pieces = existingPieces;
			validateOrders();
			validOrders.forEach((startingLocation, anOrder) -> {
				resolveOrder(startingLocation);
			});
		}
	}

	protected void resolveOrder(String currentLocationOfOrderToResolve) {

		Order orderToResolve = validOrders.get(currentLocationOfOrderToResolve);
		OrderResolutionResults orderToResolveResults = orderResults.get(orderToResolve.getId());

		switch (orderToResolve.getAction()) {
		case CONVOYS:
			resolveConvoyHoldMovesToActions(orderToResolve, orderToResolveResults, validOrders, orderResults, myGameMap);
			break;
		case HOLDS:
			resolveConvoyHoldMovesToActions(orderToResolve, orderToResolveResults, validOrders, orderResults, myGameMap);
			break;
		case MOVESTO:
			resolveConvoyHoldMovesToActions(orderToResolve, orderToResolveResults, validOrders, orderResults, myGameMap);
			break;
		case SUPPORTS:
			resolveSuportAction(orderToResolve, orderToResolveResults, validOrders);
			break;
		default:
			orderToResolveResults.setIsValidOrder(false);
			orderToResolveResults
					.setExecutionDescription("Invalid order - unknown action found while resolving the order");
			break;
		}

	}

	protected boolean validateConvoyMovePath(Order holdOrMovesToOrderToResolve,
			OrderResolutionResults orderToResolveResults, Map<String, Order> ordersToExamine,
			Map<String, OrderResolutionResults> ordersToExamineResults, GameMap aGameMap) {

		/*
		 * the order must be a move. If it's to something adjacent, it was already
		 * validated in the OrderValidator. This method only validates moves that must
		 * be convoyed.
		 */

		boolean[] convoyPathIsComplete = { false };

		if (holdOrMovesToOrderToResolve.getPieceType() == PieceType.ARMY
				&& Action.MOVESTO == holdOrMovesToOrderToResolve.getAction()) {
			Region currentRegion = aGameMap.getRegion(holdOrMovesToOrderToResolve.getCurrentLocationName());
			Region endingRegion = aGameMap.getRegion(holdOrMovesToOrderToResolve.getEndingLocationName());
			if (currentRegion.getType() == RegionType.COASTAL && endingRegion.getType() == RegionType.COASTAL) {
				Region endingLocationBordersCurrentLocation = aGameMap
						.getRegion(holdOrMovesToOrderToResolve.getCurrentLocationName()).getBoarderingRegions()
						.get(holdOrMovesToOrderToResolve.getEndingLocationName());
				if (endingLocationBordersCurrentLocation == null) {
					/*
					 * look at all the bordering regions and see if there is a valid convoy order in
					 * it. See if it's convoying this move. See if it ends at this moves ending
					 * location. If it doesn't, see if there are other bordering regions with
					 * convoys that work. Do this recursively with the boarding regions that have
					 * good convoys if necessary.
					 */
					Map<String, Order> convoyOrdersForTheMoveOrder = getConvoyOrdersForMoveOrder(
							holdOrMovesToOrderToResolve, ordersToExamine, ordersToExamineResults);
					convoyPathIsComplete[0] = validateConvoyPath(currentRegion, holdOrMovesToOrderToResolve,
							orderToResolveResults, convoyOrdersForTheMoveOrder, ordersToExamineResults, aGameMap);
					if (!convoyPathIsComplete[0]) {
						orderToResolveResults.setOrderExecutedSuccessfully(false);
						orderToResolveResults
								.setExecutionDescription("Failed. No collection of valid convoys existed for the move");

					}

				} else {
					/*
					 * this means that there is no convoy. The army is just moving to an adjacent
					 * region. This should have been previously validated in the OrderValidator so
					 * you can ignore it here.
					 */
				}

			}
		}

		return convoyPathIsComplete[0];

	}

	protected boolean validateConvoyPath(Region currentRegion, Order holdOrMovesToOrderToResolve,
			OrderResolutionResults orderToResolveResults, Map<String, Order> convoyOrders,
			Map<String, OrderResolutionResults> ordersToExamineResults, GameMap aGameMap) {

		boolean[] convoyPathIsValid = { false };

		// first see if currentRegion borders on the ending location. If yes, you're
		// good and done.
		Map<String, Region> myCoastalRegions = aGameMap.getRegion(currentRegion.getName())
				.getBoarderingRegionsOfType(RegionType.COASTAL);
		if (myCoastalRegions.get(holdOrMovesToOrderToResolve.getEndingLocationName()) != null) {
			convoyPathIsValid[0] = true;
		} else {
			// check each of the water regions bordering me. See if there is a appropriate
			// convoy order and do it again
			Map<String, Region> waterRegionsBorderingCurrentRegion = currentRegion
					.getBoarderingRegionsOfType(RegionType.WATER);
			waterRegionsBorderingCurrentRegion.forEach((regionName, aRegion) -> {
				if (convoyOrders.get(regionName) != null) {
					convoyPathIsValid[0] = convoyPathIsValid[0]
							|| validateConvoyPath(aRegion, holdOrMovesToOrderToResolve, orderToResolveResults,
									convoyOrders, ordersToExamineResults, aGameMap);
				}
			});
		}

		return convoyPathIsValid[0];

	}
	
	protected void resolveConvoyHoldMovesToActions(Order orderToResolve,
			OrderResolutionResults orderToResolveResults, Map<String, Order> ordersToExamine,
			Map<String, OrderResolutionResults> ordersToExamineResults, GameMap aGameMap) {

		List<String> startingLocationNames = new ArrayList<String>();
		List<Integer> strengths = new ArrayList<Integer>();
		String endingLocationName = orderToResolve.getEffectiveEndingLocationName();

		String orderDescription = "Hold ";
		if (Action.MOVESTO == orderToResolve.getAction()) {
			orderDescription = "Move ";
			validateConvoyMovePath(orderToResolve, orderToResolveResults, ordersToExamine,
					ordersToExamineResults, aGameMap);
		} else {
			if (Action.CONVOYS == orderToResolve.getAction()) {
				orderDescription = "Convoy ";
				endingLocationName = orderToResolve.getCurrentLocationName();
			}
		}

		if (orderToResolveResults.wasOrderExecutedSuccessfully()) {
			Map<String, Order> allOrdersAssociatedWithMyEndingLocation = getOrdersForEndingLocation(
					endingLocationName, ordersToExamine, null);

			allOrdersAssociatedWithMyEndingLocation.forEach((startingLocation, anOrder) -> {
				if (Action.MOVESTO == anOrder.getAction() || Action.HOLDS == anOrder.getAction() || Action.CONVOYS == anOrder.getAction()) {
					int strength = determineStrengthForHoldOrMoveAction(anOrder,
							ordersToExamineResults.get(anOrder.getId()), ordersToExamine, ordersToExamineResults);
					startingLocationNames.add(startingLocation);
					strengths.add(strength);
				}
			});

			int maxStrength = 0;
			String strongestStartingLocation = null;
			StringBuilder description = new StringBuilder();

			for (int index = 0; index < strengths.size(); index++) {
				description.append(startingLocationNames.get(index) + " : " + strengths.get(index) + ", ");
				if (strengths.get(index) > maxStrength) {
					maxStrength = strengths.get(index);
					strongestStartingLocation = startingLocationNames.get(index);
				} else {
					if (strengths.get(index) == maxStrength
							&& startingLocationNames.get(index) == orderToResolve.getCurrentLocationName()
							&& (Action.HOLDS == orderToResolve.getAction() || Action.CONVOYS == orderToResolve.getAction())) {
						strongestStartingLocation = orderToResolve.getCurrentLocationName();
					}
				}
			}

			if (orderToResolve.getCurrentLocationName().contentEquals(strongestStartingLocation)) {
				orderToResolveResults.setOrderExecutedSuccessfully(true);
				orderToResolveResults
						.setExecutionDescription(orderDescription + "Successful. All competitors are: " + description);
			} else {
				orderToResolveResults.setOrderExecutedSuccessfully(false);
				orderToResolveResults
						.setExecutionDescription(orderDescription + "Failed. All competitors are: " + description);
			}

		}

	}


	protected int determineStrengthForHoldOrMoveAction(Order orderToDetermineStrengthFor,
			OrderResolutionResults orderToResolveResults, Map<String, Order> ordersToExamine,
			Map<String, OrderResolutionResults> ordersToExamineResults) {

		int[] strengthOfAction = { 1 };

		Map<String, Order> allOrdersAssociatedWithMyEndingLocation = new HashMap<String, Order>();
		
		if (Action.CONVOYS == orderToDetermineStrengthFor.getAction()) {
			allOrdersAssociatedWithMyEndingLocation.putAll(getOrdersForEndingLocation(
					orderToDetermineStrengthFor.getCurrentLocationName(), ordersToExamine, null));
		} else {
			allOrdersAssociatedWithMyEndingLocation.putAll(getOrdersForEndingLocation(
					orderToDetermineStrengthFor.getEffectiveEndingLocationName(), ordersToExamine, null));	
		}

		allOrdersAssociatedWithMyEndingLocation.forEach((startingLocation, anOrder) -> {
			if (Action.SUPPORTS == anOrder.getAction() && orderToDetermineStrengthFor.getCurrentLocationName()
					.contentEquals(anOrder.getSecondaryCurrentLocationName())) {
				resolveSuportAction(anOrder, ordersToExamineResults.get(anOrder.getId()), ordersToExamine);
				if (ordersToExamineResults.get(anOrder.getId()).wasOrderExecutedSuccessfully()) {
					strengthOfAction[0]++;
				}
			}
		});

		return strengthOfAction[0];

	}

	protected void resolveSuportAction(Order supportOrderToResolve, OrderResolutionResults orderToResolveResults,
			Map<String, Order> ordersToExamine) {

		/*
		 * Support is cut if a piece from any location other than the one where support
		 * is being given moves to the current location of the supporting piece
		 */

		if (!orderToResolveResults.isOrderResolutionCompleted()) {
			Map<String, Order> ordersInvolvingMyCurrentLocation = getOrdersForEndingLocation(
					supportOrderToResolve.getCurrentLocationName(), ordersToExamine, null);

			// need to put it in an array so it can be modified inside the lambda function
			Boolean[] supportWasCut = { false };

			ordersInvolvingMyCurrentLocation.forEach((startingLocation, anOrder) -> {
				if (Action.MOVESTO == anOrder.getAction()) {
					if (!anOrder.getCurrentLocationName()
							.equals(supportOrderToResolve.getSecondaryEndingLocationName())) {
						supportWasCut[0] = true;
						orderToResolveResults.setOrderExecutedSuccessfully(false);
						orderToResolveResults
								.setExecutionDescription("Execution Failed - Support was cut by a move from: "
										+ anOrder.getCurrentLocationName());
					}
				}
			});

			if (!supportWasCut[0]) {
				orderToResolveResults.setOrderExecutedSuccessfully(true);
				orderToResolveResults.setExecutionDescription("Execution Succeeded");

			}

		}
	}

	/*
	 * Return a map containing the orders that end at anEndingLocationName filtered
	 * by action if desired. Note that convoys will be included in the secondary
	 * piece ending location as well as their current location. This is because they
	 * both convoy(secondary ending location) and hold (current location) at the
	 * same time. The key to the map being returned is the current location of the
	 * piece involved in the order.
	 */
	protected Map<String, Order> getOrdersForEndingLocation(String anEndingLocationName,
			Map<String, Order> ordersToExamine, Action anAction) {

		Map<String, Order> selectedOrders = new HashMap<String, Order>();

		ordersToExamine.forEach((startingLocation, anOrder) -> {
			if (anOrder.getEffectiveEndingLocationName().equals(anEndingLocationName)) {
				if (anAction == null || anOrder.getAction() == anAction) {
					selectedOrders.put(startingLocation, anOrder);
				}
			} else {
				if (Action.CONVOYS == anOrder.getAction()
						&& anOrder.getCurrentLocationName().equals(anEndingLocationName)) {
					if (anAction == null || anOrder.getAction() == anAction) {
						selectedOrders.put(startingLocation, anOrder);
					}
				}
			}
		});

		return selectedOrders;
	}

	/*
	 * Return a map containing the convoy orders for a desired move order.
	 */
	protected Map<String, Order> getConvoyOrdersForMoveOrder(Order aMoveOrder, Map<String, Order> ordersToExamine,
			Map<String, OrderResolutionResults> ordersToExamineResults) {

		Map<String, Order> selectedConvoyOrders = new HashMap<String, Order>();

		ordersToExamine.forEach((startingLocation, anOrder) -> {
			if (Action.CONVOYS == anOrder.getAction()
					&& anOrder.getSecondaryCurrentLocationName().equals(aMoveOrder.getCurrentLocationName())
					&& anOrder.getSecondaryPieceType().equals(aMoveOrder.getPieceType())
					&& anOrder.getSecondaryEndingLocationName().equals(aMoveOrder.getEndingLocationName())
					&& ordersToExamineResults.get(anOrder.getId()).wasOrderExecutedSuccessfully()) {
				selectedConvoyOrders.put(startingLocation, anOrder);
			}
		});

		return selectedConvoyOrders;
	}

	protected void validateOrders() {

		allOrders.forEach((startingLocation, anOrder) -> {
			OrderResolutionResults results = new OrderResolutionResults(anOrder.getId(), anOrder.getTurnId(),
					anOrder.getGameId());
			orderResults.put(anOrder.getId(), results);
			myOrderValidator.validateOrder(anOrder, results, myGameMap, pieces, allOrders);
			if (results.isValidOrder()) {
				validOrders.put(startingLocation, anOrder);
			} else {
				inValidOrders.put(startingLocation, anOrder);
			}
		});
	}
}
