package com.sdk.diplomacy.turnadmin.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.Timestamp;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.PropertyManager;
import com.sdk.diplomacy.turnadmin.domain.Turn;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class TurnDAOTest {

	protected static TurnDAO myTurnDAO;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myTurnDAO = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionName")).getTurnDAO();

		} catch (Exception e) {
			throw (e);
		}
	}

	@Test
	public void testGettingOpenTurnForGame() throws Exception {

		String myGameID = "Woe9CDOyILIlCq53UHUJ";

		Turn aTurn = myTurnDAO.getOpenTurnForGame(myGameID);

		assertNotNull("a turn came back", aTurn);
		assertEquals("game id is right", myGameID, aTurn.getGameId());
		assertEquals("season is right", Turn.Seasons.FALL, aTurn.getSeason());
		assertEquals("status is right", Turn.Statuss.OPEN, aTurn.getStatus());
		assertEquals("year is right", 1, aTurn.getYear());

	}
	
	@Test
	public void testInsertingUpdatingAndDeletingATurn() throws Exception {
		
		Turn myTurn = new Turn("turnTestIUD", "turnTestIUDGameId", Turn.Seasons.SPRING, 1, Turn.Statuss.OPEN, Turn.Phases.ORDER_RESOLUTION);
		
		String turnId = myTurnDAO.insertTurn(myTurn);
		
		assertNotNull("new Id was returned", turnId);
		assertFalse("new id is different from original one", turnId.contentEquals("turnTestIUD"));
		
		Turn myInsertedTurn = myTurnDAO.getTurn(turnId);
		assertNotNull("turn was inserted", myInsertedTurn);
		assertEquals("turn id is right", turnId, myInsertedTurn.getId());
		assertEquals("phase is right", Turn.Phases.ORDER_RESOLUTION, myInsertedTurn.getPhase());
		
		Timestamp udpateTimestamp = myTurnDAO.updatePhase(myInsertedTurn, Turn.Phases.DIPLOMATIC);
		assertNotNull("update timestamp", udpateTimestamp);
		
		Turn myUpdatedTurn = myTurnDAO.getTurn(turnId);
		assertNotNull("updated turn came back",  myUpdatedTurn);
		assertEquals("turn was updated", Turn.Phases.DIPLOMATIC, myUpdatedTurn.getPhase());

		Timestamp deleteTimestamp = myTurnDAO.deleteTurn(myTurn);
		
		assertNotNull("delete timestamp", deleteTimestamp);
		Turn turnAfterDelete = myTurnDAO.getTurn(turnId);
		assertNull("turn should have been deleted", turnAfterDelete);

	}

}
