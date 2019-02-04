package com.sdk.diplomacy.turnadmin.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.sdk.diplomacy.turnadmin.domain.Game;

public class GameDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	

	public GameDAO(LambdaLogger logger) {
		super();
		this.logger = logger;
	}

	public void initializeFirebase() throws IOException, ClassNotFoundException {

		GoogleCredentials myCredentials = null;
		
		try {
			logger.log("Started initializing Firebase for AWS deployment");
			InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("resources/steviewarediplomacy-3820ded55c85.json");
			logger.log("got the input stream for AWS deployment");
			myCredentials = GoogleCredentials.fromStream(serviceAccount);
			logger.log("got credentials for AWS deployment");
		} catch (Exception e) {
			try {
				logger.log("Started initializing Firebase for local deployment");
				InputStream serviceAccount = getClass().getClassLoader()
						.getResourceAsStream("steviewarediplomacy-3820ded55c85.json");
				logger.log("got the input stream for local deployment");
				myCredentials = GoogleCredentials.fromStream(serviceAccount);
				logger.log("got credentials for local deployment");
			} catch(Exception e2) {
				logger.log("unable to get credentials: " + printStackTrace(e2));
				throw e2;
			}
		}
		
		if (myCredentials !=  null) {
			
			FirestoreOptions myOptions = FirestoreOptions.newBuilder().setCredentials(myCredentials).setTimestampsInSnapshotsEnabled(true).build();
			db = myOptions.getService();
			logger.log("finished initializing Firebase");
		}
	}

	public List<Game> getAllGames() throws IOException, ClassNotFoundException {

		logger.log("Started getting all games");
		
		List<Game> theReturn = new ArrayList<Game>();

		if (db == null) {
			initializeFirebase();
		}

		logger.log("About to get the data from Firebase");
		
		// asynchronously retrieve all users
		ApiFuture<QuerySnapshot> query = db.collection("TEST").document("games").collection("allGames").get();
		// ...
		// query.get() blocks on response
		try {
			logger.log("About to get the query");
			QuerySnapshot querySnapshot = query.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				System.out.println("Game ID: " + document.getId());
				System.out.println("Game Name: " + document.getString("name"));
				theReturn.add(new Game(document.getId(), document.getString("name")));
			}

		} catch (Exception e) {
			logger.log("error getting games" + e);
			System.out.println("error getting games: " + e);
		}

		return theReturn;
	}
	
	private String printStackTrace(Exception exception) {
		
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
		
		
	}
}
