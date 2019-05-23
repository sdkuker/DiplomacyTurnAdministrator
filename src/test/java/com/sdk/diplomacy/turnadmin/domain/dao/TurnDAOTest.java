package com.sdk.diplomacy.turnadmin.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

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
}
