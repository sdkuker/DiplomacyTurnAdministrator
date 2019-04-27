package com.sdk.diplomacy.turnadmin.conflict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
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

public class TurnOrderResolverTest {

	protected static GameMap myGameMap = new GameMap();
	
	protected TurnOrderResolver myResolver = new TurnOrderResolver();
	
	@BeforeClass
	public static void beforeTests() {
		myGameMap.initialize();
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

		myResolver.resolve(allOrders, existingPieces);

		assertEquals("number of valid orders", 1, myResolver.validOrders.size());
		assertEquals("number of invalid orders", 0, myResolver.inValidOrders.size());
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

		myResolver.resolve(allOrders, existingPieces);

		assertEquals("number of valid orders", 0, myResolver.validOrders.size());
		assertEquals("number of invalid orders", 1, myResolver.inValidOrders.size());
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
		
		myResolver.resolveConvoyHoldMovesToActions(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(holdOrder, holdResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(convoyOrder, convoyResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", convoyResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", convoyResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(moveOrder1, moveOrder1Result, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order 1 not successful", moveOrder1Result.wasOrderExecutedSuccessfully());
		assertTrue("order 1 completed", moveOrder1Result.isOrderResolutionCompleted());
		assertTrue("order 1 standoff", moveOrder1Result.isExecutionFailedDueToStandoff());
		assertEquals("description move order 1", "Move Failed - Standoff. All competitors are: Brest : 1, Burgundy : 1, ", moveOrder1Result.getExecutionDescription());

		moveOrder1Result.setExecutionDescription(null);
		moveOrder1Result.setIsValidOrder(true);
		moveOrder1Result.setOrderExecutedSuccessfully(true);
		moveOrder1Result.setOrderResolutionCompleted(false);
		moveOrder2Result.setExecutionDescription(null);
		moveOrder2Result.setIsValidOrder(true);
		moveOrder2Result.setOrderExecutedSuccessfully(true);
		moveOrder2Result.setOrderResolutionCompleted(false);

		myResolver.resolveConvoyHoldMovesToActions(moveOrder2, moveOrder2Result, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order 2 not successful", moveOrder2Result.wasOrderExecutedSuccessfully());
		assertTrue("order 2 completed", moveOrder2Result.isOrderResolutionCompleted());
		assertTrue("order 2 standoff", moveOrder2Result.isExecutionFailedDueToStandoff());
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
		
		myResolver.resolveConvoyHoldMovesToActions(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(attackingFleetMoveOrder, attackingFleetMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", attackingFleetMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", attackingFleetMoveResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(attackingFleetMoveOrder, attackingFleetMoveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", attackingFleetMoveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", attackingFleetMoveResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertFalse("order not successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
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
		
		myResolver.resolveConvoyHoldMovesToActions(moveOrder, moveResult, ordersToExamine, ordersToExamineResults, myGameMap);
		
		assertTrue("order successful", moveResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", moveResult.isOrderResolutionCompleted());
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

}
