package com.sdk.diplomacy.turnadmin.conflict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.map.GameMap;

public class OrderResolverTest {

	protected static GameMap myGameMap = new GameMap();
	
	protected static OrderResolver myResolver;
	
	@BeforeClass
	public static void beforeTests() {
		myGameMap.initialize();
		myResolver = new OrderResolver(myGameMap);
	}
	
	@Before
	public void beforeAllTests() {
		myResolver.clearState();
	}
		
	@Test
	public void testResolveOrderWithValidOrder() {

		OrderValidator mockedValidator = mock(OrderValidator.class);
		doNothing().when(mockedValidator).validateOrder(any(Order.class), any(OrderResolutionResults.class), any(GameMap.class), anyMap(), anyMap());
		myResolver.setMyOrderValidator(mockedValidator);
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Brest", Action.HOLDS,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("Brest", holdOrder);
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));

		Map<String, OrderResolutionResults> myResults = myResolver.resolve(allOrders, existingPieces);

		assertEquals("number of valid orders", 1, myResolver.validOrders.size());
		assertEquals("number of invalid orders", 0, myResolver.inValidOrders.size());
		assertEquals("number of results returned", 1, myResults.size());
	}

	@Test
	public void testResolveOrderWithInvalidOrder() {

		OrderValidator mockedValidator = mock(OrderValidator.class);
		
		doAnswer((Answer) invocation -> {
			OrderResolutionResults results = invocation.getArgument(1);
			results.setIsValidOrder(false);
			return null;
		}).when(mockedValidator).validateOrder(any(Order.class), any(OrderResolutionResults.class), any(GameMap.class), anyMap(), anyMap());

		myResolver.setMyOrderValidator(mockedValidator);
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Brest", Action.HOLDS,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("Brest", holdOrder);
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));

		Map<String, OrderResolutionResults> myResults = myResolver.resolve(allOrders, existingPieces);

		assertEquals("number of valid orders", 0, myResolver.validOrders.size());
		assertEquals("number of invalid orders", 1, myResolver.inValidOrders.size());
		assertEquals("number of results returned", 1, myResults.size());
	}
	
	@Test
	public void testGetOrdersForEndingLocation() {
		
		Order brestOrder1 = new Order("1", PieceType.ARMY, "Brest", Action.HOLDS,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order brestOrder2 = new Order("1", PieceType.ARMY, "Paris", Action.MOVESTO,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order burgundyOrder1 = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", brestOrder1);
		ordersToExamine.put("Paris", brestOrder2);
		ordersToExamine.put("Burgundy", burgundyOrder1);
		
		Map<String, Order> selectedOrders = myResolver.getOrdersForEndingLocation("Brest", ordersToExamine, null);
		
		assertEquals("should be two", 2, selectedOrders.size());
		assertNotNull("Brest should be there", selectedOrders.get("Brest"));
		assertNotNull("Paris should be there", selectedOrders.get("Paris"));
		
	}
	
	@Test
	public void testGetOrdersForEndingLocationWithFilterOnAction() {
		
		Order brestOrder1 = new Order("1", PieceType.ARMY, "Brest", Action.HOLDS,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order brestOrder2 = new Order("1", PieceType.ARMY, "Paris", Action.MOVESTO,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order burgundyOrder1 = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", brestOrder1);
		ordersToExamine.put("Paris", brestOrder2);
		ordersToExamine.put("Burgundy", burgundyOrder1);
		
		Map<String, Order> selectedOrders = myResolver.getOrdersForEndingLocation("Brest", ordersToExamine, Action.MOVESTO);
		
		assertEquals("should be on1", 1, selectedOrders.size());
		assertNotNull("Paris should be there", selectedOrders.get("Paris"));
		
	}

	@Test
	public void testGetConvoyOrdersForMoveOrder() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Liverpool", null, null, null, null, "France", "turnId", "gameId");
		
		Order usefullConvoyOrder1 = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Liverpool", "France", "turnId", "gameId");
		
		Order usefullConvoyOrder2 = new Order("3", PieceType.FLEET, "Irish_Sea", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Liverpool", "France", "turnId", "gameId");

		Order uselessConvoyOrder1 = new Order("4", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.CONVOYS,
				null, PieceType.ARMY, "Gascony", Action.MOVESTO, "Portugal", "France", "turnId", "gameId");
		
		Order uselessMoveOrder1 = new Order("5", PieceType.ARMY, "Yorkshire", Action.MOVESTO,
				"Liverpool", null, null, null, null, "France", "turnId", "gameId");


		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", moveOrder);
		ordersToExamine.put("English_Channel", usefullConvoyOrder1);
		ordersToExamine.put("Irish_Sea", usefullConvoyOrder2);
		ordersToExamine.put("Mid_Atlantic_Ocean", uselessConvoyOrder1);
		ordersToExamine.put("Yorkshire", uselessMoveOrder1);
		
		OrderResolutionResults moveOrderResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults usefullConvoyOrder1Result = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults usefullConvoyOrder2Result = new OrderResolutionResults("3", "turnId", "gameId");
		OrderResolutionResults uselessConvoyOrder1Result = new OrderResolutionResults("4", "turnId", "gameId");
		OrderResolutionResults uselessMoveOrder1Result = new OrderResolutionResults("5", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveOrderResult);
		ordersToExamineResults.put("2", usefullConvoyOrder1Result);
		ordersToExamineResults.put("3", usefullConvoyOrder2Result);
		ordersToExamineResults.put("4", uselessConvoyOrder1Result);
		ordersToExamineResults.put("5", uselessMoveOrder1Result);

		
		Map<String, Order> selectedOrders = myResolver.getConvoyOrdersForMoveOrder(moveOrder, ordersToExamine, ordersToExamineResults);
		
		assertEquals("should be two", 2, selectedOrders.size());
		assertNotNull("English Channel should be there", selectedOrders.get("English_Channel"));
		assertNotNull("Irish Sea should be there", selectedOrders.get("Irish_Sea"));
		
	}
	
	@Test
	public void testGetConvoyOrdersForMoveOrderWithFailedConvoy() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Liverpool", null, null, null, null, "France", "turnId", "gameId");
		
		Order usefullConvoyOrder1 = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Liverpool", "France", "turnId", "gameId");
		
		Order usefullConvoyOrder2 = new Order("3", PieceType.FLEET, "Irish_Sea", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Liverpool", "France", "turnId", "gameId");

		Order uselessConvoyOrder1 = new Order("4", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.CONVOYS,
				null, PieceType.ARMY, "Gascony", Action.MOVESTO, "Portugal", "France", "turnId", "gameId");
		
		Order uselessMoveOrder1 = new Order("5", PieceType.ARMY, "Yorkshire", Action.MOVESTO,
				"Liverpool", null, null, null, null, "France", "turnId", "gameId");


		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", moveOrder);
		ordersToExamine.put("English_Channel", usefullConvoyOrder1);
		ordersToExamine.put("Irish_Sea", usefullConvoyOrder2);
		ordersToExamine.put("Mid_Atlantic_Ocean", uselessConvoyOrder1);
		ordersToExamine.put("Yorkshire", uselessMoveOrder1);
		
		OrderResolutionResults moveOrderResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults usefullConvoyOrder1Result = new OrderResolutionResults("2", "turnId", "gameId");
		usefullConvoyOrder1Result.setOrderExecutedSuccessfully(false);
		OrderResolutionResults usefullConvoyOrder2Result = new OrderResolutionResults("3", "turnId", "gameId");
		OrderResolutionResults uselessConvoyOrder1Result = new OrderResolutionResults("4", "turnId", "gameId");
		OrderResolutionResults uselessMoveOrder1Result = new OrderResolutionResults("5", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveOrderResult);
		ordersToExamineResults.put("2", usefullConvoyOrder1Result);
		ordersToExamineResults.put("3", usefullConvoyOrder2Result);
		ordersToExamineResults.put("4", uselessConvoyOrder1Result);
		ordersToExamineResults.put("5", uselessMoveOrder1Result);

		
		Map<String, Order> selectedOrders = myResolver.getConvoyOrdersForMoveOrder(moveOrder, ordersToExamine, ordersToExamineResults);
		
		assertEquals("should be one", 1, selectedOrders.size());
		assertNotNull("Irish Sea should be there", selectedOrders.get("Irish_Sea"));
		
	}

	@Test
	public void testResolveSuportActionSuccessfulSupport() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Brest", Action.HOLDS,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder = new Order("2", PieceType.ARMY, "Paris", Action.MOVESTO,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order supportOrder = new Order("3", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Paris", Action.MOVESTO, "Brest", "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", holdOrder);
		ordersToExamine.put("Paris", moveOrder);
		ordersToExamine.put("Picardy", supportOrder);
		
		OrderResolutionResults result = new OrderResolutionResults("3", "turnId", "gameId");
		
		myResolver.resolveSuportAction(supportOrder, result, ordersToExamine);
		
		assertTrue("should be successful", result.wasOrderExecutedSuccessfully());
		assertTrue("should be complete", result.isOrderResolutionCompleted());
		assertEquals("description", "Execution Succeeded", result.getExecutionDescription());

	}
	
	@Test
	public void testResolveSuportActionSuccessfulSupportAttackFromSupportingRegion() {
		
		Order attackingOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Picardy", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder = new Order("2", PieceType.ARMY, "Paris", Action.MOVESTO,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order supportOrder = new Order("3", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Paris", Action.MOVESTO, "Brest", "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", attackingOrder);
		ordersToExamine.put("Paris", moveOrder);
		ordersToExamine.put("Picardy", supportOrder);
		
		OrderResolutionResults result = new OrderResolutionResults("3", "turnId", "gameId");
		
		myResolver.resolveSuportAction(supportOrder, result, ordersToExamine);
		
		assertTrue("should be successful", result.wasOrderExecutedSuccessfully());
		assertTrue("should be complete", result.isOrderResolutionCompleted());
		assertEquals("description", "Execution Succeeded", result.getExecutionDescription());

	}
	
	@Test
	public void testResolveSuportActionSupportCut() {
		
		Order attackingOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.MOVESTO,
				"Picardy", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder = new Order("2", PieceType.ARMY, "Paris", Action.MOVESTO,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		
		Order supportOrder = new Order("3", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Paris", Action.MOVESTO, "Brest", "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", attackingOrder);
		ordersToExamine.put("Paris", moveOrder);
		ordersToExamine.put("Picardy", supportOrder);
		
		OrderResolutionResults result = new OrderResolutionResults("3", "turnId", "gameId");
		
		myResolver.resolveSuportAction(supportOrder, result, ordersToExamine);
		
		assertFalse("should not be successful", result.wasOrderExecutedSuccessfully());
		assertTrue("should be complete", result.isOrderResolutionCompleted());
		assertEquals("description", "Execution Failed - Support was cut by a move from: Burgundy", result.getExecutionDescription());

	}
	
	
	@Test
	public void testDetermineStrengthForHoldOrMoveActionWithNoSupport() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		
		int supportStrength = myResolver.determineStrengthForHoldOrMoveAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
		assertEquals("support strength", 1, supportStrength);

	}
		
	@Test
	public void testDetermineStrengthForHoldOrMoveActionWith2ValidSupports() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Order supportOrder1 = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");
			
		Order supportOrder2 = new Order("3", PieceType.ARMY, "Marseilles", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Picardy", supportOrder1);
		ordersToExamine.put("Marseilles", supportOrder2);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults support1Result = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults support2Result = new OrderResolutionResults("3", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("2", support1Result);
		ordersToExamineResults.put("3", support2Result);

		
		int supportStrength = myResolver.determineStrengthForHoldOrMoveAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
		assertEquals("support strength", 3, supportStrength);

	}
	
	@Test
	public void testDetermineStrengthForHoldOrMoveActionWithSupportForMoveToDifferentLocation() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Order supportOrder1 = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");
			
		Order supportOrder2 = new Order("3", PieceType.ARMY, "Marseilles", Action.SUPPORTS,
				null, PieceType.ARMY, "Spain", Action.HOLDS, "Spain", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Picardy", supportOrder1);
		ordersToExamine.put("Marseilles", supportOrder2);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults support1Result = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults support2Result = new OrderResolutionResults("3", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("2", support1Result);
		ordersToExamineResults.put("3", support2Result);

		
		int supportStrength = myResolver.determineStrengthForHoldOrMoveAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
		assertEquals("support strength", 2, supportStrength);

	}

	@Test
	public void testDetermineStrengthForHoldOrMoveActionWithSupportForDifferentPieceToSameLocation() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Order supportOrder1 = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");
			
		Order supportOrder2 = new Order("3", PieceType.ARMY, "Marseilles", Action.SUPPORTS,
				null, PieceType.ARMY, "Gascony", Action.MOVESTO, "Burgundy", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Picardy", supportOrder1);
		ordersToExamine.put("Marseilles", supportOrder2);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults support1Result = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults support2Result = new OrderResolutionResults("3", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("2", support1Result);
		ordersToExamineResults.put("3", support2Result);

		
		int supportStrength = myResolver.determineStrengthForHoldOrMoveAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
		assertEquals("support strength", 2, supportStrength);

	}
	
	@Test
	public void testDetermineStrengthForAConvoyAsItsHolding() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");
				
		Order convoyOrder = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "London", "France", "turnId", "gameId");
			

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", moveOrder);
		ordersToExamine.put("English_Channel", convoyOrder);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults convoyResult = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		ordersToExamineResults.put("2", convoyResult);

		int convoyStrength = myResolver.determineStrengthForHoldOrMoveAction(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults);
		
		assertEquals("convoy strength", 1, convoyStrength);

	}
	
	@Test
	public void testDetermineStrengthForAConvoyAsItsHoldingWithSupport() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");
				
		Order convoyOrder = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "London", "France", "turnId", "gameId");
		
		Order supportOrder1 = new Order("3", PieceType.FLEET, "North_Sea", Action.SUPPORTS,
				null, PieceType.FLEET, "English_Channel", Action.HOLDS, "English_Channel", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", moveOrder);
		ordersToExamine.put("English_Channel", convoyOrder);
		ordersToExamine.put("North_Sea", supportOrder1);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults convoyResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults supportResult1 = new OrderResolutionResults("3", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		ordersToExamineResults.put("2", convoyResult);
		ordersToExamineResults.put("3", supportResult1);

		int convoyStrength = myResolver.determineStrengthForHoldOrMoveAction(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults);
		
		assertEquals("convoy strength", 2, convoyStrength);

	}

	
	@Test
	public void testResolveHoldActionNoSupportOrCompetition() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
		assertEquals("order strength", 1, orderStrength);
		assertEquals("description", "Hold Successful. All competitors are: Burgundy : 1, ", holdResult.getExecutionDescription());

	}
	

	@Test
	public void testResolveHoldActionWithSupportButNoCompetition() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order supportOrder1 = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");

				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Picardy", supportOrder1);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults support1Result = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("2", support1Result);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
		assertEquals("order strength", 2, orderStrength);
		assertEquals("description", "Hold Successful. All competitors are: Burgundy : 2, ", holdResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveHoldActionWithNoSupportButEqualCompetion() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder = new Order("11", PieceType.ARMY, "Ruhr", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");

				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Ruhr", moveOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults moveResult = new OrderResolutionResults("11", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("11", moveResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
		assertEquals("order strength", 1, orderStrength);
		assertEquals("description", "Hold Successful. All competitors are: Burgundy : 1, Ruhr : 1, ", holdResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveHoldActionWithNoSupportButStrongerCompetion() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder = new Order("11", PieceType.ARMY, "Ruhr", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order ruhrSupportOrder = new Order("12", PieceType.ARMY, "Munich", Action.SUPPORTS,
				null, PieceType.ARMY, "Ruhr", Action.MOVESTO, "Burgundy", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Ruhr", moveOrder);
		ordersToExamine.put("Munich", ruhrSupportOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults moveResult = new OrderResolutionResults("11", "turnId", "gameId");
		OrderResolutionResults ruhrSupportResult = new OrderResolutionResults("12", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("11", moveResult);
		ordersToExamineResults.put("12", ruhrSupportResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
		assertEquals("order strength", 0, orderStrength);
		assertEquals("description", "Hold Failed. All competitors are: Burgundy : 1, Ruhr : 2, ", holdResult.getExecutionDescription());

	}

	@Test
	public void testResolveHoldActionWithMoreSupportThanCompetition() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order burgundySupportOrder1 = new Order("2", PieceType.ARMY, "Marseilles", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");

		Order burgundySupportOrder2 = new Order("3", PieceType.ARMY, "Gascony", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");

		Order moveOrder = new Order("11", PieceType.ARMY, "Ruhr", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order ruhrSupportOrder = new Order("12", PieceType.ARMY, "Munich", Action.SUPPORTS,
				null, PieceType.ARMY, "Ruhr", Action.MOVESTO, "Burgundy", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Marseilles", burgundySupportOrder1);
		ordersToExamine.put("Gascony", burgundySupportOrder2);
		ordersToExamine.put("Ruhr", moveOrder);
		ordersToExamine.put("Munich", ruhrSupportOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults burgundySupportResult1 = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults burgundySupportResult2 = new OrderResolutionResults("3", "turnId", "gameId");
		OrderResolutionResults moveResult = new OrderResolutionResults("11", "turnId", "gameId");
		OrderResolutionResults ruhrSupportResult = new OrderResolutionResults("12", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("2", burgundySupportResult1);
		ordersToExamineResults.put("3", burgundySupportResult2);
		ordersToExamineResults.put("11", moveResult);
		ordersToExamineResults.put("12", ruhrSupportResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
		assertEquals("order strength", 3, orderStrength);
		assertEquals("description", "Hold Successful. All competitors are: Burgundy : 3, Ruhr : 2, ", holdResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveConvoyActionNoSupportOrCompetition() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Picardy", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");

		Order convoyOrder = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Picardy", Action.MOVESTO, "London", "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Picardy", moveOrder);
		ordersToExamine.put("English_Channel", convoyOrder);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults convoyResult = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		ordersToExamineResults.put("2", convoyResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
		assertEquals("order strength", 1, orderStrength);
		assertEquals("description", "Convoy Successful. All competitors are: English_Channel : 1, ", convoyResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveConvoyActionWithSupportButNoCompetition() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Picardy", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");

		Order convoyOrder = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Picardy", Action.MOVESTO, "London", "France", "turnId", "gameId");
		
		Order convoySupportOrder = new Order("3", PieceType.FLEET, "North_Sea", Action.SUPPORTS,
				null, PieceType.FLEET, "English_Channel", Action.HOLDS, "English_Channel", "France", "turnId", "gameId");

				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Picardy", moveOrder);
		ordersToExamine.put("English_Channel", convoyOrder);
		ordersToExamine.put("North_Sea", convoySupportOrder);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults convoyResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults convoySupportResult = new OrderResolutionResults("3", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		ordersToExamineResults.put("2", convoyResult);
		ordersToExamineResults.put("3", convoySupportResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
		assertEquals("order strength", 2, orderStrength);
		assertEquals("description", "Convoy Successful. All competitors are: English_Channel : 2, ", convoyResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveConvoyActionWithNoSupportButEqualCompetition() {
		
		Order armyMoveOrder = new Order("1", PieceType.ARMY, "Picardy", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");

		Order convoyOrder = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Picardy", Action.MOVESTO, "London", "France", "turnId", "gameId");
		
		Order fleetMoveOrder = new Order("3", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");

				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Picardy", armyMoveOrder);
		ordersToExamine.put("English_Channel", convoyOrder);
		ordersToExamine.put("Mid_Atlantic_Ocean", fleetMoveOrder);
		
		OrderResolutionResults armyMoveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults convoyResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults fleetMoveResult = new OrderResolutionResults("3", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", armyMoveResult);
		ordersToExamineResults.put("2", convoyResult);
		ordersToExamineResults.put("3", fleetMoveResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
		assertEquals("order strength", 1, orderStrength);
		assertEquals("description", "Convoy Successful. All competitors are: English_Channel : 1, Mid_Atlantic_Ocean : 1, ", convoyResult.getExecutionDescription());

	}

	@Test
	public void testResolveConvoyActionWithNoSupportButStrongerCompetition() {
		
		Order armyMoveOrder = new Order("1", PieceType.ARMY, "Picardy", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");

		Order convoyOrder = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Picardy", Action.MOVESTO, "London", "France", "turnId", "gameId");
		
		Order fleetMoveOrder = new Order("3", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");
		
		Order fleetMoveSupportOrder = new Order("4", PieceType.FLEET, "North_Sea", Action.SUPPORTS,
				null, PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO, "English_Channel", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Picardy", armyMoveOrder);
		ordersToExamine.put("English_Channel", convoyOrder);
		ordersToExamine.put("Mid_Atlantic_Ocean", fleetMoveOrder);
		ordersToExamine.put("North_Sea", fleetMoveSupportOrder);
		
		OrderResolutionResults armyMoveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults convoyResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults fleetMoveResult = new OrderResolutionResults("3", "turnId", "gameId");
		OrderResolutionResults fleetMoveSupportResult = new OrderResolutionResults("4", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", armyMoveResult);
		ordersToExamineResults.put("2", convoyResult);
		ordersToExamineResults.put("3", fleetMoveResult);
		ordersToExamineResults.put("4", fleetMoveSupportResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
		assertEquals("order strength", 0, orderStrength);
		assertEquals("description", "Convoy Failed. All competitors are: English_Channel : 1, Mid_Atlantic_Ocean : 2, ", convoyResult.getExecutionDescription());

	}

	@Test
	public void testResolveConvoyActionWithMoreSupportThanTheCompetition() {
		
		Order armyMoveOrder = new Order("1", PieceType.ARMY, "Picardy", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");

		Order convoyOrder = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Picardy", Action.MOVESTO, "London", "France", "turnId", "gameId");
		
		Order fleetMoveOrder = new Order("3", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");
		
		Order convoySupportOrder = new Order("4", PieceType.FLEET, "North_Sea", Action.SUPPORTS,
				null, PieceType.FLEET, "English_Channel", Action.HOLDS, "English_Channel", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Picardy", armyMoveOrder);
		ordersToExamine.put("English_Channel", convoyOrder);
		ordersToExamine.put("Mid_Atlantic_Ocean", fleetMoveOrder);
		ordersToExamine.put("North_Sea", convoySupportOrder);
		
		OrderResolutionResults armyMoveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults convoyResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults fleetMoveResult = new OrderResolutionResults("3", "turnId", "gameId");
		OrderResolutionResults convoySupportOrderResult = new OrderResolutionResults("4", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", armyMoveResult);
		ordersToExamineResults.put("2", convoyResult);
		ordersToExamineResults.put("3", fleetMoveResult);
		ordersToExamineResults.put("4", convoySupportOrderResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
		assertEquals("order strength", 2, orderStrength);
		assertEquals("description", "Convoy Successful. All competitors are: English_Channel : 2, Mid_Atlantic_Ocean : 1, ", convoyResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveMoveActionWithStandoffWithNoSupport() {
		
		Order moveOrder1 = new Order("1", PieceType.ARMY, "Burgundy", Action.MOVESTO,
				"Paris", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder2 = new Order("2", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Paris", null, null, null, null, "France", "turnId", "gameId");

				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", moveOrder1);
		ordersToExamine.put("Brest", moveOrder2);
		
		OrderResolutionResults moveOrder1Result = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults moveOrder2Result = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveOrder1Result);
		ordersToExamineResults.put("2", moveOrder2Result);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(moveOrder1, moveOrder1Result, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order 1 not successful", moveOrder1Result.wasOrderExecutedSuccessfully());
		assertTrue("order 1 completed", moveOrder1Result.isOrderResolutionCompleted());
		assertTrue("order 1 standoff", moveOrder1Result.isExecutionFailedDueToStandoff());
		assertEquals("order strength", 0, orderStrength);
		assertEquals("description move order 1", "Move Failed - Standoff. All competitors are: Brest : 1, Burgundy : 1, ", moveOrder1Result.getExecutionDescription());

		moveOrder1Result.setExecutionDescription(null);
		moveOrder1Result.setIsValidOrder(true);
		moveOrder1Result.setOrderExecutedSuccessfully(true);
		moveOrder1Result.setOrderResolutionCompleted(false);
		moveOrder2Result.setExecutionDescription(null);
		moveOrder2Result.setIsValidOrder(true);
		moveOrder2Result.setOrderExecutedSuccessfully(true);
		moveOrder2Result.setOrderResolutionCompleted(false);

		orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(moveOrder2, moveOrder2Result, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order 2 not successful", moveOrder2Result.wasOrderExecutedSuccessfully());
		assertTrue("order 2 completed", moveOrder2Result.isOrderResolutionCompleted());
		assertTrue("order 2 standoff", moveOrder2Result.isExecutionFailedDueToStandoff());
		assertEquals("order strength", 0, orderStrength);
		assertEquals("description move order 2", "Move Failed - Standoff. All competitors are: Brest : 1, Burgundy : 1, ", moveOrder2Result.getExecutionDescription());

	}

	@Test
	public void testResolveMovesToActionNoSupportOrCompetition() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Paris", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Paris", moveOrder);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
		assertEquals("order strength", 1, orderStrength);
		assertEquals("description", "Move Successful. All competitors are: Paris : 1, ", moveResult.getExecutionDescription());

	}

	@Test
	public void testResolveMovesToActionWithSupportButNoCompetition() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Paris", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order supportOrder1 = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Paris", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Paris", moveOrder);
		ordersToExamine.put("Picardy", supportOrder1);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults support1Result = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		ordersToExamineResults.put("2", support1Result);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
		assertEquals("order strength", 2, orderStrength);
		assertEquals("description", "Move Successful. All competitors are: Paris : 2, ", moveResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveMovesToActionWithNoSupportButEqualCompetion() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder = new Order("11", PieceType.ARMY, "Ruhr", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Ruhr", moveOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults moveResult = new OrderResolutionResults("11", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("11", moveResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
		assertEquals("order strength", 0, orderStrength);
		assertEquals("description", "Move Failed. All competitors are: Burgundy : 1, Ruhr : 1, ", moveResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveMovesToActionToConvoyingFleetLocationWithAttackingMoveStronger() {
		
		Order convoyOrder = new Order("1", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "London", "France", "turnId", "gameId");
		
		Order armyMoveOrder = new Order("2", PieceType.ARMY, "Brest", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");
		
		Order attackingFleetMoveOrder = new Order("3", PieceType.FLEET, "Irish_Sea", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");
		
		Order attackingFleetSupportOrder = new Order("4", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.SUPPORTS,
				null, PieceType.FLEET, "Irish_Sea", Action.MOVESTO, "English_Channel", "France", "turnId", "gameId");


		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("English_Channel", convoyOrder);
		ordersToExamine.put("Brest", armyMoveOrder);
		ordersToExamine.put("Irish_Sea", attackingFleetMoveOrder);
		ordersToExamine.put("Mid_Atlantic_Ocean", attackingFleetSupportOrder);
		
		OrderResolutionResults convoyResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults armyMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults attackingFleetMoveResult = new OrderResolutionResults("3", "turnId", "gameId");
		OrderResolutionResults attackingFleetSupportResult = new OrderResolutionResults("4", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", convoyResult);
		ordersToExamineResults.put("2", armyMoveResult);
		ordersToExamineResults.put("3", attackingFleetMoveResult);
		ordersToExamineResults.put("4", attackingFleetSupportResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(attackingFleetMoveOrder, attackingFleetMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", attackingFleetMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", attackingFleetMoveResult.isOrderResolutionCompleted());
		assertEquals("order strength", 2, orderStrength);
		assertEquals("description", "Move Successful. All competitors are: English_Channel : 1, Irish_Sea : 2, ", attackingFleetMoveResult.getExecutionDescription());

	}
	
	@Test
	public void testResolveMovesToActionToConvoyingFleetLocationWithAttackingMoveWeaker() {
		
		Order convoyOrder = new Order("1", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "London", "France", "turnId", "gameId");
		
		Order armyMoveOrder = new Order("2", PieceType.ARMY, "Brest", Action.MOVESTO,
				"London", null, null, null, null, "France", "turnId", "gameId");
		
		Order attackingFleetMoveOrder = new Order("3", PieceType.FLEET, "Irish_Sea", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");
		
		Order convoyingFleetSupportOrder = new Order("4", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.SUPPORTS,
				null, PieceType.FLEET, "English_Channel", Action.HOLDS, "English_Channel", "France", "turnId", "gameId");


		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("English_Channel", convoyOrder);
		ordersToExamine.put("Brest", armyMoveOrder);
		ordersToExamine.put("Irish_Sea", attackingFleetMoveOrder);
		ordersToExamine.put("Mid_Atlantic_Ocean", convoyingFleetSupportOrder);
		
		OrderResolutionResults convoyResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults armyMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults attackingFleetMoveResult = new OrderResolutionResults("3", "turnId", "gameId");
		OrderResolutionResults convoyingFleetSupportResult = new OrderResolutionResults("4", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", convoyResult);
		ordersToExamineResults.put("2", armyMoveResult);
		ordersToExamineResults.put("3", attackingFleetMoveResult);
		ordersToExamineResults.put("4", convoyingFleetSupportResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(attackingFleetMoveOrder, attackingFleetMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", attackingFleetMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", attackingFleetMoveResult.isOrderResolutionCompleted());
		assertEquals("order strength", 0, orderStrength);
		assertEquals("description", "Move Failed. All competitors are: English_Channel : 2, Irish_Sea : 1, ", attackingFleetMoveResult.getExecutionDescription());
		
	}
	
	@Test
	public void testResolveMoveActionWithNoSupportButStrongerCompetion() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order moveOrder = new Order("11", PieceType.ARMY, "Ruhr", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order burgundySupportOrder = new Order("12", PieceType.ARMY, "Munich", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Ruhr", moveOrder);
		ordersToExamine.put("Munich", burgundySupportOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults moveResult = new OrderResolutionResults("11", "turnId", "gameId");
		OrderResolutionResults burgundySupportResult = new OrderResolutionResults("12", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("11", moveResult);
		ordersToExamineResults.put("12", burgundySupportResult);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
		assertEquals("order strength", 0, orderStrength);
		assertEquals("description", "Move Failed. All competitors are: Burgundy : 2, Ruhr : 1, ", moveResult.getExecutionDescription());

	}

	
	@Test
	public void testResolveMovesToActionWithMoreSupportThanCompetition() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order burgundySupportOrder1 = new Order("2", PieceType.ARMY, "Marseilles", Action.SUPPORTS,
				null, PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");

		Order moveOrder = new Order("11", PieceType.ARMY, "Ruhr", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		
		Order ruhrSupportOrder = new Order("12", PieceType.ARMY, "Munich", Action.SUPPORTS,
				null, PieceType.ARMY, "Ruhr", Action.MOVESTO, "Burgundy", "France", "turnId", "gameId");
		
		Order ruhrSupportOrder2 = new Order("13", PieceType.ARMY, "Gascony", Action.SUPPORTS,
				null, PieceType.ARMY, "Ruhr", Action.HOLDS, "Burgundy", "France", "turnId", "gameId");


		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		ordersToExamine.put("Marseilles", burgundySupportOrder1);
		ordersToExamine.put("Gascony", ruhrSupportOrder2);
		ordersToExamine.put("Ruhr", moveOrder);
		ordersToExamine.put("Munich", ruhrSupportOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults burgundySupportResult1 = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults moveResult = new OrderResolutionResults("11", "turnId", "gameId");
		OrderResolutionResults ruhrSupportResult = new OrderResolutionResults("12", "turnId", "gameId");
		OrderResolutionResults ruhrSupportOrder2Result = new OrderResolutionResults("13", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		ordersToExamineResults.put("2", burgundySupportResult1);
		ordersToExamineResults.put("11", moveResult);
		ordersToExamineResults.put("12", ruhrSupportResult);
		ordersToExamineResults.put("13", ruhrSupportOrder2Result);
		
		int orderStrength = myResolver.resolveConvoyHoldMovesToActionsByRegion(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
		assertEquals("order strength", 3, orderStrength);
		assertEquals("description", "Move Successful. All competitors are: Burgundy : 2, Ruhr : 3, ", moveResult.getExecutionDescription());

	}
	
	@Test
	public void testValidateConvoyMovePathSingleHop() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Wales", null, null, null, null, "France", "turnId", "gameId");
		
		Order usefullConvoyOrder1 = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Wales", "France", "turnId", "gameId");

		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", moveOrder);
		ordersToExamine.put("English_Channel", usefullConvoyOrder1);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults usefullConvoyOrder1Result = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		ordersToExamineResults.put("2", usefullConvoyOrder1Result);
		
		boolean moveIsComplete = myResolver.validateConvoyMovePath(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("move Should Be Complete", moveIsComplete);	
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertFalse("order not completed", moveResult.isOrderResolutionCompleted());
		assertNull("description", moveResult.getExecutionDescription());

	}
	
	@Test
	public void testValidateConvoyMovePathSingleHopButNoConvoy() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Wales", null, null, null, null, "France", "turnId", "gameId");
		
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", moveOrder);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		
		boolean moveIsComplete = myResolver.validateConvoyMovePath(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("move Should not be complete", moveIsComplete);	
		assertFalse("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
		assertEquals("description", "Failed. No collection of valid convoys existed for the move", moveResult.getExecutionDescription());

	}
	
	
	@Test
	public void testValidateConvoyMovePathMultipleHops() {
		
		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Liverpool", null, null, null, null, "France", "turnId", "gameId");
		
		Order usefullConvoyOrder1 = new Order("2", PieceType.FLEET, "English_Channel", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Liverpool", "France", "turnId", "gameId");
		
		Order usefullConvoyOrder2 = new Order("3", PieceType.FLEET, "Irish_Sea", Action.CONVOYS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Liverpool", "France", "turnId", "gameId");


		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Brest", moveOrder);
		ordersToExamine.put("English_Channel", usefullConvoyOrder1);
		ordersToExamine.put("Irish_Sea", usefullConvoyOrder2);
		
		OrderResolutionResults moveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults usefullConvoyOrder1Result1 = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults usefullConvoyOrder1Result2 = new OrderResolutionResults("3", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", moveResult);
		ordersToExamineResults.put("2", usefullConvoyOrder1Result1);
		ordersToExamineResults.put("3", usefullConvoyOrder1Result2);

		boolean moveIsComplete = myResolver.validateConvoyMovePath(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("move should be complete", moveIsComplete);	
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertFalse("order not completed", moveResult.isOrderResolutionCompleted());
		assertNull("description", moveResult.getExecutionDescription());
		
	}
	
	@Test
	public void testSelectOrdersByAction() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Brest", Action.HOLDS,
				"Brest", null, null, null, null, "France", "turnId", "gameId");
		Order moveOrder1 = new Order("2", PieceType.ARMY, "Picardy", Action.MOVESTO,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
		Order moveOrder2 = new Order("3", PieceType.ARMY, "Berlin", Action.MOVESTO,
				"Ruhr", null, null, null, null, "France", "turnId", "gameId");
		Order supportOrder = new Order("4", PieceType.ARMY, "Prussia", Action.SUPPORTS,
				null, PieceType.ARMY, "Berlin", Action.MOVESTO, "Ruhr", "France", "turnId", "gameId");
		
		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("Brest", holdOrder);
		allOrders.put("Picardy", moveOrder1);
		allOrders.put("Berlin", moveOrder2);
		allOrders.put("Prussia", supportOrder);
		
		List<Action> selectedActions = new ArrayList<Action>();
		selectedActions.add(Action.HOLDS);
		
		Map<String, Order> holdOrders = myResolver.selectOrdersByAction(allOrders, selectedActions);

		assertEquals("single selected action number of orders", 1, holdOrders.size());
		assertNotNull("single selected action order exists", holdOrders.get("Brest"));
		
		selectedActions.add(Action.MOVESTO);
		Map<String, Order> holdAndMovesToOrders = myResolver.selectOrdersByAction(allOrders, selectedActions);
		
		assertEquals("multiple selected action number of orders", 3, holdAndMovesToOrders.size());
		assertNotNull("multiple selected action order hold exists", holdAndMovesToOrders.get("Brest"));
		assertNotNull("multiple selected action order move1 exists", holdAndMovesToOrders.get("Picardy"));
		assertNotNull("multiple selected action order move2 exists", holdAndMovesToOrders.get("Berlin"));

	}
	
	@Test
	public void testSelectOrdersByEffectiveEndingLocation() {
		
		Order holdInSpainOrder = new Order("1", PieceType.ARMY, "Spain", Action.HOLDS,
				"Spain", null, null, null, null, "France", "turnId", "gameId");
		Order moveToSpainOrder = new Order("2", PieceType.ARMY, "Gascony", Action.MOVESTO,
				"Spain", null, null, null, null, "France", "turnId", "gameId");
		Order moveToSpainNCOrder = new Order("3", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO,
				"Spain_(nc)", null, null, null, null, "France", "turnId", "gameId");
		Order holdInSpainNCOrder = new Order("4", PieceType.FLEET, "Spain_(nc)", Action.HOLDS,
				"Spain_(nc)", null, null, null, null, "France", "turnId", "gameId");
		Order holdInBrestOrder = new Order("1", PieceType.ARMY, "Brest", Action.HOLDS,
				"Brest", null, null, null, null, "France", "turnId", "gameId");

		
		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("Spain", holdInSpainOrder);
		allOrders.put("Gascony", moveToSpainOrder);
		allOrders.put("Mid_Atlantic_Ocean", moveToSpainNCOrder);
		allOrders.put("Spain_(nc)", holdInSpainNCOrder);
		allOrders.put("Brest", holdInBrestOrder);
		
		Map<String, Order> spainOrders = myResolver.selectOrdersByEffectiveEndingLocationProvince(allOrders, myGameMap.getProvince("Spain"));

		assertEquals("orders for Spain", 4, spainOrders.size());
		assertNotNull("spain hold order selected", spainOrders.get("Spain"));
		assertNotNull("spain move order selected", spainOrders.get("Gascony"));
		assertNotNull("spain nc move order selected", spainOrders.get("Mid_Atlantic_Ocean"));
		assertNotNull("spain nc move order selected", spainOrders.get("Spain_(nc)"));
		
	}

	@Test
	public void testSingleRegionProvinceResolveHoldActionNoSupportOrCompetition() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		
		myResolver.resolveConvoyHoldMovesToActions(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
		assertEquals("description", "Hold Successful. All competitors are: Burgundy : 1, ", holdResult.getExecutionDescription());

	}

	
	@Test
	public void testMultiRegionProvinceResolveHoldActionNoSupportOrCompetition() {
		
		Order spainHoldOrder = new Order("1", PieceType.ARMY, "Spain", Action.HOLDS,
				"Spain", null, null, null, null, "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Spain", spainHoldOrder);
		
		OrderResolutionResults spainHoldResult = new OrderResolutionResults("1", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", spainHoldResult);
		
		myResolver.resolveConvoyHoldMovesToActions(spainHoldOrder, spainHoldResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", spainHoldResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", spainHoldResult.isOrderResolutionCompleted());
		assertEquals("description", "Hold Successful. All competitors are: Spain : 1, ", spainHoldResult.getExecutionDescription());

	}
	
	@Test
	public void testMultiRegionProvinceResolveHoldActionWithCompetingEqualStrengthMoveToRegion() {
		
		Order spainHoldOrder = new Order("1", PieceType.ARMY, "Spain", Action.HOLDS,
				"Spain", null, null, null, null, "France", "turnId", "gameId");
		Order spainNCMoveOrder = new Order("2", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO,
				"Spain_(nc)", null, null, null, null, "France", "turnId", "gameId");

				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Spain", spainHoldOrder);
		ordersToExamine.put("Mid_Atlantic_Ocean", spainNCMoveOrder);
		
		OrderResolutionResults spainHoldResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults spainNCMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", spainHoldResult);
		ordersToExamineResults.put("2", spainNCMoveResult);
		
		myResolver.resolveConvoyHoldMovesToActions(spainHoldOrder, spainHoldResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("hold order successful", spainHoldResult.wasOrderExecutedSuccessfully());
		assertTrue("hold order completed", spainHoldResult.isOrderResolutionCompleted());
		assertEquals("hold description", "Hold Successful. All competitors are: Spain : 1, ", spainHoldResult.getExecutionDescription());
		
		assertFalse("nc move order not successful", spainNCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("nc move order completed", spainNCMoveResult.isOrderResolutionCompleted());
		assertEquals("nc move description", "Failed. A hold in one of the regions in the province won", spainNCMoveResult.getExecutionDescription());
		
		// should work the same way when resolving the other order 
		spainHoldResult = new OrderResolutionResults("1", "turnId", "gameId");
		spainNCMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		ordersToExamineResults.clear();
		ordersToExamineResults.put("1", spainHoldResult);
		ordersToExamineResults.put("2", spainNCMoveResult);
		
		myResolver.resolveConvoyHoldMovesToActions(spainNCMoveOrder, spainNCMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);

		assertTrue("hold order successful - 2", spainHoldResult.wasOrderExecutedSuccessfully());
		assertTrue("hold order completed - 2", spainHoldResult.isOrderResolutionCompleted());
		assertEquals("hold description - 2", "Hold Successful. All competitors are: Spain : 1, ", spainHoldResult.getExecutionDescription());
		
		assertFalse("nc move order not successful - 2", spainNCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("nc move order completed - 2", spainNCMoveResult.isOrderResolutionCompleted());
		assertEquals("nc move description - 2", "Failed. A hold in one of the regions in the province won", spainNCMoveResult.getExecutionDescription());

	}

	@Test
	public void testMultiRegionProvinceResolveEqualStrengthMovesToMultipleRegionProducingStandoff() {
		
		Order spainNCMoveOrder = new Order("1", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO,
				"Spain_(nc)", null, null, null, null, "France", "turnId", "gameId");
		Order spainSCMoveOrder = new Order("2", PieceType.FLEET, "Gulf_of_Lyon", Action.MOVESTO,
				"Spain_(sc)", null, null, null, null, "France", "turnId", "gameId");

				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Mid_Atlantic_Ocean", spainNCMoveOrder);
		ordersToExamine.put("Gulf_of_Lyon", spainSCMoveOrder);
		
		OrderResolutionResults spainNCMoveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults spainSCMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", spainNCMoveResult);
		ordersToExamineResults.put("2", spainSCMoveResult);
		
		myResolver.resolveConvoyHoldMovesToActions(spainNCMoveOrder, spainNCMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("spain NC order not successful", spainNCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("spain NC order completed", spainNCMoveResult.isOrderResolutionCompleted());
		assertEquals("spain NC description", "Failed. Standoff with moves to multiple regions in the province", spainNCMoveResult.getExecutionDescription());
		
		assertFalse("sc move order not successful", spainSCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("sc move order completed", spainSCMoveResult.isOrderResolutionCompleted());
		assertEquals("sc move description", "Failed. Standoff with moves to multiple regions in the province", spainSCMoveResult.getExecutionDescription());
		
		// should work the same way when resolving the other order 
		spainNCMoveResult = new OrderResolutionResults("1", "turnId", "gameId");
		spainSCMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		ordersToExamineResults.clear();
		ordersToExamineResults.put("1", spainNCMoveResult);
		ordersToExamineResults.put("2", spainSCMoveResult);
		
		myResolver.resolveConvoyHoldMovesToActions(spainSCMoveOrder, spainSCMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);

		assertFalse("spain SC order not successful", spainSCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("spain SC order completed", spainSCMoveResult.isOrderResolutionCompleted());
		assertEquals("spain SC description", "Failed. Standoff with moves to multiple regions in the province", spainSCMoveResult.getExecutionDescription());
		
		assertFalse("nc move order not successful", spainNCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("nc move order completed", spainNCMoveResult.isOrderResolutionCompleted());
		assertEquals("nc move description", "Failed. Standoff with moves to multiple regions in the province", spainNCMoveResult.getExecutionDescription());

	}
	
	@Test
	public void testMultiRegionProvinceResolveUnevenStrengthMovesToMultipleRegionProducingVictor() {

		Order spainNCMoveOrder = new Order("1", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO,
				"Spain_(nc)", null, null, null, null, "France", "turnId", "gameId");
		Order spainSCMoveOrder = new Order("2", PieceType.FLEET, "Gulf_of_Lyon", Action.MOVESTO,
				"Spain_(sc)", null, null, null, null, "France", "turnId", "gameId");
		Order spainSCSupportOrder = new Order("3", PieceType.FLEET, "Western_Mediterranean", Action.SUPPORTS,
				null, PieceType.FLEET, "Gulf_of_Lyon", Action.MOVESTO, "Spain_(sc)", "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Mid_Atlantic_Ocean", spainNCMoveOrder);
		ordersToExamine.put("Gulf_of_Lyon", spainSCMoveOrder);
		ordersToExamine.put("Western_Mediterranean", spainSCSupportOrder);
		
		OrderResolutionResults spainNCMoveResult = new OrderResolutionResults("1", "turnId", "gameId");
		OrderResolutionResults spainSCMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		OrderResolutionResults spainSCSupportResult = new OrderResolutionResults("3", "turnId", "gameId");
		spainSCSupportResult.setOrderExecutedSuccessfully(true);
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", spainNCMoveResult);
		ordersToExamineResults.put("2", spainSCMoveResult);
		ordersToExamineResults.put("3", spainSCSupportResult);
		
		myResolver.resolveConvoyHoldMovesToActions(spainNCMoveOrder, spainNCMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("spain NC order not successful", spainNCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("spain NC order completed", spainNCMoveResult.isOrderResolutionCompleted());
		assertEquals("spain NC description", "Failed.  There was a stronger move or hold in one of the other regions in the province", spainNCMoveResult.getExecutionDescription());
		
		assertTrue("sc move order successful", spainSCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("sc move order completed", spainSCMoveResult.isOrderResolutionCompleted());
		assertEquals("sc move description", "Move Successful. All competitors are: Gulf_of_Lyon : 2, ", spainSCMoveResult.getExecutionDescription());
		
		assertTrue("sc support order successful", spainSCSupportResult.wasOrderExecutedSuccessfully());
		assertTrue("sc support order completed", spainSCSupportResult.isOrderResolutionCompleted());
		assertEquals("sc support description", "Move Successful. All competitors are: Gulf_of_Lyon : 2, ", spainSCMoveResult.getExecutionDescription());

		// should work the same way when resolving the other order 
		spainNCMoveResult = new OrderResolutionResults("1", "turnId", "gameId");
		spainSCMoveResult = new OrderResolutionResults("2", "turnId", "gameId");
		spainSCSupportResult = new OrderResolutionResults("3", "turnId", "gameId");
		ordersToExamineResults.clear();
		ordersToExamineResults.put("1", spainNCMoveResult);
		ordersToExamineResults.put("2", spainSCMoveResult);
		ordersToExamineResults.put("3", spainSCSupportResult);
		
		myResolver.resolveConvoyHoldMovesToActions(spainSCMoveOrder, spainSCMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);

		assertFalse("spain NC order not successful", spainNCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("spain NC order completed", spainNCMoveResult.isOrderResolutionCompleted());
		assertEquals("spain NC description", "Failed.  There was a stronger move or hold in one of the other regions in the province", spainNCMoveResult.getExecutionDescription());
		
		assertTrue("sc move order successful", spainSCMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("sc move order completed", spainSCMoveResult.isOrderResolutionCompleted());
		assertEquals("sc move description", "Move Successful. All competitors are: Gulf_of_Lyon : 2, ", spainSCMoveResult.getExecutionDescription());
		
		assertTrue("sc support order successful", spainSCSupportResult.wasOrderExecutedSuccessfully());
		assertTrue("sc support order completed", spainSCSupportResult.isOrderResolutionCompleted());
		assertEquals("sc support description", "Move Successful. All competitors are: Gulf_of_Lyon : 2, ", spainSCMoveResult.getExecutionDescription());

	}
		

}
