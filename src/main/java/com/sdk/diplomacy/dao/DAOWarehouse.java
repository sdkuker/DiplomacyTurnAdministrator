package com.sdk.diplomacy.dao;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.sdk.diplomacy.turnadmin.domain.dao.GameDAO;
import com.sdk.diplomacy.turnadmin.domain.dao.PieceDAO;
import com.sdk.diplomacy.turnadmin.domain.dao.TurnDAO;

public class DAOWarehouse {

	private Firestore db;
	private LambdaLogger logger;
	private String topLevelCollectionName;

	private GameDAO gameDAO;
	private TurnDAO turnDAO;
	private PieceDAO pieceDAO;

	public DAOWarehouse(LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		topLevelCollectionName = aTopLevelCollectionName;
	}

	public GameDAO getGameDAO() throws ClassNotFoundException, IOException {

		if (gameDAO != null) {
			return gameDAO;
		} else {
			if (db == null) {
				initializeFirebase();
			};
			gameDAO = new GameDAO(db, logger, topLevelCollectionName);
			return gameDAO;
		}
	}

	public TurnDAO getTurnDAO() throws ClassNotFoundException, IOException {

		if (turnDAO != null) {
			return turnDAO;
		} else {
			if (db == null) {
				initializeFirebase();
			};
			turnDAO = new TurnDAO(db, logger, topLevelCollectionName);
			return turnDAO;
		}
	}
	
	public PieceDAO getPieceDAO() throws ClassNotFoundException, IOException {

		if (pieceDAO != null) {
			return pieceDAO;
		} else {
			if (db == null) {
				initializeFirebase();
			};
			pieceDAO = new PieceDAO(db, logger, topLevelCollectionName);
			return pieceDAO;
		}
	}

	public void initializeFirebase() throws IOException, ClassNotFoundException {

		GoogleCredentials myCredentials = null;

		try {
			logger.log("Started initializing Firebase for AWS deployment");
			InputStream serviceAccount = getClass().getClassLoader()
					.getResourceAsStream("resources/steviewarediplomacy-3820ded55c85.json");
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
			} catch (Exception e2) {
				logger.log("unable to get credentials: " + DAOUtilities.printStackTrace(e2));
				throw e2;
			}
		}

		if (myCredentials != null) {

			FirestoreOptions myOptions = FirestoreOptions.newBuilder().setCredentials(myCredentials)
					.setTimestampsInSnapshotsEnabled(true).build();
			db = myOptions.getService();
			logger.log("finished initializing Firebase");
		}
	}

}
