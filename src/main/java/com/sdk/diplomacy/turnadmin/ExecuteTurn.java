package com.sdk.diplomacy.turnadmin;

import java.util.List;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.conflict.ConflictResolutionResults;
import com.sdk.diplomacy.turnadmin.conflict.TurnResolver;
import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Piece;

public class ExecuteTurn {

	private DAOWarehouse myDAOWarehouse;
	private LambdaLogger myLogger;

	public ExecuteTurn(DAOWarehouse aDAOWarehouse, LambdaLogger aLambdaLogger) {
		super();
		this.myDAOWarehouse = aDAOWarehouse;
		myLogger = aLambdaLogger;
	}

	public void executeOrderResolutionPhase(String aGameId, String aTurnID) {

		try {
			List<Order> ordersForTurn = myDAOWarehouse.getOrderDAO().getOrdersForTurn(aTurnID);
			List<Piece> piecesForTurn = myDAOWarehouse.getPieceDAO().getPiecesForTurn(aTurnID);
			TurnResolver myResolver = new TurnResolver();
			
			ConflictResolutionResults conflictResolutionResults = myResolver.resolveConflict(ordersForTurn,
					piecesForTurn);
			
			//TODO persist updates to the pieces
			//TODO persist the standoff provinces
			//TODO persist the order execution results
			//TODO persist the change in phase of the turn done if it's a spring turn or 'retreat and disbanding' if a fall turn
		} catch (Exception e) {
			myLogger.log("Unable to execute turn: " + aTurnID + " stack trace is: " + DAOUtilities.printStackTrace(e));
		}
	}

}
