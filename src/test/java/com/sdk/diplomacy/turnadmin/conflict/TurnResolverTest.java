package com.sdk.diplomacy.turnadmin.conflict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.map.Province;

public class TurnResolverTest {

	@Test
	public void testCreateMapForOrdersByCurrentLocation() {

		TurnResolver myResolver = new TurnResolver();

		Map<String, Order> emptyMap = myResolver.createMapByCurrentLocationForOrders(null);

		assertNotNull("empty map exists", emptyMap);
		assertEquals("empty map has no entries", 0, emptyMap.size());

		Order order1 = new Order("1", PieceType.ARMY, "currentLocationName", Action.HOLDS, "endingLocationName", null,
				null, null, null, "France", "Turn1", "Game1");
		
		List<Order> listOfOrders = new ArrayList<Order>();
		listOfOrders.add(order1);
		
		Map<String, Order> mapWithEntry = myResolver.createMapByCurrentLocationForOrders(listOfOrders);
		assertNotNull("map with entry exists", mapWithEntry);
		assertEquals("map with entries size", 1, mapWithEntry.size());
		assertNotNull("map with entries has right entry", mapWithEntry.get("currentLocationName"));

	}
	
	@Test
	public void testCreateMapForOrdersById() {

		TurnResolver myResolver = new TurnResolver();

		Map<String, Order> emptyMap = myResolver.createMapByIdForOrders(null);

		assertNotNull("empty map exists", emptyMap);
		assertEquals("empty map has no entries", 0, emptyMap.size());

		Order order1 = new Order("1", PieceType.ARMY, "currentLocationName", Action.HOLDS, "endingLocationName", null,
				null, null, null, "France", "Turn1", "Game1");
		
		List<Order> listOfOrders = new ArrayList<Order>();
		listOfOrders.add(order1);
		
		Map<String, Order> mapWithEntry = myResolver.createMapByIdForOrders(listOfOrders);
		assertNotNull("map with entry exists", mapWithEntry);
		assertEquals("map with entries size", 1, mapWithEntry.size());
		assertNotNull("map with entries has right entry", mapWithEntry.get("1"));

	}

	
	@Test
	public void testCreateMapForPiecesByCurrentLocation() {

		TurnResolver myResolver = new TurnResolver();

		Map<String, Piece> emptyMap = myResolver.createMapByCurrentLocationForPieces(null);

		assertNotNull("empty map exists", emptyMap);
		assertEquals("empty map has no entries", 0, emptyMap.size());

		Piece piece1 = new Piece("1", "France", "locationAtBeginningOfTurn", "Turn1", "Game1", PieceType.ARMY);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(piece1);
		
		Map<String, Piece> mapWithEntry = myResolver.createMapByCurrentLocationForPieces(listOfPieces);
		assertNotNull("map with entry exists", mapWithEntry);
		assertEquals("map with entries size", 1, mapWithEntry.size());
		assertNotNull("map with entries has right entry", mapWithEntry.get("locationAtBeginningOfTurn"));

	}
		
	@Test
	public void testUpdatePieceEndingLocations() {
		
		TurnResolver myResolver = new TurnResolver();
		
		Order holdOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.HOLDS, "endingLocationName", null,
				null, null, null, "France", "Turn1", "Game1");
		Order moveOrder = new Order("2", PieceType.ARMY, "currentLocationName2", Action.MOVESTO, "currentLocationName", null,
				null, null, null, "France", "Turn1", "Game1");
		Order supportOrder = new Order("3", PieceType.ARMY, "currentLocationName3", Action.SUPPORTS, null, PieceType.ARMY,
				"currentLocationName2", Action.MOVESTO, "currentLocationName", "France", "Turn1", "Game1");

		Map<String, Order> ordersById = new HashMap<String, Order>();
		ordersById.put("1", holdOrder);
		ordersById.put("2", moveOrder);
		ordersById.put("3", supportOrder);
		
		OrderResolutionResults holdOrderResults = new OrderResolutionResults("1", "Turn1", "Game1");
		OrderResolutionResults moveOrderResults = new OrderResolutionResults("2", "Turn1", "Game1");
		OrderResolutionResults supportOrderResults = new OrderResolutionResults("3", "Turn1", "Game1");
		
		Map<String, OrderResolutionResults> orderResults = new HashMap<String, OrderResolutionResults>();
		orderResults.put("1", holdOrderResults);
		orderResults.put("2", moveOrderResults);
		orderResults.put("3", supportOrderResults);
		
		Piece holdPiece = new Piece("p1", "France", "currentLocationName", "Turn1", "Game1", PieceType.ARMY);
		Piece movePiece = new Piece("p2", "France", "currentLocationName2", "Turn1", "Game1", PieceType.ARMY);
		Piece supportPiece = new Piece("p3", "France", "currentLocationName3", "Turn1", "Game1", PieceType.ARMY);
		
		Map<String, Piece> piecesByCurrentLocation = new HashMap<String, Piece>();
		piecesByCurrentLocation.put("currentLocationName", holdPiece);
		piecesByCurrentLocation.put("currentLocationName2", movePiece);
		piecesByCurrentLocation.put("currentLocationName3", supportPiece);
		
		myResolver.updatePieceEndingLocations(orderResults, ordersById, piecesByCurrentLocation);
		
		assertNull("hold piece should have no ending location", holdPiece.getNameOfLocationAtEndOfTurn());
		assertEquals("move piece should be have moved", "currentLocationName", movePiece.getNameOfLocationAtEndOfTurn());
		assertEquals("support piece should be where it started", "currentLocationName3", supportPiece.getNameOfLocationAtEndOfTurn());
	}
	
	@Test
	public void testIdentifyStandoffProvincesSingleRegionProvinces() {
		
		TurnResolver myResolver = new TurnResolver();
		
		Order moveToBurgundyFromParisOrder = new Order("1", PieceType.ARMY, "Paris", Action.MOVESTO, "Burgundy", null,
				null, null, null, "France", "Turn1", "Game1");
		Order moveToBurgundyFromRuhrOrder = new Order("2", PieceType.ARMY, "Ruhr", Action.MOVESTO, "Burgundy", null,
				null, null, null, "France", "Turn1", "Game1");
		Order moveToLondonFromWalesOrder = new Order("3", PieceType.ARMY, "London", Action.MOVESTO, "Wales", null,
				null, null, null, "France", "Turn1", "Game1");


		Map<String, Order> ordersById = new HashMap<String, Order>();
		ordersById.put("1", moveToBurgundyFromParisOrder);
		ordersById.put("2", moveToBurgundyFromRuhrOrder);
		ordersById.put("3", moveToLondonFromWalesOrder);
		
		OrderResolutionResults moveToBurgundyFromParisOrderResults = new OrderResolutionResults("1", "Turn1", "Game1");
		moveToBurgundyFromParisOrderResults.setExecutionFailedDueToStandoff(true);
		OrderResolutionResults moveToBurgundyFromRuhrOrderResults = new OrderResolutionResults("2", "Turn1", "Game1");
		moveToBurgundyFromRuhrOrderResults.setExecutionFailedDueToStandoff(true);
		OrderResolutionResults moveToLondonFromWalesOrderResults = new OrderResolutionResults("3", "Turn1", "Game1");
		
		Map<String, OrderResolutionResults> orderResults = new HashMap<String, OrderResolutionResults>();
		orderResults.put("1", moveToBurgundyFromParisOrderResults);
		orderResults.put("2", moveToBurgundyFromRuhrOrderResults);
		orderResults.put("3", moveToLondonFromWalesOrderResults);
				
		Set<Province> standoffProvinces = myResolver.identifyStandoffProvinces(ordersById, orderResults);
		Province standoffProvincesAsArray[] = new Province[standoffProvinces.size()];
		standoffProvinces.toArray(standoffProvincesAsArray);
		
		assertNotNull("something should be returned", standoffProvinces);
		assertEquals("should be only 1 province returned", 1, standoffProvinces.size());
		assertEquals("province name should be correct", "Burgundy", standoffProvincesAsArray[0].getName());
	}

	@Test
	public void testIdentifyStandoffProvincesMultipleRegionProvinces() {
		assertTrue(false);
	}

}
