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

	public ConflictResolutionResults resolveConflict(List<Order> ordersForTurn, List<Piece> piecesForTurn) {

		ConflictResolutionResults myResults = new ConflictResolutionResults();
		
		Map<String, Order> ordersByCurrentLocationMap = createMapByCurrentLocationForOrders(ordersForTurn);
		Map<String, Order> ordersByIdMap = createMapByIdForOrders(ordersForTurn);
		Map<String, Piece> piecesByCurrentLocationMap = createMapByCurrentLocationForPieces(piecesForTurn);

		OrderResolver myOrderResolver = new OrderResolver(myGameMap);

		// the key is the id of the orders that the results are for
		Map<String, OrderResolutionResults> results = myOrderResolver.resolve(ordersByCurrentLocationMap,
				piecesByCurrentLocationMap);

		updatePieceEndingLocations(results, ordersByIdMap, piecesByCurrentLocationMap);
		Set<StandoffProvince> standoffProvices = identifyStandoffProvinces(ordersByIdMap, results);
		
		myResults.setOrderResolutionResults(results);
		myResults.setStandoffProvinces(standoffProvices);

		return myResults;
	}

	protected Set<StandoffProvince> identifyStandoffProvinces(Map<String, Order> ordersByIdMap,
			Map<String, OrderResolutionResults> results) {

		Set<StandoffProvince> standoffProvinces = new HashSet<StandoffProvince>();

		results.forEach((orderId, anOrderResolutionResult) -> {
			if (anOrderResolutionResult.isExecutionFailedDueToStandoff()) {
				String standoffRegionName = ordersByIdMap.get(orderId).getEffectiveEndingLocationName();
				String standoffProvinceName = myGameMap.getProvinceContainingRegionByName(standoffRegionName).getName();
				StandoffProvince aStandOffProvince = new StandoffProvince(null, standoffProvinceName,
						anOrderResolutionResult.getTurnId(), anOrderResolutionResult.getGameId());
				standoffProvinces.add(aStandOffProvince);
			}
		});

		return standoffProvinces;

	}

	protected void updatePieceEndingLocations(Map<String, OrderResolutionResults> results,
			Map<String, Order> ordersByIdMap, Map<String, Piece> piecesByCurrentLocationMap) {

		List<Province> provincesOccupiedBySuccessfulMoves = new ArrayList<Province>();

		results.forEach((orderId, anOrderResolutionResult) -> {
			Order myOrder = ordersByIdMap.get(orderId);
			if (Action.MOVESTO == myOrder.getAction()) {
				if (anOrderResolutionResult.wasOrderExecutedSuccessfully()) {
					piecesByCurrentLocationMap.get(myOrder.getCurrentLocationName())
							.setNameOfLocationAtEndOfPhase(myOrder.getEndingLocationName());
					provincesOccupiedBySuccessfulMoves
							.add(myGameMap.getProvinceContainingRegionByName(myOrder.getEndingLocationName()));
				}
			}
		});

		piecesByCurrentLocationMap.forEach((currentLocation, aPiece) -> {

			/*
			 * if the pieces locationAtEndOfTurn is filled in, it means that the piece is
			 * done. If it's not filled in, it should stay where it started unless something
			 * else took it's place - in which case it's been displaced. You must see if
			 * something is in the province, not just the region.
			 */
			if (aPiece.getNameOfLocationAtEndOfPhase() == null) {
				if (provincesOccupiedBySuccessfulMoves.contains(
						myGameMap.getProvinceContainingRegionByName(aPiece.getNameOfLocationAtBeginningOfPhase()))) {
					aPiece.setMustRetreatAtEndOfTurn(true);
				} else {
					aPiece.setNameOfLocationAtEndOfPhase(aPiece.getNameOfLocationAtBeginningOfPhase());
				}
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
				mapOfPieces.put(aPiece.getNameOfLocationAtBeginningOfPhase(), aPiece);
			}
		}

		return mapOfPieces;
	}

}
