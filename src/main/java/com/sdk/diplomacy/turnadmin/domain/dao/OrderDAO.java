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
import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Piece;

public class OrderDAO {

	private Firestore db = null;
	private LambdaLogger logger;
	private String topLevelCollectionName;

	public OrderDAO(Firestore db, LambdaLogger logger, String aTopLevelCollectionName) {
		super();
		this.logger = logger;
		this.db = db;
		topLevelCollectionName = aTopLevelCollectionName;
	}
	
	public List<Order> getOrdersForTurn(String aTurnID) throws InterruptedException, ExecutionException {
		
		logger.log("Started getting the orders for turn id: " + aTurnID);
		
		List<Order> listOfOrders = new ArrayList<Order>();

		// asynchronously retrieve all orders
		Query query = db.collection(topLevelCollectionName).document("moves").collection("allMoves").whereEqualTo("turnId", aTurnID);
		ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
		try {
			logger.log("About to launch the query");
			// querySnapshot.get() blocks on response
			QuerySnapshot querySnapshot = querySnapshotFuture.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			logger.log("Number of documents in the get orders for turn query snapshot:" + documents.size());
			for (QueryDocumentSnapshot document : documents) {
				logger.log("Order ID: " + document.getId());
				Order anOrder = new Order(document.getId(), Piece.PieceType.from(document.getString("pieceType")), 
						document.getString("currentLocationName"), Order.Action.from(document.getString("action")),
						document.getString("endingLocationName"), Piece.PieceType.from(document.getString("secondaryPieceType")),
						document.getString("secondaryCurrentLocationName"), Order.Action.from(document.getString("secondaryAction")), 
						document.getString("secondaryEndingLocationName"), document.getString("owningCountryName"), 
						document.getString("turnId"), document.getString("gameId")
						);
				listOfOrders.add(anOrder);
			}

		} catch (Exception e) {
			logger.log("error getting the orders for turn: " + aTurnID + " stacktrace: "
					+ DAOUtilities.printStackTrace(e));
			throw e;
		}

		return listOfOrders;
	}

}
