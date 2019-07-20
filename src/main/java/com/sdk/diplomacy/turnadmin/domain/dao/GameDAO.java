package com.sdk.diplomacy.turnadmin.domain.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.turnadmin.domain.Game;
import com.sdk.diplomacy.turnadmin.domain.Turn;

public class GameDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	private String topLevelCollectionName;
	private String documentName = "games";
	private String collectionName = "allGames";
	
	public GameDAO(Firestore db, LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		this.db = db;
		topLevelCollectionName = aTopLevelCollectionName;
	}

	public List<Game> getAllGames() throws InterruptedException, ExecutionException {
	
		List<Game> theReturn = new ArrayList<Game>();

		logger.log("About to get games from Firebase");
		
		// asynchronously retrieve all games
		ApiFuture<QuerySnapshot> query = db.collection(topLevelCollectionName).document(documentName).collection(collectionName).get();
		// query.get() blocks on response
		try {
			logger.log("About to get the all games query");
			QuerySnapshot querySnapshot = query.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the all games query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				System.out.println("Game ID: " + document.getId());
				System.out.println("Game Name: " + document.getString("name"));
				theReturn.add(new Game(document.getId(), document.getString("name")));
			}

		} catch (Exception e) {
			logger.log("error getting games" + DAOUtilities.printStackTrace(e));
			throw e;
		}

		return theReturn;
	}
	
	public Game getGame(String aGameId) throws InterruptedException, ExecutionException {

		logger.log("Started getting the game for ID: " + aGameId);

		Game theReturn = null;

		DocumentReference docRef = db.collection(topLevelCollectionName).document(documentName).collection(collectionName)
				.document(aGameId);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot aDocument = future.get();
		
		if (aDocument.exists()) {
			theReturn = new Game(aDocument.getId(), aDocument.getString("name"));
		}
		
		return theReturn;
	}
	
}
