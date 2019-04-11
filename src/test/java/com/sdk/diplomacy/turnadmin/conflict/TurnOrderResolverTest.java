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

}
