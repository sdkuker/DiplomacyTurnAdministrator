package com.sdk.diplomacy.turnadmin.conflict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.map.Province;

public class TurnResolverIntegrationTest {

	@Test
	public void testMoveHoldSupportWithMoveVictorSingleRegionProvince() {
		
		TurnResolver myResolver = new TurnResolver();
		
		Order successfulMoveOrder = new Order("1", PieceType.ARMY, "Paris", Action.MOVESTO, "Burgundy", null,
				null, null, null, "France", "Turn1", "Game1");
		Order successfulMoveSupportOrder = new Order("2", PieceType.ARMY, "Belguim", Action.SUPPORTS, null, PieceType.ARMY,
				"Paris", Action.MOVESTO, "Burgundy", "France", "Turn1", "Game1");
		Order unSuccessfulMoveOrder = new Order("3", PieceType.ARMY, "Ruhr", Action.MOVESTO, "Burgundy", null,
				null, null, null, "France", "Turn1", "Game1");
		Order unSuccessfulHoldOrder = new Order("4", PieceType.ARMY, "Burgundy", Action.HOLDS, "Burgundy", null,
				null, null, null, "France", "Turn1", "Game1");

		List<Order> listOfOrders = new ArrayList<Order>();
		listOfOrders.add(successfulMoveOrder);
		listOfOrders.add(successfulMoveSupportOrder);
		listOfOrders.add(unSuccessfulMoveOrder);
		listOfOrders.add(unSuccessfulHoldOrder);
		
		Piece armyStartingInParis = new Piece("1", "France", "Paris", "Turn1", "Game1", PieceType.ARMY);
		Piece armyStartingInBelguim = new Piece("2", "France", "Belguim", "Turn1", "Game1", PieceType.ARMY);
		Piece armyStartingInRuhr = new Piece("2", "France", "Ruhr", "Turn1", "Game1", PieceType.ARMY);
		Piece armyStartingInBurgundy = new Piece("4", "France", "Burgundy", "Turn1", "Game1", PieceType.ARMY);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(armyStartingInParis);
		listOfPieces.add(armyStartingInBelguim);
		listOfPieces.add(armyStartingInRuhr);
		listOfPieces.add(armyStartingInBurgundy);

		Set<Province> standoffProvices = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff provice list returned", standoffProvices);
		assertEquals("no standoff provices were returned", 0, standoffProvices.size());
		assertEquals("successful move order", "Burgundy", armyStartingInParis.getNameOfLocationAtEndOfTurn());
		assertEquals("successful move support order", "Belguim", armyStartingInBelguim.getNameOfLocationAtEndOfTurn());
		assertEquals("unsuccessful move order", "Ruhr", armyStartingInRuhr.getNameOfLocationAtEndOfTurn());
		assertNull("unsuccessful hold order", armyStartingInBurgundy.getNameOfLocationAtEndOfTurn());
		assertTrue("unsuccessful hold order must retreat", armyStartingInBurgundy.getMustRetreatAtEndOfTurn());
	}
	
	@Test
	public void testMoveHoldSupportWithMoveVictorMultipleRegionProvince() {
		
		TurnResolver myResolver = new TurnResolver();
		
		Order moveFromMidAtlanticOceanOrder = new Order("1", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO, "Spain_(nc)", null,
				null, null, null, "France", "Turn1", "Game1");
		Order moveFromGulfOfLyon = new Order("2", PieceType.FLEET, "Gulf_of_Lyon", Action.MOVESTO, "Spain_(sc)", null,
				null, null, null, "France", "Turn1", "Game1");
		Order supportForMoveFromGOL = new Order("3", PieceType.FLEET, "Western_Mediterranean", Action.SUPPORTS, null, PieceType.FLEET,
				"Gulf_of_Lyon", Action.MOVESTO, "Spain_(sc)", "France", "Turn1", "Game1");
		Order holdInSpain = new Order("4", PieceType.ARMY, "Spain", Action.HOLDS, "Spain", null,
				null, null, null, "France", "Turn1", "Game1");

		List<Order> listOfOrders = new ArrayList<Order>();
		listOfOrders.add(moveFromMidAtlanticOceanOrder);
		listOfOrders.add(moveFromGulfOfLyon);
		listOfOrders.add(supportForMoveFromGOL);
		listOfOrders.add(holdInSpain);

		Piece fleetStartingInMAO = new Piece("1", "France", "Mid_Atlantic_Ocean", "Turn1", "Game1", PieceType.FLEET);
		Piece fleetStartingInGulf = new Piece("2", "France", "Gulf_of_Lyon", "Turn1", "Game1", PieceType.FLEET);
		Piece fleetStartingInWesternMed = new Piece("3", "France", "Western_Mediterranean", "Turn1", "Game1", PieceType.FLEET);
		Piece armyHoldingInSpain = new Piece("4", "France", "Spain", "Turn1", "Game1", PieceType.ARMY);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(fleetStartingInMAO);
		listOfPieces.add(fleetStartingInGulf);
		listOfPieces.add(fleetStartingInWesternMed);
		listOfPieces.add(armyHoldingInSpain);

		Set<Province> standoffProvices = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff provice list returned", standoffProvices);
		assertEquals("no provices were returned", 0, standoffProvices.size());
		assertEquals("move from MAO fails", "Mid_Atlantic_Ocean", fleetStartingInMAO.getNameOfLocationAtEndOfTurn());
		assertEquals("move from Gulf succeeds", "Spain_(sc)", fleetStartingInGulf.getNameOfLocationAtEndOfTurn());
		assertEquals("support fleet ends where it started", "Western_Mediterranean", fleetStartingInWesternMed.getNameOfLocationAtEndOfTurn());
		assertNull("unsuccessful hold order", armyHoldingInSpain.getNameOfLocationAtEndOfTurn());
		assertTrue("unsuccessful hold order must retreat", armyHoldingInSpain.getMustRetreatAtEndOfTurn());

	}

	
	@Test
	public void testStandoffSingleRegionProvince() {
		
		TurnResolver myResolver = new TurnResolver();
		
		Order moveFromParisOrder = new Order("1", PieceType.ARMY, "Paris", Action.MOVESTO, "Burgundy", null,
				null, null, null, "France", "Turn1", "Game1");
		Order moveFromRuhrOrder = new Order("2", PieceType.ARMY, "Ruhr", Action.MOVESTO, "Burgundy", null,
				null, null, null, "France", "Turn1", "Game1");

		List<Order> listOfOrders = new ArrayList<Order>();
		listOfOrders.add(moveFromParisOrder);
		listOfOrders.add(moveFromRuhrOrder);

		Piece armyStartingInParis = new Piece("1", "France", "Paris", "Turn1", "Game1", PieceType.ARMY);
		Piece armyStartingInRuhr = new Piece("2", "France", "Ruhr", "Turn1", "Game1", PieceType.ARMY);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(armyStartingInParis);
		listOfPieces.add(armyStartingInRuhr);

		Set<Province> standoffProvices = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff provice list returned", standoffProvices);
		assertEquals("provices were returned", 1, standoffProvices.size());
		assertEquals("correct provice was returned", "Burgundy", ((Province)standoffProvices.toArray()[0]).getName());
		assertEquals("move from paris fails", "Paris", armyStartingInParis.getNameOfLocationAtEndOfTurn());
		assertEquals("move from Ruhr fails", "Ruhr", armyStartingInRuhr.getNameOfLocationAtEndOfTurn());
	}
	
	@Test
	public void testStandoffMultiRegionProvince() {
		
		TurnResolver myResolver = new TurnResolver();
		
		Order moveFromMidAtlanticOceanOrder = new Order("1", PieceType.FLEET, "Mid_Atlantic_Ocean", Action.MOVESTO, "Spain_(nc)", null,
				null, null, null, "France", "Turn1", "Game1");
		Order moveFromGulfOfLyon = new Order("2", PieceType.FLEET, "Gulf_of_Lyon", Action.MOVESTO, "Spain_(sc)", null,
				null, null, null, "France", "Turn1", "Game1");

		List<Order> listOfOrders = new ArrayList<Order>();
		listOfOrders.add(moveFromMidAtlanticOceanOrder);
		listOfOrders.add(moveFromGulfOfLyon);

		Piece fleetStartingInMAO = new Piece("1", "France", "Mid_Atlantic_Ocean", "Turn1", "Game1", PieceType.FLEET);
		Piece fleetStartingInGulf = new Piece("2", "France", "Gulf_of_Lyon", "Turn1", "Game1", PieceType.FLEET);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(fleetStartingInMAO);
		listOfPieces.add(fleetStartingInGulf);

		Set<Province> standoffProvices = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff provice list returned", standoffProvices);
		assertEquals("provices were returned", 1, standoffProvices.size());
		assertEquals("correct provice was returned", "Spain", ((Province)standoffProvices.toArray()[0]).getName());
		assertEquals("move from MAO fails", "Mid_Atlantic_Ocean", fleetStartingInMAO.getNameOfLocationAtEndOfTurn());
		assertEquals("move from Gulf fails", "Gulf_of_Lyon", fleetStartingInGulf.getNameOfLocationAtEndOfTurn());
	}

	
}
