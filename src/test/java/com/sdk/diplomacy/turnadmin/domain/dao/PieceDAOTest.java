package com.sdk.diplomacy.turnadmin.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.Timestamp;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.PropertyManager;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.domain.PieceLocation;
import com.sdk.diplomacy.turnadmin.domain.Turn.Phases;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class PieceDAOTest {

	protected static PieceDAO myPieceDAO;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myPieceDAO = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionName")).getPieceDAO();

		} catch (Exception e) {
			throw (e);
		}
	}

	@Test
	public void testGettingPiecesForATurn() throws Exception {

		String turnId = "testGettingPiecesForATurnID";
		String gameId = "testGettingPiecesForATurnGameID";
		
		PieceLocation myDiplomaticLocation = new PieceLocation("tempLocationId", "tempId", turnId, Phases.DIPLOMATIC, gameId, "Paris", null, true);
		Piece myPiece = new Piece("tempId", "France", gameId, PieceType.ARMY, myDiplomaticLocation);

		myPieceDAO.insertPiece(myPiece);
		
		String myDiplomaticLocationID = myPiece.getPieceLocation().getId();
		
		PieceLocation myGandLUnitsLocation = new PieceLocation("tempLocationId", "tempId", turnId, Phases.GAINING_AND_LOSING_UNITS, gameId, "Paris", null, false);
		myPieceDAO.insertLocation(myGandLUnitsLocation);

		List<Piece> myPieces = myPieceDAO.getPiecesForTurn(gameId, turnId, Phases.DIPLOMATIC);

		assertNotNull("a piece list came back", myPieces);
		assertEquals("right number of pieces came back", 1, myPieces.size());
		Piece returnedPiece = myPieces.get(0);
		assertEquals("game id is right", gameId, returnedPiece.getGameId());
		assertEquals("location id is right", myDiplomaticLocationID, returnedPiece.getPieceLocation().getId());
		assertTrue("must retreat is right", returnedPiece.getMustRetreatAtEndOfTurn());
		assertEquals("location at beginning of turn", "Paris", returnedPiece.getNameOfLocationAtBeginningOfPhase());
		assertNull("location at end of turn", returnedPiece.getNameOfLocationAtEndOfPhase());
		assertEquals("owning country name", "France", returnedPiece.getOwningCountryName());
		assertEquals("type", Piece.PieceType.ARMY, returnedPiece.getType());
		
		myPieceDAO.deleteLocation(myGandLUnitsLocation);
		myPieceDAO.deleteLocation(myDiplomaticLocation);
		myPieceDAO.deletePiece(myPiece);
	}

	@Test
	public void testInsertingUpdatingAndDeletingAPiece() throws Exception {

		String turnId = "testInsertingAndDeletingAPieceTurn";
		String gameId = "testInsertingAndDeletingAPieceGameÏd";
		
		PieceLocation myLocation = new PieceLocation("tempLocationId", "tempId", turnId, Phases.DIPLOMATIC, gameId, "Paris", null, false);
		Piece myPiece = new Piece("tempId", "France", gameId, PieceType.ARMY, myLocation);
		
		String pieceId = myPieceDAO.insertPiece(myPiece);
		
		assertNotNull("new Id was returned", pieceId);
		assertFalse("new id is different from original one", pieceId.contentEquals("tempId"));
		
		assertNotNull("new Id was returned for location", myPiece.getPieceLocation().getId());
		assertFalse("new location id is different from original one", pieceId.contentEquals("tempLocationId"));

		List<Piece> myPieces = myPieceDAO.getPiecesForTurn(gameId, turnId, Phases.DIPLOMATIC);
		assertEquals("right number of pieces came back", 1, myPieces.size());
		assertEquals("piece id is right", pieceId, myPieces.get(0).getId());
		
		myPieces.get(0).setNameOfLocationAtEndOfPhase("Brest");
		
		Timestamp udpateTimestamp = myPieceDAO.update(myPieces.get(0));
		assertNotNull("update timestamp", udpateTimestamp);
		
		List<Piece> myUpdatedPieces = myPieceDAO.getPiecesForTurn(gameId, turnId, Phases.DIPLOMATIC);
		assertEquals("right number of pieces came back on update", 1, myUpdatedPieces.size());
		assertEquals("piece was updated", "Brest", myUpdatedPieces.get(0).getNameOfLocationAtEndOfPhase());

		Timestamp deleteTimestamp = myPieceDAO.deletePiece(myPiece);
		
		assertNotNull("delete timestamp", deleteTimestamp);
		List<Piece> myPiecesAfterDelete = myPieceDAO.getPiecesForTurn(gameId, turnId, Phases.DIPLOMATIC);
		assertEquals("right number of pieces came back after delete", 0, myPiecesAfterDelete.size());

	}
	
	@Test
	public void testGettingAllLocationsForATurnPhase() throws Exception {

		String turnId = "TGALFATPTurnId";
		String turnId2 = "TGALFATPTurnId2";
		String gameId = "TGALFATPGameId";
		
		PieceLocation locationDiplomatic1 = new PieceLocation("tempLocationId1", "tempId", turnId, Phases.DIPLOMATIC, gameId, "Paris", null, true);
		PieceLocation locationDiplomatic2 = new PieceLocation("tempLocationId2", "tempId", turnId, Phases.DIPLOMATIC, gameId, "Gascony", null, true);
		PieceLocation locationDiplomaticTurn2 = new PieceLocation("tempLocationId2", "tempId", turnId2, Phases.DIPLOMATIC, gameId, "Gascony", null, true);
		PieceLocation locationGainAndLosingUnits = new PieceLocation("tempLocationId3", "tempId", turnId, Phases.GAINING_AND_LOSING_UNITS, gameId, "Berlin", null, true);
	
		List<PieceLocation> startingDiplomaticLocations = myPieceDAO.getAllLocations(gameId, turnId, Phases.DIPLOMATIC);
		assertEquals("No existing diplomatic locations turn id", 0, startingDiplomaticLocations.size());
		
		List<PieceLocation> startingDiplomaticLocationsTurn2 = myPieceDAO.getAllLocations(gameId, turnId2, Phases.DIPLOMATIC);
		assertEquals("No existing diplomatic locations turn id 2", 0, startingDiplomaticLocationsTurn2.size());

		List<PieceLocation> startingGLULocations = myPieceDAO.getAllLocations(gameId, turnId, Phases.GAINING_AND_LOSING_UNITS);
		assertEquals("No existing gaining and losing units locations turn id", 0, startingGLULocations.size());

		String locationDiplomatic1Id = myPieceDAO.insertLocation(locationDiplomatic1);
		assertNotNull("ID for diplomatic 1 came back", locationDiplomatic1Id);
		
		String locationDiplomatic2Id = myPieceDAO.insertLocation(locationDiplomatic2);
		assertNotNull("ID for diplomatic 2 came back", locationDiplomatic2Id);
		
		String locationGLUId = myPieceDAO.insertLocation(locationGainAndLosingUnits);
		assertNotNull("ID for gaining and losing units came back", locationGLUId);
		
		String locationDiplomaticTurn2Id = myPieceDAO.insertLocation(locationDiplomaticTurn2);
		assertNotNull("ID for diplomatic turn 2 came back", locationDiplomaticTurn2Id);

		List<PieceLocation> endingDiplomaticLocations = myPieceDAO.getAllLocations(gameId, turnId, Phases.DIPLOMATIC);
		assertEquals("Ending diplomatic locations turn id", 2, endingDiplomaticLocations.size());
		
		List<PieceLocation> endingDiplomaticLocationsTurn2 = myPieceDAO.getAllLocations(gameId, turnId2, Phases.DIPLOMATIC);
		assertEquals("Ending diplomatic locations turn id 2", 1, endingDiplomaticLocationsTurn2.size());

		List<PieceLocation> endingGLULocations = myPieceDAO.getAllLocations(gameId, turnId, Phases.GAINING_AND_LOSING_UNITS);
		assertEquals("Ending gaining and losing units locations turn id", 1, endingGLULocations.size());
		
		myPieceDAO.deleteLocation(locationDiplomatic1);
		myPieceDAO.deleteLocation(locationDiplomatic2);
		myPieceDAO.deleteLocation(locationDiplomaticTurn2);
		myPieceDAO.deleteLocation(locationGainAndLosingUnits);
	
	}
	
	// @Test
	public void tempTroubleshootingTest() throws Exception {

		String turnId = "DoRvEiIYkoR6MhXgnr3G";
		String gameId = "2ljOlJJbB5M75ugjufGp";
			
		myPieceDAO.topLevelCollectionName = "TEST";
		List<PieceLocation> startingDiplomaticLocations = myPieceDAO.getAllLocations(gameId, turnId, Phases.DIPLOMATIC);
		assertTrue("Found some", startingDiplomaticLocations.size() > 0);
			
	}

	
}
