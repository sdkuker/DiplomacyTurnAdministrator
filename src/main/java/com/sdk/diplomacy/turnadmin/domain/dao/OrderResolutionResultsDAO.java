package com.sdk.diplomacy.turnadmin.domain.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.turnadmin.conflict.OrderResolutionResults;

public class OrderResolutionResultsDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	private String topLevelCollectionName;
	private String documentName = "orderResolutionResults";
	private String collectionName = "allOrderResolutionResults";

	public OrderResolutionResultsDAO(Firestore db, LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		this.db = db;
		topLevelCollectionName = aTopLevelCollectionName;
	}
	
	public String insertOrderResolutionResults(OrderResolutionResults aResults) throws InterruptedException, ExecutionException {

		logger.log("Started inserting order resolution results with id: " + aResults.getId());

		Map<String, Object> docData = new HashMap<String, Object>();
		docData.put("orderId", aResults.getOrderId());
		docData.put("turnId", aResults.getTurnId());
		docData.put("gameId", aResults.getGameId());
		docData.put("orderExecutedSuccessfully", aResults.wasOrderExecutedSuccessfully());
		docData.put("executionDescription", aResults.getExecutionDescription());
		docData.put("isValidOrder", aResults.isValidOrder());
		docData.put("orderResolutionCompleted", aResults.wasOrderExecutedSuccessfully());
		docData.put("executionFailedDueToStandoff", aResults.isExecutionFailedDueToStandoff());

		ApiFuture<DocumentReference> addedDocRef = db.collection(topLevelCollectionName).document(documentName)
				.collection(collectionName).add(docData);

		aResults.setId(addedDocRef.get().getId());

		return aResults.getId();
	}

	protected Timestamp deleteOrderResolutionResults(OrderResolutionResults aResults)
			throws InterruptedException, ExecutionException {

		logger.log("Started deleting order execution results with id: " + aResults.getId());

		ApiFuture<WriteResult> future = db.collection(topLevelCollectionName).document(documentName)
				.collection(collectionName).document(aResults.getId()).delete();

		WriteResult aWriteResult = future.get();

		logger.log("Delete time: " + aWriteResult.getUpdateTime());

		return aWriteResult.getUpdateTime();
	}

	protected List<OrderResolutionResults> getOrderResolutionResultsForTurn(String aTurnID)
			throws InterruptedException, ExecutionException {

		logger.log("Started getting the order execution results for turn id: " + aTurnID);

		List<OrderResolutionResults> listOfResults = new ArrayList<OrderResolutionResults>();

		// asynchronously retrieve all results
		Query query = db.collection(topLevelCollectionName).document(documentName)
				.collection(collectionName).whereEqualTo("turnId", aTurnID);
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the get order resolution results for turn query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				logger.log("Order resolution results ID: " + document.getId());
				OrderResolutionResults aResults = new OrderResolutionResults(document.getString("orderId"),
						document.getString("turnId"), document.getString("gameId"));
				aResults.setId(document.getId());
				aResults.setOrderExecutedSuccessfully(document.getBoolean("orderExecutedSuccessfully"));
				aResults.setExecutionDescription(document.getString("executionDescription"));
				aResults.setIsValidOrder(document.getBoolean("isValidOrder"));
				aResults.setOrderResolutionCompleted(document.getBoolean("orderResolutionCompleted"));
				aResults.setExecutionFailedDueToStandoff(document.getBoolean("executionFailedDueToStandoff"));

				listOfResults.add(aResults);
			}

		} catch (Exception e) {
			logger.log("error getting the order resolution results for turn: " + aTurnID + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return listOfResults;
	}

}
