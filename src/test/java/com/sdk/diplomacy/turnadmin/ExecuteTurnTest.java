package com.sdk.diplomacy.turnadmin;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class ExecuteTurnTest {

	protected static DAOWarehouse myDaoWarehouse;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myDaoWarehouse = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionNameForUI"));
			
		} catch (Exception e) {
			throw (e);
		}
	}
	
	//@Test
	public void testExecuteTurn() {
		
		String gameId = "r5RjaDz6XO2IryaNuYES";
		String turnId = "RPpunFavN572V1a1GqCT";
		
		ExecuteTurn myExecuteTurn = new ExecuteTurn(myDaoWarehouse, myTestLambdaLogger);
		myExecuteTurn.executeOrderResolutionPhase(gameId, turnId);
		
		assertTrue(true);
	}
	
	@Test
	public void dummyTest() {
		assertTrue(true);
	}
}
