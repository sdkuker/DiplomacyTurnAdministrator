package com.sdk.diplomacy.turnadmin.conflict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.map.GameMap;

public class TurnOrderResolverTest {

	protected TurnOrderResolver myResolver = new TurnOrderResolver();
	
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
		
		Map<String, Order> selectedOrders = myResolver.getOrdersForEndingLocation("Brest", ordersToExamine);
		
		assertEquals("should be two", 2, selectedOrders.size());
		assertNotNull("Brest should be there", selectedOrders.get("Brest"));
		assertNotNull("Paris should be there", selectedOrders.get("Paris"));
		
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
	
	/*
	 * Need to test determineStrengthForHoldOrMoveAction method.  Need to include situations where
	 * there is support to a different move/hold/order going to the same ending location and also do a different ending location
	 */
	
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
	public void testResolveHoldActionNoSupportOrCompetition() {
		
		Order holdOrder = new Order("1", PieceType.ARMY, "Burgundy", Action.HOLDS,
				"Burgundy", null, null, null, null, "France", "turnId", "gameId");
				
		Map<String, Order> ordersToExamine = new HashMap<String, Order>();
		ordersToExamine.put("Burgundy", holdOrder);
		
		OrderResolutionResults holdResult = new OrderResolutionResults("1", "turnId", "gameId");
		
		Map<String, OrderResolutionResults> ordersToExamineResults = new HashMap<String, OrderResolutionResults>();
		ordersToExamineResults.put("1", holdResult);
		
		myResolver.resolveHoldAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
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
		
		myResolver.resolveHoldAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
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
		
		myResolver.resolveHoldAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
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
		
		myResolver.resolveHoldAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
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
		
		myResolver.resolveHoldAction(holdOrder, holdResult, ordersToExamine, ordersToExamineResults);
		
		assertTrue("order successful", holdResult.wasOrderExecutedSuccessfully());
		assertTrue("order completed", holdResult.isOrderResolutionCompleted());
		assertEquals("description", "Hold Successful. All competitors are: Burgundy : 3, Ruhr : 2, ", holdResult.getExecutionDescription());

	}


}
