package com.sdk.diplomacy.turnadmin.domain.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.turnadmin.domain.Turn;

public class TurnDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	private String topLevelCollectionName;
	private String documentName = "turns";
	private String collectionName = "allTurns";

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
		Query query = db.collection(topLevelCollectionName).document(documentName).collection(collectionName).whereEqualTo("gameId", aGameId)
				.whereEqualTo("status", "OPEN");
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the get open turn for turn query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				logger.log("Turn ID: " + document.getId());
				theReturn = new Turn(document.getId(), document.getString("gameId"),
						Turn.Seasons.valueOf(document.getString("season")), (Long) document.get("year"),
						Turn.Statuss.valueOf(document.getString("status")), Turn.Phases.valueOf(document.getString("phase")));
			}

		} catch (Exception e) {
			logger.log("error getting the open turn for game: " + aGameId + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return theReturn;
	}
	
	public Turn getTurn(String aTurnId) throws InterruptedException, ExecutionException {

		logger.log("Started getting the turn for ID: " + aTurnId);

		Turn theReturn = null;

		DocumentReference docRef = db.collection(topLevelCollectionName).document(documentName).collection(collectionName)
				.document(aTurnId);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot aDocument = future.get();
		
		if (aDocument.exists()) {
			theReturn = new Turn(aDocument.getId(), aDocument.getString("gameId"),
					Turn.Seasons.valueOf(aDocument.getString("season")), (Long) aDocument.get("year"),
					Turn.Statuss.valueOf(aDocument.getString("status")), Turn.Phases.valueOf(aDocument.getString("phase")));
		}
		
		return theReturn;
	}

	
	public Timestamp updatePhase(Turn aTurn, Turn.Phases aPhase) throws InterruptedException, ExecutionException {

		logger.log("Started updating turn phase for turn with id: " + aTurn.getId());

		DocumentReference docRef = db.collection(topLevelCollectionName).document(documentName).collection(collectionName)
				.document(aTurn.getId());
		ApiFuture<WriteResult> future = docRef.update("phase", aPhase.toString());
		WriteResult aWriteResult = future.get();
		
		logger.log("Update time: " + aWriteResult.getUpdateTime());

		return aWriteResult.getUpdateTime();
	}

	protected String insertTurn(Turn aTurn) throws InterruptedException, ExecutionException {

		logger.log("Started inserting turn with id: " + aTurn.getId());

		Map<String, Object> docData = new HashMap<String, Object>();
		docData.put("gameId", aTurn.getGameId());
		docData.put("season", aTurn.getSeason().toString());
		docData.put("year", aTurn.getYear());
		docData.put("status", aTurn.getStatus().toString());
		docData.put("phase", aTurn.getPhase().toString());

		ApiFuture<DocumentReference> addedDocRef = db.collection(topLevelCollectionName).document(documentName).collection(collectionName)
				.add(docData);
		
		aTurn.setId(addedDocRef.get().getId());
		
		return aTurn.getId();
	}
	
	protected Timestamp deleteTurn(Turn aTurn) throws InterruptedException, ExecutionException {

		logger.log("Started deleting turn with id: " + aTurn.getId());
		
		ApiFuture<WriteResult> future = db.collection(topLevelCollectionName).document(documentName).collection(collectionName)
				.document(aTurn.getId()).delete();
		
		WriteResult aWriteResult = future.get();
		
		logger.log("Delete time: " + aWriteResult.getUpdateTime());

		return aWriteResult.getUpdateTime();
	}

}
