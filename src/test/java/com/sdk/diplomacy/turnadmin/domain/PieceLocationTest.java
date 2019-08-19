package com.sdk.diplomacy.turnadmin.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.sdk.diplomacy.turnadmin.domain.Turn.Phases;

public class PieceLocationTest {

	@Test
	public void testCloneForNextPhase() {
		String id = "1";
		String pieceId = "2";
		String turnId = "3";
		Phases turnPhase = Phases.DIPLOMATIC;
		String gameId = "4";
		String nameOfLocationAtBeginningOfPhase = "Tom";
		String nameOfLocationAtEndOfPhase = "Dick";
		boolean mustRetreatAtEndOfTurn = true;

		PieceLocation originalLocation = new PieceLocation(id, pieceId, turnId, turnPhase, gameId,
				nameOfLocationAtBeginningOfPhase, nameOfLocationAtEndOfPhase, mustRetreatAtEndOfTurn);
		
		PieceLocation clonedLocation = originalLocation.cloneForNextPhase();
		
		assertNotNull("there is a cloned location", clonedLocation);
		assertFalse("the original and cloned are different object", originalLocation == clonedLocation);
		assertNull("cloned id is null", clonedLocation.getId());
		assertEquals("piece id", originalLocation.getPieceId(), clonedLocation.getPieceId());
		assertEquals("turn id", originalLocation.getTurnId(), clonedLocation.getTurnId());
		assertEquals("turn phase", originalLocation.getTurnPhase().nextPhase(), clonedLocation.getTurnPhase());
		assertEquals("game id", originalLocation.getGameId(), clonedLocation.getGameId());
		assertEquals("beginning location", originalLocation.getNameOfLocationAtEndOfPhase(), clonedLocation.getNameOfLocationAtBeginningOfPhase());
		assertNull("ending location", clonedLocation.getNameOfLocationAtEndOfPhase());
		assertFalse("must retreat", clonedLocation.isMustRetreatAtEndOfTurn());
	}
}
