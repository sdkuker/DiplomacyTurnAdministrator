package com.sdk.diplomacy.turnadmin.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
}
