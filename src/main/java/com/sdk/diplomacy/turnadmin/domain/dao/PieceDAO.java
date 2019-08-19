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
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.PieceLocation;
import com.sdk.diplomacy.turnadmin.domain.Turn.Phases;

public class PieceDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	protected String topLevelCollectionName;
	private String documentName = "pieces";
	private String collectionName = "allPieces";
	private String locationDocumentName = "pieceLocations";
	private String locationCollectionName = "allPieceLocations";

	public PieceDAO(Firestore db, LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		this.db = db;
		topLevelCollectionName = aTopLevelCollectionName;
	}

	public List<Piece> getPiecesForTurn(String aGameId, String aTurnId, Phases aTurnPhase)
			throws InterruptedException, ExecutionException {

		logger.log("Started getting the pieces for game id: " + aGameId + " turn id: " + aTurnId + " and phase: " + aTurnPhase);
		
		List<Piece> listOfPieces = new ArrayList<Piece>();

		List<PieceLocation> myLocations = getAllLocations(aGameId, aTurnId, determinePersistentPhase(aTurnPhase));
		
		for (PieceLocation aLocation : myLocations) {
			
			DocumentReference docRef = db.collection(topLevelCollectionName).document(documentName)
					.collection(collectionName).document(aLocation.getPieceId());
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot aDocument = future.get();

			if (aDocument.exists()) {
				Piece myPiece = new Piece(aDocument.getId(), aDocument.getString("owningCountryName"),
						aDocument.getString("gameId"), Piece.PieceType.valueOf(aDocument.getString("type")), null);
				myPiece.setPieceLocation(aLocation);
				listOfPieces.add(myPiece);
			}

		}
		
		return listOfPieces;
	}

	public Timestamp update(Piece aPiece) throws InterruptedException, ExecutionException {

		// the only thing you can update is the location
		
		return updateLocation(aPiece.getPieceLocation());
	}

	protected String insertPiece(Piece aPiece) throws InterruptedException, ExecutionException {

		logger.log("Started inserting piece with id: " + aPiece.getId());
		
		DocumentReference addedDocRefForPiece = db.collection(topLevelCollectionName).document(documentName)
				.collection(collectionName).document();

		aPiece.getPieceLocation().setPieceId(addedDocRefForPiece.getId());
		insertLocation(aPiece.getPieceLocation());

		Map<String, Object> docData = new HashMap<String, Object>();
		docData.put("owningCountryName", aPiece.getOwningCountryName());
		docData.put("gameId", aPiece.getGameId());
		docData.put("type", aPiece.getType().toString());

		ApiFuture<WriteResult> writeResult = addedDocRefForPiece.set(docData);

		aPiece.setId(addedDocRefForPiece.getId());

		return aPiece.getId();
	}

	protected Timestamp deletePiece(Piece aPiece) throws InterruptedException, ExecutionException {

		logger.log("Started deleting piece with id: " + aPiece.getId());
		
		List<PieceLocation> allLocationsForPiece = getAllLocationsForPiece(aPiece.getId());
		
		for (PieceLocation aLocation : allLocationsForPiece) {
			deleteLocation(aLocation);
		}

		ApiFuture<WriteResult> future = db.collection(topLevelCollectionName).document(documentName)
				.collection(collectionName).document(aPiece.getId()).delete();

		WriteResult aWriteResult = future.get();

		logger.log("Delete time: " + aWriteResult.getUpdateTime());

		return aWriteResult.getUpdateTime();
	}

	protected Phases determinePersistentPhase(Phases inputPhase) {

		/*
		 * locations don't change in the ORDER_WRITING phase so they're not saved. If
		 * that's the inputPhase, return the locations from DIPLOMATIC instead.
		 */

		Phases persistentPhase = inputPhase;
		
		switch(inputPhase) {
		case ORDER_WRITING:
			persistentPhase = Phases.DIPLOMATIC;
			break;
		case ORDER_RESOLUTION:
			persistentPhase = Phases.DIPLOMATIC;
			break;
		default:
			break;
		}

		return persistentPhase;
	}

	public List<PieceLocation> getAllLocations(String aGameId, String aTurnId, Phases aTurnPhase)
			throws InterruptedException, ExecutionException {

		logger.log("Started getting the locations for game id: " + aGameId + " and turn id: " + aTurnId + " for phase: " + aTurnPhase);

		List<PieceLocation> desiredLocations = new ArrayList<PieceLocation>();

		// asynchronously retrieve all locations
		Query query = db.collection(topLevelCollectionName).document(locationDocumentName)
				.collection(locationCollectionName).whereEqualTo("gameId", aGameId).whereEqualTo("turnId", aTurnId)
				.whereEqualTo("turnPhase", determinePersistentPhase(aTurnPhase).toString());
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log(
					"Number of documents in the get locations for game, turn & phase query snapshot:" + documents.size());
			if (documents.size() > 0) {
				querySnapshot.forEach((document) -> {
					PieceLocation aDesiredLocation = new PieceLocation(document.getId(), document.getString("pieceId"), document.getString("turnId"),
							Phases.valueOf(document.getString("turnPhase")), document.getString("gameId"),
							document.getString("nameOfLocationAtBeginningOfPhase"),
							document.getString("nameOfLocationAtEndOfPhase"),
							document.getBoolean("mustRetreatAtEndOfTurn"));
					desiredLocations.add(aDesiredLocation);
				});
			}

		} catch (Exception e) {
			logger.log("error getting the locations for game id: " + aGameId + " for turn id: " + aTurnId + " for phase: " + aTurnPhase + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return desiredLocations;
	}
	
	public PieceLocation getLocationForPiece(String aPieceId, Phases aTurnPhase)
			throws InterruptedException, ExecutionException {

		logger.log("Started getting the location for piece id: " + aPieceId + " for phase: " + aTurnPhase);

		PieceLocation desiredLocation = null;

		// asynchronously retrieve all locations
		Query query = db.collection(topLevelCollectionName).document(locationDocumentName)
				.collection(locationCollectionName).whereEqualTo("pieceId", aPieceId)
				.whereEqualTo("turnPhase", determinePersistentPhase(aTurnPhase).toString());
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log(
					"Number of documents in the get get locations for turn & phase query snapshot:" + documents.size());
			// should be zero or 1
			if (documents.size() == 1) {
				QueryDocumentSnapshot document = documents.get(0);
				desiredLocation = new PieceLocation(document.getId(), document.getString("pieceId"), document.getString("turnId"),
						Phases.valueOf(document.getString("turnPhase")), document.getString("gameId"),
						document.getString("nameOfLocationAtBeginningOfPhase"),
						document.getString("nameOfLocationAtEndOfPhase"),
						document.getBoolean("mustRetreatAtEndOfTurn"));
			}

		} catch (Exception e) {
			logger.log("error getting the location for piece id: " + aPieceId + " for phase: " + aTurnPhase + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return desiredLocation;
	}

	
	public List<PieceLocation> getAllLocationsForPiece(String aPieceId)
			throws InterruptedException, ExecutionException {

		logger.log("Started getting all the locations for piece id: " + aPieceId);

		List<PieceLocation> desiredLocations = new ArrayList<PieceLocation>();

		// asynchronously retrieve all locations
		Query query = db.collection(topLevelCollectionName).document(locationDocumentName)
				.collection(locationCollectionName).whereEqualTo("pieceId", aPieceId);
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log(
					"Number of documents in the get all locations for piece query snapshot:" + documents.size());
			querySnapshot.forEach((document) -> {
				PieceLocation aDesiredLocation = new PieceLocation(document.getId(), document.getString("pieceId"), document.getString("turnId"),
						Phases.valueOf(document.getString("turnPhase")), document.getString("gameId"),
						document.getString("nameOfLocationAtBeginningOfPhase"),
						document.getString("nameOfLocationAtEndOfPhase"),
						document.getBoolean("mustRetreatAtEndOfTurn"));
				desiredLocations.add(aDesiredLocation);
			});

		} catch (Exception e) {
			logger.log("error getting all the locations for piece id: " + aPieceId + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return desiredLocations;
	}



	protected Timestamp updateLocation(PieceLocation aLocation) throws InterruptedException, ExecutionException {

		logger.log("Started updating piece location with id: " + aLocation.getId());

		Map<String, Object> docData = new HashMap<String, Object>();
		docData.put("pieceId", aLocation.getPieceId());
		docData.put("turnId", aLocation.getTurnId());
		docData.put("gameId", aLocation.getGameId());
		docData.put("turnPhase", aLocation.getTurnPhase().toString());
		docData.put("nameOfLocationAtBeginningOfPhase", aLocation.getNameOfLocationAtBeginningOfPhase());
		docData.put("mustRetreatAtEndOfTurn", aLocation.isMustRetreatAtEndOfTurn());
		docData.put("nameOfLocationAtEndOfPhase", aLocation.getNameOfLocationAtEndOfPhase());

		ApiFuture<WriteResult> future = db.collection(topLevelCollectionName).document(locationDocumentName)
				.collection(locationCollectionName).document(aLocation.getId()).set(docData);
		WriteResult aWriteResult = future.get();

		logger.log("Update time: " + aWriteResult.getUpdateTime());

		return aWriteResult.getUpdateTime();
	}

	public String insertLocation(PieceLocation aLocation) throws InterruptedException, ExecutionException {

		logger.log("Started inserting piece location with id: " + aLocation.getId());

		Map<String, Object> docData = new HashMap<String, Object>();
		docData.put("pieceId", aLocation.getPieceId());
		docData.put("turnId", aLocation.getTurnId());
		docData.put("gameId", aLocation.getGameId());
		docData.put("turnPhase", aLocation.getTurnPhase().toString());
		docData.put("nameOfLocationAtBeginningOfPhase", aLocation.getNameOfLocationAtBeginningOfPhase());
		docData.put("mustRetreatAtEndOfTurn", aLocation.isMustRetreatAtEndOfTurn());
		docData.put("nameOfLocationAtEndOfPhase", aLocation.getNameOfLocationAtEndOfPhase());

		ApiFuture<DocumentReference> addedDocRef = db.collection(topLevelCollectionName).document(locationDocumentName)
				.collection(locationCollectionName).add(docData);

		aLocation.setId(addedDocRef.get().getId());

		return aLocation.getId();
	}

	protected Timestamp deleteLocation(PieceLocation aLocation) throws InterruptedException, ExecutionException {

		logger.log("Started deleting piece location with id: " + aLocation.getId());

		ApiFuture<WriteResult> future = db.collection(topLevelCollectionName).document(locationDocumentName)
				.collection(locationCollectionName).document(aLocation.getId()).delete();

		WriteResult aWriteResult = future.get();

		logger.log("Delete time: " + aWriteResult.getUpdateTime());

		return aWriteResult.getUpdateTime();
	}
	
	public void addLocation(Piece aPiece, PieceLocation aLocationToAdd) throws InterruptedException, ExecutionException {
		insertLocation(aLocationToAdd);
		aPiece.setPieceLocation(aLocationToAdd);
		
	}

}
