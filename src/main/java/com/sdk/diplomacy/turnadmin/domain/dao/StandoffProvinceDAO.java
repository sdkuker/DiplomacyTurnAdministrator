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
import com.sdk.diplomacy.turnadmin.conflict.StandoffProvince;

public class StandoffProvinceDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	private String topLevelCollectionName;

	public StandoffProvinceDAO(Firestore db, LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		this.db = db;
		topLevelCollectionName = aTopLevelCollectionName;
	}

	public String insertStandoffProvince(StandoffProvince aProvince) throws InterruptedException, ExecutionException {

		logger.log("Started inserting standoff province with id: " + aProvince.getId());

		Map<String, Object> docData = new HashMap<String, Object>();
		docData.put("provinceName", aProvince.getProvinceName());
		docData.put("turnId", aProvince.getTurnId());
		docData.put("gameId", aProvince.getGameId());

		ApiFuture<DocumentReference> addedDocRef = db.collection(topLevelCollectionName).document("standoffProvinces")
				.collection("allStandoffProvinces").add(docData);

		aProvince.setId(addedDocRef.get().getId());

		return aProvince.getId();
	}

	protected Timestamp deleteStandoffProvince(StandoffProvince aProvince)
			throws InterruptedException, ExecutionException {

		logger.log("Started deleting standoff province with id: " + aProvince.getId());

		ApiFuture<WriteResult> future = db.collection(topLevelCollectionName).document("standoffProvinces")
				.collection("allStandoffProvinces").document(aProvince.getId()).delete();

		WriteResult aWriteResult = future.get();

		logger.log("Delete time: " + aWriteResult.getUpdateTime());

		return aWriteResult.getUpdateTime();
	}

	protected List<StandoffProvince> getStandoffProvincesForTurn(String aTurnID)
			throws InterruptedException, ExecutionException {

		logger.log("Started getting the standoff provinces for turn id: " + aTurnID);

		List<StandoffProvince> listOfStandoffProvinces = new ArrayList<StandoffProvince>();

		// asynchronously retrieve all standoff provinces
		Query query = db.collection(topLevelCollectionName).document("standoffProvinces")
				.collection("allStandoffProvinces").whereEqualTo("turnId", aTurnID);
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the get standoff provinces for turn query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				logger.log("Standoff province ID: " + document.getId());
				StandoffProvince aStandoffProvince = new StandoffProvince(document.getId(),
						document.getString("provinceName"), document.getString("turnId"), document.getString("gameId"));

				listOfStandoffProvinces.add(aStandoffProvince);
			}

		} catch (Exception e) {
			logger.log("error getting the standoff provinces for turn: " + aTurnID + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return listOfStandoffProvinces;
	}

}
