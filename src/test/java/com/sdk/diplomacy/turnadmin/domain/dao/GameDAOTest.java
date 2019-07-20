package com.sdk.diplomacy.turnadmin.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.PropertyManager;
import com.sdk.diplomacy.turnadmin.domain.Game;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class GameDAOTest {

	protected static GameDAO myGameDAO;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myGameDAO = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionName")).getGameDAO();
			
		} catch (Exception e) {
			throw (e);
		}
	}

	@Test
	public void testGettingGames() throws Exception {
		
		List<Game> allGames = myGameDAO.getAllGames();
		
		assertNotNull("a list of games came back", allGames);
		assertEquals("right number of games in the list", 1, allGames.size());
		assertEquals("right game is in the list", "Game1", allGames.get(0).getName());

	}
	
	@Test
	public void testGetGame() throws Exception {
		
		String gameID = "Woe9CDOyILIlCq53UHUJ";
		Game theGame = myGameDAO.getGame(gameID);
		
		assertNotNull("the game was found", theGame);
		assertEquals("the id is right", gameID, theGame.getId());
		
		theGame = myGameDAO.getGame("George");
		assertNull("the game should not be found", theGame);

	}

}
