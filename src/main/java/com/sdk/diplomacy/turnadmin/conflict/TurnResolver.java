package com.sdk.diplomacy.turnadmin.conflict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.map.GameMap;
import com.sdk.diplomacy.turnadmin.map.Province;

public class TurnResolver {

	protected GameMap myGameMap = new GameMap();
	
	public TurnResolver() {
		super();
		myGameMap.initialize();
	}
	
	public void resolveTurn(List<Order> ordersForTurn, List<Piece> piecesForTurn) {

		Map<String, Order> ordersByCurrentLocationMap = createMapByCurrentLocationForOrders(ordersForTurn);
		Map<String, Order> ordersByIdMap = createMapByIdForOrders(ordersForTurn);
		Map<String, Piece> piecesByCurrentLocationMap = createMapByCurrentLocationForPieces(piecesForTurn);

		OrderResolver myOrderResolver = new OrderResolver(myGameMap);

		// the key is the id of the orders that the results are for
		Map<String, OrderResolutionResults> results = myOrderResolver.resolve(ordersByCurrentLocationMap,
				piecesByCurrentLocationMap);

		updatePieceEndingLocations(results, ordersByIdMap, piecesByCurrentLocationMap);
		//TODO identifyStandoffProvinces
	}
	
	protected Set<Province> identifyStandoffProvinces(Map<String, Order> ordersByIdMap, Map<String, OrderResolutionResults> results) {
		
		Set<Province> standoffProvinces = new HashSet<Province>();
		
		results.forEach((orderId, anOrderResolutionResult) -> {
			if (anOrderResolutionResult.isExecutionFailedDueToStandoff()) {
				String standoffRegionName = ordersByIdMap.get(orderId).getEffectiveEndingLocationName();
				standoffProvinces.add(myGameMap.getProvinceContainingRegionByName(standoffRegionName));
			}
		});
		
		return standoffProvinces;
		
	}

	protected void updatePieceEndingLocations(Map<String, OrderResolutionResults> results,
			Map<String, Order> ordersByIdMap, Map<String, Piece> piecesByCurrentLocationMap) {

		List<String> locationsOccupiedBySuccessfulMoves = new ArrayList<String>();
		
		results.forEach((orderId, anOrderResolutionResult) -> {
			Order myOrder = ordersByIdMap.get(orderId);
			if (Action.MOVESTO == myOrder.getAction()) {
				if (anOrderResolutionResult.wasOrderExecutedSuccessfully()) {
					piecesByCurrentLocationMap.get(myOrder.getCurrentLocationName())
							.setNameOfLocationAtEndOfTurn(myOrder.getEndingLocationName());
					locationsOccupiedBySuccessfulMoves.add(myOrder.getEndingLocationName());
				}
			}
		});
		
		piecesByCurrentLocationMap.forEach((currentLocation, aPiece) -> {
		
			if ( (! locationsOccupiedBySuccessfulMoves.contains(aPiece.getNameOfLocationAtBeginningOfTurn())) &&
					aPiece.getNameOfLocationAtEndOfTurn() == null) {
				aPiece.setNameOfLocationAtEndOfTurn(aPiece.getNameOfLocationAtBeginningOfTurn());
			}
		});
	}

	/*
	 * Key to the map is the id of the order
	 */
	protected Map<String, Order> createMapByIdForOrders(List<Order> orders) {

		Map<String, Order> mapOfOrders = new HashMap<String, Order>();

		if (orders != null) {
			for (Order anOrder : orders) {
				mapOfOrders.put(anOrder.getId(), anOrder);
			}
		}

		return mapOfOrders;
	}

	/*
	 * Key to the map is the current location name of the piece in the order
	 */
	protected Map<String, Order> createMapByCurrentLocationForOrders(List<Order> orders) {

		Map<String, Order> mapOfOrders = new HashMap<String, Order>();

		if (orders != null) {
			for (Order anOrder : orders) {
				mapOfOrders.put(anOrder.getCurrentLocationName(), anOrder);
			}
		}

		return mapOfOrders;
	}

	/*
	 * Key to the map is the current location name of the piece
	 */
	protected Map<String, Piece> createMapByCurrentLocationForPieces(List<Piece> pieces) {

		Map<String, Piece> mapOfPieces = new HashMap<String, Piece>();

		if (pieces != null) {
			for (Piece aPiece : pieces) {
				mapOfPieces.put(aPiece.getNameOfLocationAtBeginningOfTurn(), aPiece);
			}
		}

		return mapOfPieces;
	}

}
