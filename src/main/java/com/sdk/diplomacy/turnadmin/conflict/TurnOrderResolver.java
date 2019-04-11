package com.sdk.diplomacy.turnadmin.conflict;

import java.util.HashMap;
import java.util.Map;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.map.GameMap;

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
	 * piece in the order
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
			break;
		case HOLDS:
			resolveHoldAction(orderToResolve, orderToResolveResults);
			break;
		case MOVESTO:
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

	protected void resolveHoldAction(Order holdOrderToResolve, OrderResolutionResults orderToResolveResults) {

		// TODO do this...
	}

	protected void resolveSuportAction(Order supportOrderToResolve, OrderResolutionResults orderToResolveResults,
			Map<String, Order> ordersToExamine) {

		/*
		 * Support is cut if a piece from any location other than the one where support
		 * is being given moves to the current location of the supporting piece
		 */

		if (!orderToResolveResults.isOrderResolutionCompleted()) {
			Map<String, Order> ordersInvolvingMyCurrentLocation = getOrdersForEndingLocation(
					supportOrderToResolve.getCurrentLocationName(), ordersToExamine);

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
	 * Return a map containing the orders that end at anEndingLocationName. The key
	 * to the map being returned is the current location of the piece involved in
	 * the order.
	 */
	protected Map<String, Order> getOrdersForEndingLocation(String anEndingLocationName,
			Map<String, Order> ordersToExamine) {

		Map<String, Order> selectedOrders = new HashMap<String, Order>();

		ordersToExamine.forEach((startingLocation, anOrder) -> {
			if (anOrder.getEffectiveEndingLocationName().equals(anEndingLocationName)) {
				selectedOrders.put(startingLocation, anOrder);
			}
		});

		return selectedOrders;
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
