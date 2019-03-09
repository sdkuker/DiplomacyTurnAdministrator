package com.sdk.diplomacy.turnadmin.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public class DAOWarehouse {
	
	private Firestore db;
	private LambdaLogger logger;
	
	private GameDAO gameDAO;
	
	public DAOWarehouse(LambdaLogger logger) {
		super();
		this.logger = logger;
	}
	
	public GameDAO getGameDAO() throws ClassNotFoundException, IOException {
		
		if (gameDAO != null) {
			return gameDAO;
		} else {
			if (db == null) {
				initializeFirebase();
			};
			gameDAO = new GameDAO(db, logger);
			return gameDAO;
		}
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

	private String printStackTrace(Exception exception) {
		
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

}
