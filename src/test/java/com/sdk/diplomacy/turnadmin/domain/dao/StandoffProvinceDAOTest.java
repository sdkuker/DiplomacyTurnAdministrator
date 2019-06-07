package com.sdk.diplomacy.turnadmin.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.Timestamp;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.PropertyManager;
import com.sdk.diplomacy.turnadmin.conflict.StandoffProvince;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class StandoffProvinceDAOTest {

	protected static StandoffProvinceDAO myStandoffProvinceDAO;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myStandoffProvinceDAO = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionName")).getStandoffProvinceDAO();

		} catch (Exception e) {
			throw (e);
		}
	}

	@Test
	public void testInsertingAndDeletingAStandoffProvince() throws Exception {

		String turnId = "testInsertingAndDeletingAStandoffProvince";

		StandoffProvince myStandoffProvince = new StandoffProvince("tempId", "France", turnId,
				"testInsertingAndDeletingAPieceGameÏ");

		String standoffProvinceId = myStandoffProvinceDAO.insertStandoffProvince(myStandoffProvince);

		assertNotNull("new Id was returned", standoffProvinceId);
		assertFalse("new id is different from original one", standoffProvinceId.contentEquals("tempId"));

		List<StandoffProvince> myStandoffProvinces = myStandoffProvinceDAO.getStandoffProvincesForTurn(turnId);
		assertEquals("right number of provinces came back", 1, myStandoffProvinces.size());
		assertEquals("province id is right", standoffProvinceId, myStandoffProvinces.get(0).getId());

		Timestamp deleteTimestamp = myStandoffProvinceDAO.deleteStandoffProvince(myStandoffProvince);

		assertNotNull("delete timestamp", deleteTimestamp);
		List<StandoffProvince> myProvincesAfterDelete = myStandoffProvinceDAO.getStandoffProvincesForTurn(turnId);
		assertEquals("right number of provinces came back after delete", 0, myProvincesAfterDelete.size());

	}

}
