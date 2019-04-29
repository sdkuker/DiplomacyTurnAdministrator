package com.sdk.diplomacy.turnadmin.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.sdk.diplomacy.turnadmin.domain.Game;

public class GameDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	

	public GameDAO(Firestore db, LambdaLogger logger) {
		super();
		this.logger = logger;
		this.db = db;
	}


	public List<Game> getAllGames() throws InterruptedException, ExecutionException {

		logger.log("Started getting all games");
	
		List<Game> theReturn = new ArrayList<Game>();

		logger.log("About to get games from Firebase");
		
		// asynchronously retrieve all games
		ApiFuture<QuerySnapshot> query = db.collection("TEST").document("games").collection("allGames").get();
		// ...
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
			logger.log("error getting games" + printStackTrace(e));
			throw e;
		}

		return theReturn;
	}
	
	private String printStackTrace(Exception exception) {
		
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
		
		
	}
}
