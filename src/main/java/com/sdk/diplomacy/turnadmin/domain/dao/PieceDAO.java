package com.sdk.diplomacy.turnadmin.domain.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.turnadmin.domain.Piece;

public class PieceDAO {
	
	private Firestore db = null;
	private LambdaLogger logger;
	private String topLevelCollectionName;

	public PieceDAO(Firestore db, LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		this.db = db;
		topLevelCollectionName = aTopLevelCollectionName;
	}

	public List<Piece> getPiecesForTurn(String aTurnID) throws InterruptedException, ExecutionException {
		
		logger.log("Started getting the pieces for turn id: " + aTurnID);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();

		// asynchronously retrieve all games
		Query query = db.collection(topLevelCollectionName).document("pieces").collection("allPieces").whereEqualTo("turnId", aTurnID);
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the get pieces for turn query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				logger.log("Piece ID: " + document.getId());
				Piece aPiece = new Piece(document.getId(), document.getString("owningCountryName"),
						document.getString("nameOfLocationAtBeginningOfTurn"), document.getString("turnId"),
						document.getString("gameId"), Piece.PieceType.valueOf(document.getString("type"))
						);
				aPiece.setMustRetreatAtEndOfTurn(document.getBoolean("mustRetreatAtEndOfTurn"));
				aPiece.setNameOfLocationAtEndOfTurn(document.getString("nameOfLocationAtEndOfTurn"));
				
				listOfPieces.add(aPiece);
			}

		} catch (Exception e) {
			logger.log("error getting the pieces for turn: " + aTurnID + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return listOfPieces;
	}

}
