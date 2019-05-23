package com.sdk.diplomacy.turnadmin.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;

public class OrderTest {

	@Test
	public void testDescription() {
		
		Order myHoldOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.HOLDS, null, null, null, null, null, "France", "aTurnId", "aGameId");
		assertEquals("hold", "ARMY in Paris Holds", myHoldOrder.getDescription());
	
		Order myMoveOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.MOVESTO, "Burgundy", null, null, null, null, "France", "aTurnId", "aGameId");
		assertEquals("move", "ARMY in Paris moves to Burgundy", myMoveOrder.getDescription());

		Order mySupportHoldOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.HOLDS, null, "France", "aTurnId", "aGameId");
		assertEquals("support hold", "ARMY in Paris supports ARMY in Burgundy Holds", mySupportHoldOrder.getDescription());

		Order mySupportMoveOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.MOVESTO, "Belguim", "France", "aTurnId", "aGameId");
		assertEquals("support move", "ARMY in Paris supports ARMY in Burgundy moves to Belguim", mySupportMoveOrder.getDescription());

		Order mySupportConvoyOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.CONVOYS, "Belguim", "France", "aTurnId", "aGameId");
		assertEquals("support convoy", "ARMY in Paris supports ARMY in Burgundy convoys to Belguim", mySupportConvoyOrder.getDescription());

		Order myConvoyOrder = new Order("anOrderId", PieceType.FLEET, "Paris", Action.CONVOYS, null, PieceType.ARMY, "Burgundy", Action.MOVESTO, "Belguim", "France", "aTurnId", "aGameId");
		assertEquals("convoy", "FLEET in Paris convoys ARMY in Burgundy to Belguim", myConvoyOrder.getDescription());

		Order myUnknownOrder = new Order("anOrderId", PieceType.FLEET, "Paris", null, null, PieceType.ARMY, "Burgundy", Action.MOVESTO, "Belguim", "France", "aTurnId", "aGameId");
		assertEquals("convoy", "FLEET in Paris unknown action", myUnknownOrder.getDescription());

	}
	
	@Test
	public void testRequiresSecondaryOrderUnknown() {
		
		Order anOrder = new Order("anOrderId", PieceType.ARMY, "Paris", null, null, null, null, null, null, "France", "aTurnId", "aGameId");
		assertFalse("no order type should not require secondary piece", anOrder.requiresSecondaryOrder());
	}
	
	@Test
	public void testRequiresSecondaryOrderTrue() {
		
		Order anOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.MOVESTO, "Picardy", "France", "aTurnId", "aGameId");
		assertTrue("Support orders require a secondary order", anOrder.requiresSecondaryOrder());
		
		Order anotherOrder = new Order("anOrderId", PieceType.FLEET, "Brest", Action.CONVOYS, null, PieceType.ARMY, "London", Action.MOVESTO, "Picardy", "France", "aTurnId", "aGameId");
		assertTrue("Convoy orders require a secondary order", anotherOrder.requiresSecondaryOrder());

	}
	
	@Test
	public void testRequiresSecondaryOrderFalse() {
		
		Order anOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.HOLDS, null, null, null, null, null, "France", "aTurnId", "aGameId");
		assertFalse("Hold orders don't require a secondary order", anOrder.requiresSecondaryOrder());
		
		Order anotherOrder = new Order("anOrderId", PieceType.FLEET, "Brest", Action.MOVESTO, "English_Channel", null, null, null, null, "France", "aTurnId", "aGameId");
		assertFalse("Convoy orders require a secondary order", anotherOrder.requiresSecondaryOrder());

	}
	
	@Test
	public void testGetEffectiveEndingLocationWithSecondaryOrder() {
		
		Order anOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.MOVESTO, "Picardy", "France", "aTurnId", "aGameId");
		assertEquals("Support order", "Picardy", anOrder.getEffectiveEndingLocationName());
		
		Order anotherOrder = new Order("anOrderId", PieceType.FLEET, "Brest", Action.CONVOYS, null, PieceType.ARMY, "London", Action.MOVESTO, "Picardy", "France", "aTurnId", "aGameId");
		assertEquals("Convoy orders ", "Picardy", anotherOrder.getEffectiveEndingLocationName());

	}

	@Test
	public void testGetEffectiveEndingLocationWithOutSecondaryOrder() {
		
		Order anOrder = new Order("anOrderId", PieceType.ARMY, "Paris", Action.HOLDS, "Paris", null, null, null, null, "France", "aTurnId", "aGameId");
		assertEquals("Hold order", "Paris", anOrder.getEffectiveEndingLocationName());
		
		Order anotherOrder = new Order("anOrderId", PieceType.FLEET, "Brest", Action.MOVESTO, "English_Channel", null, null, null, null, "France", "aTurnId", "aGameId");
		assertEquals("Convoy order", "English_Channel", anotherOrder.getEffectiveEndingLocationName());

	}


}
