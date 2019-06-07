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
import com.sdk.diplomacy.turnadmin.conflict.OrderResolutionResults;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class OrderResolutionResultsDAOTest {

	protected static OrderResolutionResultsDAO myDAO;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myDAO = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionName"))
							.getOrderResolutionResultsDAO();

		} catch (Exception e) {
			throw (e);
		}
	}
	
	@Test
	public void testInsertingAndDeletingAnOrderResolutionResult() throws Exception {

		String turnId = "testInsertingAndDeletingAnOrderResolutionResult";
		String orderId = "testOrderId";
		String gameId = "testGameId";

		OrderResolutionResults myResults = new OrderResolutionResults(orderId, turnId, gameId);
		myResults.setId("tempId");

		String resultsId = myDAO.insertOrderResolutionResults(myResults);

		assertNotNull("new Id was returned", resultsId);
		assertFalse("new id is different from original one", resultsId.contentEquals("tempId"));

		List<OrderResolutionResults> myResultList = myDAO.getOrderResolutionResultsForTurn(turnId);
		assertEquals("right number of provinces came back", 1, myResultList.size());
		assertEquals("province id is right", resultsId, myResultList.get(0).getId());

		Timestamp deleteTimestamp = myDAO.deleteOrderResolutionResults(myResults);

		assertNotNull("delete timestamp", deleteTimestamp);
		List<OrderResolutionResults> myResultsAfterDelete = myDAO.getOrderResolutionResultsForTurn(turnId);
		assertEquals("right number of results came back after delete", 0, myResultsAfterDelete.size());

	}


}
