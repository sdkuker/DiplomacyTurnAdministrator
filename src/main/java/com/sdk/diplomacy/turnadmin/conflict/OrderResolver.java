package com.sdk.diplomacy.turnadmin.conflict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.map.GameMap;
import com.sdk.diplomacy.turnadmin.map.Province;
import com.sdk.diplomacy.turnadmin.map.Region;
import com.sdk.diplomacy.turnadmin.map.Region.RegionType;

public class OrderResolver {

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

	public OrderResolver(GameMap aGameMap) {
		super();
		myGameMap = aGameMap;
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
	public Map<String, OrderResolutionResults> resolve(Map<String, Order> orders, Map<String, Piece> existingPieces) {

		if (orders != null && orders.size() > 0 && existingPieces != null && existingPieces.size() > 0) {
			allOrders = orders;
			pieces = existingPieces;
			validateOrders();
			List<Action> desiredActions = new ArrayList<Action>();

			desiredActions.clear();
			desiredActions.add(Action.SUPPORTS);
			Map<String, Order> supportOrders = selectOrdersByAction(validOrders, desiredActions);
			supportOrders.forEach((startingLocation, anOrder) -> {
				resolveOrder(startingLocation);
			});

			desiredActions.add(Action.CONVOYS);
			Map<String, Order> convoyOrders = selectOrdersByAction(validOrders, desiredActions);
			convoyOrders.forEach((startingLocation, anOrder) -> {
				resolveOrder(startingLocation);
			});

			desiredActions.clear();
			desiredActions.add(Action.MOVESTO);
			Map<String, Order> moveOrders = selectOrdersByAction(validOrders, desiredActions);
			moveOrders.forEach((startingLocation, anOrder) -> {
				resolveOrder(startingLocation);
			});

			desiredActions.clear();
			desiredActions.add(Action.HOLDS);
			Map<String, Order> holdOrders = selectOrdersByAction(validOrders, desiredActions);
			holdOrders.forEach((startingLocation, anOrder) -> {
				resolveOrder(startingLocation);
			});
		}

		return orderResults;
	}

	/*
	 * Key to the map being returned is the starting location of the order
	 */
	public Map<String, Order> selectOrdersByAction(Map<String, Order> allOrders, List<Action> desiredActions) {

		Map<String, Order> selectedOrders = new HashMap<String, Order>();

		if (allOrders != null && desiredActions != null) {
			allOrders.forEach((orderStartingLocation, anOrder) -> {
				if (desiredActions.contains(anOrder.getAction())) {
					selectedOrders.put(orderStartingLocation, anOrder);
				}
			});
		}
		;

		return selectedOrders;

	}

	/*
	 * Key to the map being returned is the starting location of the order
	 */
	protected Map<String, Order> selectOrdersByEffectiveEndingLocationProvince(Map<String, Order> allOrders,
			Province desiredProvince) {

		Map<String, Order> selectedOrders = new HashMap<String, Order>();

		if (allOrders != null && desiredProvince != null) {
			allOrders.forEach((orderStartingLocation, anOrder) -> {
				if (desiredProvince.getRegions().containsKey(anOrder.getEffectiveEndingLocationName())) {
					selectedOrders.put(orderStartingLocation, anOrder);
				}
			});
		}
		;

		return selectedOrders;

	}

	protected void resolveOrder(String currentLocationOfOrderToResolve) {

		Order orderToResolve = validOrders.get(currentLocationOfOrderToResolve);
		OrderResolutionResults orderToResolveResults = orderResults.get(orderToResolve.getId());

		switch (orderToResolve.getAction()) {
		case CONVOYS:
			resolveConvoyHoldMovesToActions(orderToResolve, orderToResolveResults, validOrders, orderResults,
					myGameMap);
			break;
		case HOLDS:
			resolveConvoyHoldMovesToActions(orderToResolve, orderToResolveResults, validOrders, orderResults,
					myGameMap);
			break;
		case MOVESTO:
			resolveConvoyHoldMovesToActions(orderToResolve, orderToResolveResults, validOrders, orderResults,
					myGameMap);
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

	protected void resolveConvoyHoldMovesToActions(Order orderToResolve, OrderResolutionResults orderToResolveResults,
			Map<String, Order> ordersToExamine, Map<String, OrderResolutionResults> ordersToExamineResults,
			GameMap aGameMap) {

		Province provinceForOrderToResolve = aGameMap
				.getProvinceContainingRegionByName(orderToResolve.getEffectiveEndingLocationName());
		if (provinceForOrderToResolve.getRegions().size() == 1) {
			resolveConvoyHoldMovesToActionsByRegion(orderToResolve, orderToResolveResults, ordersToExamine,
					ordersToExamineResults, aGameMap);
		} else {
			Map<String, Order> allOrdersForProvince = selectOrdersByEffectiveEndingLocationProvince(ordersToExamine,
					provinceForOrderToResolve);
			// key is starting location of the order, value is the strength that order has.
			// The value is 0 if the order failed.
			Map<String, Integer> strengthsOfAllOrdersForProvince = new HashMap<String, Integer>();

			allOrdersForProvince.forEach((startingLocation, anOrderForProvince) -> {
				if (Action.SUPPORTS != anOrderForProvince.getAction()) {
					Integer orderStrength = resolveConvoyHoldMovesToActionsByRegion(anOrderForProvince,
							ordersToExamineResults.get(anOrderForProvince.getId()), ordersToExamine,
							ordersToExamineResults, aGameMap);
					strengthsOfAllOrdersForProvince.put(startingLocation, orderStrength);
				}
			});

			// key is starting location. Value is true if it's a hold order
			Map<String, Boolean> strongestStartingLocations = new HashMap<String, Boolean>();
			Map<String, Integer> strongestStartingLocationStrengths = new HashMap<String, Integer>();
			int[] maxStrength = { 0 };

			strengthsOfAllOrdersForProvince.forEach((startingLocation, strengthOfOrderAtStartingLocation) -> {
				if (strengthOfOrderAtStartingLocation > maxStrength[0]) {
					Order startingLocationOrder = ordersToExamine.get(startingLocation);
					strongestStartingLocations.clear();
					strongestStartingLocationStrengths.clear();
					maxStrength[0] = strengthOfOrderAtStartingLocation;
					if (Action.HOLDS == startingLocationOrder.getAction()) {
						strongestStartingLocations.put(startingLocation, true);
					} else {
						strongestStartingLocations.put(startingLocation, false);
					}
				} else {
					if (strengthOfOrderAtStartingLocation > 0 && strengthOfOrderAtStartingLocation == maxStrength[0]) {
						Order startingLocationOrder = ordersToExamine.get(startingLocation);
						if (Action.HOLDS == startingLocationOrder.getAction()) {
							strongestStartingLocations.put(startingLocation, true);
						} else {
							strongestStartingLocations.put(startingLocation, false);
						}
					}
				}
			});

			/*
			 * Find the single strongest order for the province. All the rest of the orders
			 * fail. If more than one have the same strength and one is a hold, it wins. If
			 * there are multiple ties and no holds, then all fail - it's a standoff.
			 */
			String[] winningStartingLocation = {"NONE"};

			if (strongestStartingLocations.size() == 1) {
				winningStartingLocation[0] = (String) strongestStartingLocations.keySet().toArray()[0];
			} else {
				boolean winningStartingLocationIsHold = strongestStartingLocations.containsValue(true);
				strongestStartingLocations.forEach((startingLocation, isHoldAction) -> {
					if (!isHoldAction) {
						Order startingLocationOrder = ordersToExamine.get(startingLocation);
						OrderResolutionResults startingLocationOrderResults = ordersToExamineResults
								.get(startingLocationOrder.getId());
						startingLocationOrderResults.setOrderExecutedSuccessfully(false);
						if (winningStartingLocationIsHold) {
							startingLocationOrderResults.setExecutionDescription(
									"Failed. A hold in one of the regions in the province won");
						} else {
							orderToResolveResults.setExecutionFailedDueToStandoff(true);
							startingLocationOrderResults.setExecutionDescription(
									"Failed. Standoff with moves to multiple regions in the province");
						}
					} else {
						winningStartingLocation[0] = startingLocation;
					}
				});
			}
			/*
			 * loop through all the orders for the province. If there were successful moves
			 * or holds that aren't from the winningStartingLocation, mark them failed
			 */
			allOrdersForProvince.forEach((startingLocation, anOrder) -> {
				if (anOrder.getAction() == Action.MOVESTO || anOrder.getAction() == Action.HOLDS) {
					if (winningStartingLocation[0] == "NONE"
							|| winningStartingLocation[0] != anOrder.getCurrentLocationName()) {
						OrderResolutionResults anOrderResults = ordersToExamineResults.get(anOrder.getId());
						if (anOrderResults.wasOrderExecutedSuccessfully()) {
							anOrderResults.setOrderExecutedSuccessfully(false);
							anOrderResults.setExecutionDescription(
									"Failed.  There was a stronger move or hold in one of the other regions in the province");

						}
					}
				}

			});
		}

	}

	protected int resolveConvoyHoldMovesToActionsByRegion(Order orderToResolve,
			OrderResolutionResults orderToResolveResults, Map<String, Order> ordersToExamine,
			Map<String, OrderResolutionResults> ordersToExamineResults, GameMap aGameMap) {

		int strengthOfOrderIfSuccessful = 0; // return zero if the order was not successful

		Map<String, Order> startingLocationNames = new HashMap<String, Order>();
		Map<String, Integer> strengths = new HashMap<String, Integer>();
		String endingLocationName = orderToResolve.getEffectiveEndingLocationName();

		String orderDescription = "Hold ";
		if (Action.MOVESTO == orderToResolve.getAction()) {
			orderDescription = "Move ";
			validateConvoyMovePath(orderToResolve, orderToResolveResults, ordersToExamine, ordersToExamineResults,
					aGameMap);
		} else {
			if (Action.CONVOYS == orderToResolve.getAction()) {
				orderDescription = "Convoy ";
				endingLocationName = orderToResolve.getCurrentLocationName();
			}
		}

		if (orderToResolveResults.wasOrderExecutedSuccessfully()) {
			Map<String, Order> allOrdersAssociatedWithMyEndingLocation = getOrdersForEndingLocation(endingLocationName,
					ordersToExamine, null);

			allOrdersAssociatedWithMyEndingLocation.forEach((startingLocation, anOrder) -> {
				if (Action.MOVESTO == anOrder.getAction() || Action.HOLDS == anOrder.getAction()
						|| Action.CONVOYS == anOrder.getAction()) {
					int strength = determineStrengthForHoldOrMoveAction(anOrder,
							ordersToExamineResults.get(anOrder.getId()), ordersToExamine, ordersToExamineResults);
					startingLocationNames.put(startingLocation, anOrder);
					strengths.put(startingLocation, strength);
				}
			});

			int[] maxStrength = { 0 };
			// key is starting location. Value is whether it's a hold or convoy order
			Map<String, Boolean> strongestStartingLocations = new HashMap<String, Boolean>();
			Map<String, Integer> strongestStartingLocationStrengths = new HashMap<String, Integer>();
			StringBuilder description = new StringBuilder();

			strengths.forEach((startingLocationName, strength) -> {
				description.append(startingLocationName + " : " + strength + ", ");
				if (strength > maxStrength[0]) {
					maxStrength[0] = strength;
					strongestStartingLocations.clear();
					strongestStartingLocationStrengths.clear();
					strongestStartingLocationStrengths.put(startingLocationName, strength);
					if (Action.HOLDS == startingLocationNames.get(startingLocationName).getAction()
							|| Action.CONVOYS == startingLocationNames.get(startingLocationName).getAction()) {
						strongestStartingLocations.put(startingLocationName, true);
					} else {
						strongestStartingLocations.put(startingLocationName, false);
					}
				} else {
					if (strength == maxStrength[0]) {
						if (Action.HOLDS == startingLocationNames.get(startingLocationName).getAction()
								|| Action.CONVOYS == startingLocationNames.get(startingLocationName).getAction()) {
							strongestStartingLocations.clear();
							strongestStartingLocationStrengths.clear();
							strongestStartingLocationStrengths.put(startingLocationName, strength);
							strongestStartingLocations.put(startingLocationName, true);
						} else {
							// this is for standoffs
							strongestStartingLocations.put(startingLocationName, false);
							strongestStartingLocationStrengths.put(startingLocationName, strength);
						}
					}
				}
			});

			if (strongestStartingLocations.size() == 1) {
				if (strongestStartingLocations.containsKey(orderToResolve.getCurrentLocationName())) {
					strengthOfOrderIfSuccessful = strongestStartingLocationStrengths
							.get(orderToResolve.getCurrentLocationName());
					orderToResolveResults.setOrderExecutedSuccessfully(true);
					orderToResolveResults.setExecutionDescription(
							orderDescription + "Successful. All competitors are: " + description);

				} else {
					orderToResolveResults.setOrderExecutedSuccessfully(false);
					orderToResolveResults
							.setExecutionDescription(orderDescription + "Failed. All competitors are: " + description);
				}
			} else {
				if (strongestStartingLocations.containsKey(orderToResolve.getCurrentLocationName())
						&& strongestStartingLocations.get(orderToResolve.getCurrentLocationName())) {
					strengthOfOrderIfSuccessful = strongestStartingLocationStrengths
							.get(orderToResolve.getCurrentLocationName());
					orderToResolveResults.setOrderExecutedSuccessfully(true);
					orderToResolveResults.setExecutionDescription(
							orderDescription + "Successful. All competitors are: " + description);

				} else {
					if (strongestStartingLocations.containsValue(true)) {
						orderToResolveResults.setOrderExecutedSuccessfully(false);
						orderToResolveResults.setExecutionDescription(
								orderDescription + "Failed. All competitors are: " + description);
					} else {
						orderToResolveResults.setOrderExecutedSuccessfully(false);
						orderToResolveResults.setExecutionFailedDueToStandoff(true);
						orderToResolveResults.setExecutionDescription(
								orderDescription + "Failed - Standoff. All competitors are: " + description);

					}
				}
			}
		}
		return strengthOfOrderIfSuccessful;
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
