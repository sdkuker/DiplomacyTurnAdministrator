package com.sdk.diplomacy.turnadmin.domain.dao;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.turnadmin.domain.Turn;

public class TurnDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	private String topLevelCollectionName;

	public TurnDAO(Firestore db, LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		this.db = db;
		topLevelCollectionName = aTopLevelCollectionName;
	}

	public Turn getOpenTurnForGame(String aGameId) throws InterruptedException, ExecutionException {

		logger.log("Started getting the open turn for game id: " + aGameId);

		Turn theReturn = null;

		// asynchronously retrieve all games
		Query query = db.collection(topLevelCollectionName).document("turns").collection("allTurns").whereEqualTo("gameId", aGameId)
				.whereEqualTo("status", "OPEN");
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the get open turn for game query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				System.out.println("Game ID: " + document.getId());
				System.out.println("Game Name: " + document.getString("name"));
				theReturn = new Turn(document.getId(), document.getString("gameId"),
						Turn.Seasons.valueOf(document.getString("season")), (Integer) document.get("year"),
						Turn.Statuss.valueOf(document.getString("status")));
			}

		} catch (Exception e) {
			logger.log("error getting the open turn for game: " + aGameId + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return theReturn;
	}

}
