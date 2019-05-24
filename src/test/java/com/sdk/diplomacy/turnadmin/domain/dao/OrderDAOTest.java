package com.sdk.diplomacy.turnadmin.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.PropertyManager;
import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class OrderDAOTest {

	protected static OrderDAO myOrderDAO;
	protected static TestLambdaLogger myTestLambdaLogger;

	@BeforeClass
	public static void beforeTests() throws Exception {

		myTestLambdaLogger = new TestLambdaLogger();

		try {
			PropertyManager myManager = new PropertyManager(myTestLambdaLogger);
			myManager.setPropertyFileName("unitTestDiplomacy.properties");
			myManager.initializeProperties();

			myOrderDAO = new DAOWarehouse(myTestLambdaLogger,
					myManager.getProperties().getProperty("topLevelFirestoreCollectionName")).getOrderDAO();

		} catch (Exception e) {
			throw (e);
		}
	}
	
	@Test
	public void testGettingOrdersForATurn() throws Exception {

		String myTurnID = "qYGI6zXja0B8QfGco4QT";

		List<Order> myOrders = myOrderDAO.getOrdersForTurn(myTurnID);

		assertNotNull("an order list came back", myOrders);
		assertEquals("right number of orders came back", 2, myOrders.size());
		Order holdOrder = null;
		Order supportOrder = null;
		for (Order anOrder : myOrders) {
			if (Action.HOLDS == anOrder.getAction()) {
				holdOrder = anOrder;
			} else {
				if (Action.SUPPORTS == anOrder.getAction()) {
					supportOrder = anOrder;
				}
			}
		};
		
		assertNotNull("hold order is selected", holdOrder);
		assertEquals("hold game id is right", "Woe9CDOyILIlCq53UHUJ", holdOrder.getGameId());
		assertEquals("hold turn id is right", myTurnID, holdOrder.getTurnId());
		assertEquals("hold current location name", "London", holdOrder.getCurrentLocationName());
		assertNull("hold ending location name", holdOrder.getEndingLocationName());
		assertEquals("hold owning country name", "England", holdOrder.getOwningCountryName());
		assertEquals("hold piece Type", Piece.PieceType.FLEET, holdOrder.getPieceType());
		assertNull("hold secondary action", holdOrder.getSecondaryAction());
		assertNull("hold secondary current location name", holdOrder.getSecondaryCurrentLocationName());
		assertNull("hold secondary ending location name", holdOrder.getSecondaryEndingLocationName());
		assertNull("hold secondary piece type", holdOrder.getSecondaryPieceType());
		
		assertNotNull("support order is selected", supportOrder);
		assertEquals("support game id is right", "Woe9CDOyILIlCq53UHUJ", supportOrder.getGameId());
		assertEquals("support turn id is right", myTurnID, supportOrder.getTurnId());
		assertEquals("support current location name", "Wales", supportOrder.getCurrentLocationName());
		assertNull("support ending location name", supportOrder.getEndingLocationName());
		assertEquals("support owning country name", "England", supportOrder.getOwningCountryName());
		assertEquals("support piece Type", Piece.PieceType.ARMY, supportOrder.getPieceType());
		assertEquals("support secondary action", Action.HOLDS, supportOrder.getSecondaryAction());
		assertEquals("support secondary current location name", "London", supportOrder.getSecondaryCurrentLocationName());
		assertEquals("support secondary ending location name", "London", supportOrder.getSecondaryEndingLocationName());
		assertEquals("support secondary piece type", Piece.PieceType.FLEET, supportOrder.getSecondaryPieceType());

	}

}
