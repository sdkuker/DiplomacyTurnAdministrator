package com.sdk.diplomacy.turnadmin;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.sdk.diplomacy.dao.DAOUtilities;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.conflict.ConflictResolutionResults;
import com.sdk.diplomacy.turnadmin.conflict.OrderResolutionResults;
import com.sdk.diplomacy.turnadmin.conflict.StandoffProvince;
import com.sdk.diplomacy.turnadmin.conflict.TurnResolver;
import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.PieceLocation;
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

	public void executeOrderResolutionPhase(String aGameId, String aTurnID, TurnResolver aTurnResolver) {

		try {
			List<Order> ordersForTurn = myDAOWarehouse.getOrderDAO().getOrdersForTurn(aTurnID);
			List<Piece> piecesForTurn = myDAOWarehouse.getPieceDAO().getPiecesForTurn(aGameId, aTurnID,
					Phases.DIPLOMATIC);

			ConflictResolutionResults conflictResolutionResults = aTurnResolver.resolveConflict(ordersForTurn,
					piecesForTurn);

			for (Piece aPiece : piecesForTurn) {
				myDAOWarehouse.getPieceDAO().update(aPiece);
				PieceLocation aNewLocation = new PieceLocation(null, aPiece.getId(), aTurnID,
						Phases.RETREAT_AND_DISBANDING, aPiece.getGameId(), aPiece.getNameOfLocationAtEndOfPhase(), null,
						aPiece.getMustRetreatAtEndOfTurn());
				myDAOWarehouse.getPieceDAO().addLocation(aPiece, aNewLocation);
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
			myLogger.log("Unable to execute  order resolution phase for turn: " + aTurnID + " stack trace is: "
					+ DAOUtilities.printStackTrace(e));
		}
	}

	public void executeRetreatAndDisbandingPhase(String aGameId, String aTurnId) {
		/*
		 * the actual repositioning for retreats and disbands should have been done in
		 * real-time using the GUI. If it's a spring turn, we end the turn and generate
		 * the fall turn. If it's the fall turn, we go to the next phase.
		 */
		try {
			Turn myTurn = myDAOWarehouse.getTurnDAO().getTurn(aTurnId);
			if (myTurn != null && Turn.Seasons.SPRING == myTurn.getSeason()) {
				createPieceLocationsForPhase(Turn.Phases.RETREAT_AND_DISBANDING, aGameId, aTurnId);
				createFallTurn(aGameId, aTurnId);
			} else {
				myDAOWarehouse.getTurnDAO().updatePhase(myTurn, Turn.Phases.GAINING_AND_LOSING_UNITS);
			}
		} catch (Exception e) {
			myLogger.log("Unable to execute retreat and disbanding phase for turn: " + aTurnId + " stack trace is: "
					+ DAOUtilities.printStackTrace(e));
		}

	}

	protected void createPieceLocationsForPhase(Phases currentPhase, String aGameId, String aTurnId) {

		try {
			List<PieceLocation> pieceLocations = myDAOWarehouse.getPieceDAO().getAllLocations(aGameId, aTurnId,
					currentPhase);
			if (pieceLocations.size() > 0) {
				pieceLocations.forEach((aLocation) -> {
					try {
						myDAOWarehouse.getPieceDAO().insertLocation(aLocation.cloneForNextPhase());
					} catch (Exception e) {
						myLogger.log("Unable to create piece location for a piece" + " stack trace is: "
								+ DAOUtilities.printStackTrace(e));

					}
				});
			}
		} catch (Exception e) {
			myLogger.log("Unable to create piece locations for game: " + aGameId + " turn: " + aTurnId + " phase: "
					+ currentPhase + " stack trace is: " + DAOUtilities.printStackTrace(e));

		}
	}

	protected void createFallTurn(String aGameId, String aTurnId) {
		// TODO do this too
	}

}
