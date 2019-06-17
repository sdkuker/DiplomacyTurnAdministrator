package com.sdk.diplomacy.turnadmin.conflict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.PieceLocation;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.domain.Turn.Phases;

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
		
		PieceLocation myPieceLocation = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Paris", null, true);
		Piece armyStartingInParis = new Piece("1", "France", "Game1", PieceType.ARMY, myPieceLocation);
		PieceLocation myPieceLocation2 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Belguim", null, true);
		Piece armyStartingInBelguim = new Piece("2", "France", "Game1", PieceType.ARMY, myPieceLocation2);
		PieceLocation myPieceLocation3 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Ruhr", null, true);
		Piece armyStartingInRuhr = new Piece("3", "France", "Game1", PieceType.ARMY, myPieceLocation3);
		PieceLocation myPieceLocation4 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Burgundy", null, true);
		Piece armyStartingInBurgundy = new Piece("4", "France", "Game1", PieceType.ARMY, myPieceLocation4);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(armyStartingInParis);
		listOfPieces.add(armyStartingInBelguim);
		listOfPieces.add(armyStartingInRuhr);
		listOfPieces.add(armyStartingInBurgundy);

		ConflictResolutionResults myResults = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff provice list returned", myResults.getStandoffProvinces());
		assertEquals("no standoff provices were returned", 0, myResults.getStandoffProvinces().size());
		assertNotNull("turn results map returned", myResults.getOrderResolutionResults());
		assertEquals("right number of order results were returned", 4, myResults.getOrderResolutionResults().size());
		assertEquals("successful move order", "Burgundy", armyStartingInParis.getNameOfLocationAtEndOfPhase());
		assertEquals("successful move support order", "Belguim", armyStartingInBelguim.getNameOfLocationAtEndOfPhase());
		assertEquals("unsuccessful move order", "Ruhr", armyStartingInRuhr.getNameOfLocationAtEndOfPhase());
		assertNull("unsuccessful hold order", armyStartingInBurgundy.getNameOfLocationAtEndOfPhase());
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

		PieceLocation myPieceLocation = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Mid_Atlantic_Ocean", null, true);
		Piece fleetStartingInMAO = new Piece("1", "France", "Game1", PieceType.FLEET, myPieceLocation);
		PieceLocation myPieceLocation2 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Gulf_of_Lyon", null, true);
		Piece fleetStartingInGulf = new Piece("2", "France", "Game1", PieceType.FLEET, myPieceLocation2);
		PieceLocation myPieceLocation3 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Western_Mediterranean", null, true);
		Piece fleetStartingInWesternMed = new Piece("3", "France", "Game1", PieceType.FLEET, myPieceLocation3);
		PieceLocation myPieceLocation4 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Spain", null, true);
		Piece armyHoldingInSpain = new Piece("4", "France",  "Game1", PieceType.ARMY, myPieceLocation4);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(fleetStartingInMAO);
		listOfPieces.add(fleetStartingInGulf);
		listOfPieces.add(fleetStartingInWesternMed);
		listOfPieces.add(armyHoldingInSpain);

		ConflictResolutionResults myResults = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff province list returned", myResults.getStandoffProvinces());
		assertEquals("no provices were returned", 0, myResults.getStandoffProvinces().size());
		assertNotNull("turn results map returned", myResults.getOrderResolutionResults());
		assertEquals("right number of order results were returned", 4, myResults.getOrderResolutionResults().size());
		assertEquals("move from MAO fails", "Mid_Atlantic_Ocean", fleetStartingInMAO.getNameOfLocationAtEndOfPhase());
		assertEquals("move from Gulf succeeds", "Spain_(sc)", fleetStartingInGulf.getNameOfLocationAtEndOfPhase());
		assertEquals("support fleet ends where it started", "Western_Mediterranean", fleetStartingInWesternMed.getNameOfLocationAtEndOfPhase());
		assertNull("unsuccessful hold order", armyHoldingInSpain.getNameOfLocationAtEndOfPhase());
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

		PieceLocation myPieceLocation = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Paris", null, true);
		Piece armyStartingInParis = new Piece("1", "France", "Game1", PieceType.ARMY, myPieceLocation);
		PieceLocation myPieceLocation2 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Ruhr", null, true);
		Piece armyStartingInRuhr = new Piece("2", "France", "Game1", PieceType.ARMY, myPieceLocation2);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(armyStartingInParis);
		listOfPieces.add(armyStartingInRuhr);

		ConflictResolutionResults myResults = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff provice list returned", myResults.getStandoffProvinces());
		assertEquals("provices were returned", 1, myResults.getStandoffProvinces().size());
		assertNotNull("turn results map returned", myResults.getOrderResolutionResults());
		assertEquals("right number of order results were returned", 2, myResults.getOrderResolutionResults().size());
		assertEquals("correct province was returned", "Burgundy", ((StandoffProvince) myResults.getStandoffProvinces().toArray()[0]).getProvinceName());
		assertEquals("move from paris fails", "Paris", armyStartingInParis.getNameOfLocationAtEndOfPhase());
		assertEquals("move from Ruhr fails", "Ruhr", armyStartingInRuhr.getNameOfLocationAtEndOfPhase());
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

		PieceLocation myPieceLocation = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Mid_Atlantic_Ocean", null, true);
		Piece fleetStartingInMAO = new Piece("1", "France", "Game1", PieceType.FLEET, myPieceLocation);
		PieceLocation myPieceLocation2 = new PieceLocation("1L", "tempId", "Turn1", Phases.DIPLOMATIC, "Game1", "Gulf_of_Lyon", null, true);
		Piece fleetStartingInGulf = new Piece("2", "France", "Game1", PieceType.FLEET, myPieceLocation2);

		List<Piece> listOfPieces = new ArrayList<Piece>();
		listOfPieces.add(fleetStartingInMAO);
		listOfPieces.add(fleetStartingInGulf);

		ConflictResolutionResults myResults = myResolver.resolveConflict(listOfOrders, listOfPieces);
		
		assertNotNull("standoff provice list returned", myResults.getStandoffProvinces());
		assertEquals("provices were returned", 1, myResults.getStandoffProvinces().size());
		assertNotNull("turn results map returned", myResults.getOrderResolutionResults());
		assertEquals("right number of order results were returned", 2, myResults.getOrderResolutionResults().size());
		assertEquals("correct provice was returned", "Spain", ((StandoffProvince) myResults.getStandoffProvinces().toArray()[0]).getProvinceName());
		assertEquals("move from MAO fails", "Mid_Atlantic_Ocean", fleetStartingInMAO.getNameOfLocationAtEndOfPhase());
		assertEquals("move from Gulf fails", "Gulf_of_Lyon", fleetStartingInGulf.getNameOfLocationAtEndOfPhase());
	}

	
}
