package com.sdk.diplomacy.turnadmin;

import java.util.List;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.conflict.ConflictResolutionResults;
import com.sdk.diplomacy.turnadmin.conflict.OrderResolutionResults;
import com.sdk.diplomacy.turnadmin.conflict.StandoffProvince;
import com.sdk.diplomacy.turnadmin.conflict.TurnResolver;
import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Turn;
import com.sdk.diplomacy.turnadmin.domain.Turn.Phases;

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
			List<Piece> piecesForTurn = myDAOWarehouse.getPieceDAO().getPiecesForTurn(aTurnID, Phases.ORDER_RESOLUTION);
			TurnResolver myResolver = new TurnResolver();
			
			ConflictResolutionResults conflictResolutionResults = myResolver.resolveConflict(ordersForTurn,
					piecesForTurn);
			
			for (Piece aPiece: piecesForTurn) {
				myDAOWarehouse.getPieceDAO().update(aPiece);
			}
			
			for (StandoffProvince aStandoffProvince : conflictResolutionResults.getStandoffProvinces()) {
				myDAOWarehouse.getStandoffProvinceDAO().insertStandoffProvince(aStandoffProvince);
			}
			
			for (OrderResolutionResults aResults : conflictResolutionResults.getOrderResolutionResults().values()) {
				myDAOWarehouse.getOrderResolutionResultsDAO().insertOrderResolutionResults(aResults);
			}
	
			Turn myTurn = myDAOWarehouse.getTurnDAO().getTurn(aTurnID);
			myDAOWarehouse.getTurnDAO().updatePhase(myTurn, Turn.Phases.RETREAT_AND_DISBANDING);
			
		} catch (Exception e) {
			myLogger.log("Unable to execute turn: " + aTurnID + " stack trace is: " + DAOUtilities.printStackTrace(e));
		}
	}

}
