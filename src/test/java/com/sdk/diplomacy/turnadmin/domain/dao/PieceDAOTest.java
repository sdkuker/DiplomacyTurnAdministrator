package com.sdk.diplomacy.turnadmin.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sdk.diplomacy.dao.DAOWarehouse;
import com.google.cloud.Timestamp;
import com.sdk.diplomacy.turnadmin.PropertyManager;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class PieceDAOTest {

	protected static PieceDAO myPieceDAO;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myPieceDAO = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionName")).getPieceDAO();

		} catch (Exception e) {
			throw (e);
		}
	}

	@Test
	public void testGettingPiecesForATurn() throws Exception {

		String myTurnID = "fNOhEKjDSVOeQcZM2rMg";

		List<Piece> myPieces = myPieceDAO.getPiecesForTurn(myTurnID);

		assertNotNull("a piece list came back", myPieces);
		assertEquals("right number of pieces came back", 1, myPieces.size());
		Piece returnedPiece = myPieces.get(0);
		assertEquals("game id is right", "Woe9CDOyILIlCq53UHUJ", returnedPiece.getGameId());
		assertEquals("turn id is right", myTurnID, returnedPiece.getTurnId());
		assertTrue("must retreat is right", returnedPiece.getMustRetreatAtEndOfTurn());
		assertEquals("location at beginning of turn", "Paris", returnedPiece.getNameOfLocationAtBeginningOfTurn());
		assertEquals("location at end of turn", "Gascony", returnedPiece.getNameOfLocationAtEndOfTurn());
		assertEquals("owning country name", "France", returnedPiece.getOwningCountryName());
		assertEquals("type", Piece.PieceType.ARMY, returnedPiece.getType());
	}

	@Test
	public void testInsertingUpdatingAndDeletingAPiece() throws Exception {

		String turnId = "testInsertingAndDeletingAPieceTurn";
		
		Piece myPiece = new Piece("tempId", "France", "Paris", turnId,
				"testInsertingAndDeletingAPieceGameÏ", PieceType.ARMY);
		
		String pieceId = myPieceDAO.insertPiece(myPiece);
		
		assertNotNull("new Id was returned", pieceId);
		assertFalse("new id is different from original one", pieceId.contentEquals("tempId"));
		
		List<Piece> myPieces = myPieceDAO.getPiecesForTurn(turnId);
		assertEquals("right number of pieces came back", 1, myPieces.size());
		assertEquals("piece id is right", pieceId, myPieces.get(0).getId());
		
		myPieces.get(0).setNameOfLocationAtEndOfTurn("Brest");
		
		Timestamp udpateTimestamp = myPieceDAO.update(myPieces.get(0));
		assertNotNull("update timestamp", udpateTimestamp);
		
		List<Piece> myUpdatedPieces = myPieceDAO.getPiecesForTurn(turnId);
		assertEquals("right number of pieces came back on update", 1, myUpdatedPieces.size());
		assertEquals("piece was updated", "Brest", myUpdatedPieces.get(0).getNameOfLocationAtEndOfTurn());

		Timestamp deleteTimestamp = myPieceDAO.deletePiece(myPiece);
		
		assertNotNull("delete timestamp", deleteTimestamp);
		List<Piece> myPiecesAfterDelete = myPieceDAO.getPiecesForTurn(turnId);
		assertEquals("right number of pieces came back", 0, myPiecesAfterDelete.size());

	}
	
}
