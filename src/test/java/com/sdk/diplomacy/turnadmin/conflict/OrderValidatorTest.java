package com.sdk.diplomacy.turnadmin.conflict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.OrderExecutionResult;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.map.GameMap;

public class OrderValidatorTest {

	protected OrderValidator myOrderValidator = new OrderValidator();
	protected GameMap myGameMap = new GameMap();

	@Before
	public void beforeTest() {
		myGameMap.initialize();
	}

	@Test
	public void testValidIdValidation() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateId(validPieceOrder, result1);

		assertTrue("valid id is valid", result1.isValidOrder());
		assertTrue("valid id executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("valid id has no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidIdValidation() {

		Order validPieceOrder = new Order(null, PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult(null, "turnId", "gameId");

		myOrderValidator.validateId(validPieceOrder, result1);

		assertFalse("invalid id order is not valid", result1.isValidOrder());
		assertFalse("invalid id order did not executed successfully", result1.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - id", result1.getExecutionDescription());
	}

	@Test
	public void testValidTurnIdValidation() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateTurnId(validPieceOrder, result1);

		assertTrue("turn id is valid", result1.isValidOrder());
		assertTrue("turn id executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidTurnIdValidation() {

		Order validPieceOrder = new Order(null, PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", null, "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult(null, null, "gameId");

		myOrderValidator.validateTurnId(validPieceOrder, result1);

		assertFalse("turn id is not valid", result1.isValidOrder());
		assertFalse("invalid turn id id order did not executed successfully", result1.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - turn id", result1.getExecutionDescription());
	}

	@Test
	public void testValidGameIdValidation() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateGameId(validPieceOrder, result1);

		assertTrue("game id is valid", result1.isValidOrder());
		assertTrue("game id executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidGameIdValidation() {

		Order validPieceOrder = new Order(null, PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", "turnId", null);

		OrderExecutionResult result1 = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateGameId(validPieceOrder, result1);

		assertFalse("game id is not valid", result1.isValidOrder());
		assertFalse("invalid game id id order did not executed successfully", result1.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - game id", result1.getExecutionDescription());
	}

	@Test
	public void testValidPieceTypeValidation() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validatePieceType(validPieceOrder, result1);

		assertTrue("valid piece type order is valid", result1.isValidOrder());
		assertTrue("valid piece order executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("valid pice order has no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInValidPieceTypeValidation() {

		Order validPieceOrder = new Order("1", null, "currentLocationName", Action.HOLDS, "endingLocationName", null,
				null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validatePieceType(validPieceOrder, result1);

		assertFalse("invalid piece type order is not valid", result1.isValidOrder());
		assertFalse("invalid piece order did not executed successfully", result1.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - missing piece type", result1.getExecutionDescription());
	}

	@Test
	public void testValidCurrentLocation() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "Paris", Action.HOLDS, "endingLocationName", null, null,
				null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Paris", new Piece(null, "France", "Paris", "turnId", "gameId", PieceType.ARMY));

		myOrderValidator.validateCurrentLocationName(validPieceOrder, result1, myGameMap, existingPieces);

		assertTrue("current location validity", result1.isValidOrder());
		assertTrue("current location executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("current locationhas no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidCurrentLocationMissing() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, null, Action.HOLDS, "endingLocationName", null, null,
				null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();	

		myOrderValidator.validateCurrentLocationName(validPieceOrder, result1, myGameMap, existingPieces);

		assertFalse("current location validity", result1.isValidOrder());
		assertFalse("current location execution", result1.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - missing current location", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidCurrentLocationBad() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "Georgia", Action.HOLDS, "endingLocationName", null,
				null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Georgia", new Piece(null, "France", "Georgia", "turnId", "gameId", PieceType.ARMY));

		myOrderValidator.validateCurrentLocationName(validPieceOrder, result1, myGameMap, existingPieces);

		assertFalse("current location validity", result1.isValidOrder());
		assertFalse("current location execution", result1.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - unknown current location: Georgia",
				result1.getExecutionDescription());
	}

	@Test
	public void testValidEndingLocation() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "Paris", Action.HOLDS, "Burgundy", null, null, null,
				null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateEndingLocationName(validPieceOrder, result1, myGameMap);

		assertTrue("ending location validity", result1.isValidOrder());
		assertTrue("ending location executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("ending locationhas no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidEndingLocationMissing() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "Paris", Action.HOLDS, null, null, null, null, null,
				"France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateEndingLocationName(validPieceOrder, result1, myGameMap);

		assertFalse("ending location validity", result1.isValidOrder());
		assertFalse("ending location execution", result1.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - missing ending location", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidEndingLocationBad() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "Georgia", Action.HOLDS, "Georgia", null, null, null,
				null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateEndingLocationName(validPieceOrder, result1, myGameMap);

		assertFalse("ending location validity", result1.isValidOrder());
		assertFalse("ending location execution", result1.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - unknown ending location: Georgia",
				result1.getExecutionDescription());
	}
	
	@Test
	public void testValidSecondaryEndingLocation() {

		Order validPieceOrder = new Order("1", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.MOVESTO,
				"Belguim", "France", "turnId", "gameId");

		OrderExecutionResult result = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateSecondaryEndingLocationName(validPieceOrder, result, myGameMap);

		assertTrue("secondary ending location validity", result.isValidOrder());
		assertTrue("secondary ending location executed successfully", result.wasOrderExecutedSuccessfully());
		assertNull("secondary ending locationhas no execution description", result.getExecutionDescription());
	}

	@Test
	public void testInvalidSecondaryEndingLocationMissing() {

		Order order = new Order("1", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.MOVESTO,
				null, "France", "turnId", "gameId");

		OrderExecutionResult result = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateSecondaryEndingLocationName(order, result, myGameMap);

		assertFalse("secondary ending location validity", result.isValidOrder());
		assertFalse("secondary ending location execution", result.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - missing secondary ending location", result.getExecutionDescription());
	}

	@Test
	public void testInvalidSecondaryEndingLocationBad() {

		Order order = new Order("1", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy", Action.MOVESTO,
				"Georgia", "France", "turnId", "gameId");

		OrderExecutionResult result = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateSecondaryEndingLocationName(order, result, myGameMap);

		assertFalse("secondary ending location validity", result.isValidOrder());
		assertFalse("secondary ending location execution", result.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - unknown secondary ending location: Georgia",
				result.getExecutionDescription());
	}


	@Test
	public void testValidSecondaryCurrentLocation() {

		Order anOrder = new Order("1", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Burgundy",
				Action.MOVESTO, "Marseilles", "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Paris", new Piece(null, "France", "Paris", "turnId", "gameId", PieceType.ARMY));
		existingPieces.put("Burgundy", new Piece(null, "France", "Burgundy", "turnId", "gameId", PieceType.ARMY));

		myOrderValidator.validateSecondaryCurrentLocationName(anOrder, result1, myGameMap, existingPieces);

		assertTrue("secondary current location validity", result1.isValidOrder());
		assertTrue("secondary current location executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("secondary current locationhas no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidSecondaryCurrentLocationMissing() {

		Order anOrder = new Order("1", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, null,
				Action.MOVESTO, "Marseilles", "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();	

		myOrderValidator.validateSecondaryCurrentLocationName(anOrder, result1, myGameMap, existingPieces);

		assertFalse("secondary current location validity", result1.isValidOrder());
		assertFalse("secondary current location execution", result1.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - missing secondary current location", result1.getExecutionDescription());
	}

	@Test
	public void testInvalidSecondaryCurrentLocationBad() {

		Order anOrder = new Order("1", PieceType.ARMY, "Paris", Action.SUPPORTS, null, PieceType.ARMY, "Georgia",
				Action.MOVESTO, "Marseilles", "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Paris", new Piece(null, "France", "Paris", "turnId", "gameId", PieceType.ARMY));
		existingPieces.put("Georgia", new Piece(null, "France", "Georgia", "turnId", "gameId", PieceType.ARMY));

		myOrderValidator.validateSecondaryCurrentLocationName(anOrder, result1, myGameMap, existingPieces);

		assertFalse("secondary current location validity", result1.isValidOrder());
		assertFalse("secondary current location execution", result1.wasOrderExecutedSuccessfully());
		assertEquals("description", "Invalid order - unknown secondary current location: Georgia",
				result1.getExecutionDescription());
	}

	@Test
	public void testMissingAction() {

		Order validPieceOrder = new Order(null, PieceType.ARMY, "currentLocationName", null, "endingLocationName", null,
				null, null, null, "France", "turnId", null);
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("currentLocationName", new Piece(null, "France", "currentLocationName", "turnId", "gameId", PieceType.ARMY));
		
		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("currentLocationName", validPieceOrder);

		OrderExecutionResult result1 = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateOrder(validPieceOrder, result1, myGameMap, existingPieces, allOrders);

		assertFalse("action is not valid", result1.isValidOrder());
		assertFalse("invalid action id order did not executed successfully", result1.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - missing action", result1.getExecutionDescription());
	}

	@Test
	public void validateHoldAction() {

		Order goodOrder = new Order(null, PieceType.ARMY, "currentLocationName", Action.HOLDS, "currentLocationName",
				null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateHoldAction(goodOrder, result1, myGameMap);

		assertTrue("good action is valid", result1.isValidOrder());
		assertTrue("good action executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("description is correct", result1.getExecutionDescription());

		Order badOrderEndingLocation = new Order(null, PieceType.ARMY, "currentLocationName", Action.HOLDS,
				"endingLocationName", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult badOrderEndingLocationResult = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateHoldAction(badOrderEndingLocation, badOrderEndingLocationResult, myGameMap);

		assertFalse("bad action is not valid", badOrderEndingLocationResult.isValidOrder());
		assertFalse("bad action not executed successfully",
				badOrderEndingLocationResult.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - must hold in current location",
				badOrderEndingLocationResult.getExecutionDescription());
		
		Order badOrderSecondaryFields = new Order(null, PieceType.ARMY, "currentLocationName", Action.HOLDS,
				null, null, "secondaryCurrentLocationName", null, "secondaryEndingLocationName", "France", "turnId", "gameId");

		OrderExecutionResult badOrderSecondaryFieldsResult = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateHoldAction(badOrderSecondaryFields, badOrderSecondaryFieldsResult, myGameMap);

		assertFalse("bad action is not valid", badOrderSecondaryFieldsResult.isValidOrder());
		assertFalse("bad action not executed successfully",
				badOrderSecondaryFieldsResult.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - no secondary fields can be specified for hold actions",
				badOrderSecondaryFieldsResult.getExecutionDescription());

	}
	
	@Test
	public void validateMovesToActionSuccess() {

		Order goodOrder = new Order(null, PieceType.ARMY, "Paris", Action.MOVESTO, "Burgundy",
				null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateMovesToAction(goodOrder, result1, myGameMap);

		assertTrue("good action is valid", result1.isValidOrder());
		assertTrue("good action executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("description is correct", result1.getExecutionDescription());
		
		Order goodConvoyableOrder = new Order(null, PieceType.ARMY, "Brest", Action.MOVESTO, "London",
				null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult goodConvoyableOrderResult = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateMovesToAction(goodConvoyableOrder, goodConvoyableOrderResult, myGameMap);

		assertTrue("convoyable action is valid", goodConvoyableOrderResult.isValidOrder());
		assertTrue("convoyable action executed successfully", goodConvoyableOrderResult.wasOrderExecutedSuccessfully());
		assertNull("convoyable description is correct", goodConvoyableOrderResult.getExecutionDescription());


	}

	@Test
	public void validateMovesToActionFailLocation() {

		Order badOrderNonAdjacentEndingLocation = new Order(null, PieceType.ARMY, "Paris", Action.MOVESTO,
				"Berlin", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult badOrderNonAdjacentEndingLocationResult = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateMovesToAction(badOrderNonAdjacentEndingLocation, badOrderNonAdjacentEndingLocationResult, myGameMap);

		assertFalse("bad action is not valid", badOrderNonAdjacentEndingLocationResult.isValidOrder());
		assertFalse("bad action not executed successfully",
				badOrderNonAdjacentEndingLocationResult.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - ending location does not border current location",
				badOrderNonAdjacentEndingLocationResult.getExecutionDescription());
		
	}
	
	@Test
	public void validateMovesToActionFailSecondaryFields() {
		
		Order badOrderSecondaryFields = new Order(null, PieceType.ARMY, "currentLocationName", Action.HOLDS,
				null, null, "secondaryCurrentLocationName", null, "secondaryEndingLocationName", "France", "turnId", "gameId");

		OrderExecutionResult badOrderSecondaryFieldsResult = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateMovesToAction(badOrderSecondaryFields, badOrderSecondaryFieldsResult, myGameMap);

		assertFalse("bad action is not valid", badOrderSecondaryFieldsResult.isValidOrder());
		assertFalse("bad action not executed successfully",
				badOrderSecondaryFieldsResult.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - no secondary fields can be specified for move to actions",
				badOrderSecondaryFieldsResult.getExecutionDescription());

	}
	
	@Test
	public void validateMovesToActionFailPiecesToInvalidLocations() {
		
		Order armyToWaterOrder = new Order(null, PieceType.ARMY, "Brest", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult armyToWaterOrderResult = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateMovesToAction(armyToWaterOrder, armyToWaterOrderResult, myGameMap);

		assertFalse("army to water action is not valid", armyToWaterOrderResult.isValidOrder());
		assertFalse("army to water action not executed successfully",
				armyToWaterOrderResult.wasOrderExecutedSuccessfully());
		assertEquals("army to water description is correct", "Invalid order - armies can't move to water provinces",
				armyToWaterOrderResult.getExecutionDescription());

		//
		
		Order fleetToInlandOrder = new Order(null, PieceType.FLEET, "Brest", Action.MOVESTO,
				"Paris", null, null, null, null, "France", "turnId", "gameId");

		OrderExecutionResult fleetToInlandOrderResult = new OrderExecutionResult(null, "turnId", null);

		myOrderValidator.validateMovesToAction(fleetToInlandOrder, fleetToInlandOrderResult, myGameMap);

		assertFalse("fleet to inland action is not valid", fleetToInlandOrderResult.isValidOrder());
		assertFalse("fleet to inland action not executed successfully",
				fleetToInlandOrderResult.wasOrderExecutedSuccessfully());
		assertEquals("fleet to inland description is correct", "Invalid order - fleets can't move to inland provinces",
				fleetToInlandOrderResult.getExecutionDescription());
	}
	
	@Test
	public void validatePieceAndTypeInitialLocation() {
		
		Order goodOrder = new Order(null, PieceType.FLEET, "Brest", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.FLEET));
		
		OrderExecutionResult goodOrderResult = new OrderExecutionResult(null, "turnId", null);
		
		myOrderValidator.validatePieceAndTypeInInitialLocation(goodOrder, goodOrderResult, existingPieces);
		
		assertTrue("good order is valid", goodOrderResult.isValidOrder());
		assertTrue("good order executed successfully", goodOrderResult.wasOrderExecutedSuccessfully());
		assertNull("good order description is correct", goodOrderResult.getExecutionDescription());
	}
	
	@Test
	public void validatePieceAndTypeInInitialLocationNoPieceInCurrentLocation() {
		
		Order badOrderNoPieceInCurrentLocation = new Order(null, PieceType.FLEET, "Brest", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("George", new Piece(null, "France", "George", "turnId", "gameId", PieceType.FLEET));
		
		OrderExecutionResult badOrderNoPieceInCurrentLocationResult = new OrderExecutionResult(null, "turnId", null);
		
		myOrderValidator.validatePieceAndTypeInInitialLocation(badOrderNoPieceInCurrentLocation, badOrderNoPieceInCurrentLocationResult, existingPieces);
		
		assertFalse("no piece in current location is not valid", badOrderNoPieceInCurrentLocationResult.isValidOrder());
		assertFalse("no piece in current location not executed successfully",
				badOrderNoPieceInCurrentLocationResult.wasOrderExecutedSuccessfully());
		assertEquals("no piece in current location description is correct", "Invalid order - no piece occupies the current location",
				badOrderNoPieceInCurrentLocationResult.getExecutionDescription());
	}
	
	@Test
	public void validatePieceAndTypeInitialLocationInvalidPieceType() {
		
		Order badOrderInvalidPieceType = new Order(null, PieceType.FLEET, "Brest", Action.MOVESTO,
				"English_Channel", null, null, null, null, "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));
		
		OrderExecutionResult badOrderInvalidPieceTypeResult = new OrderExecutionResult(null, "turnId", null);
		
		myOrderValidator.validatePieceAndTypeInInitialLocation(badOrderInvalidPieceType, badOrderInvalidPieceTypeResult, existingPieces);
		
		assertFalse("invalid piece type is not valid", badOrderInvalidPieceTypeResult.isValidOrder());
		assertFalse("invalid piece type not executed successfully",
				badOrderInvalidPieceTypeResult.wasOrderExecutedSuccessfully());
		assertEquals("invalid piece type description is correct", "Invalid order - piece type in the order is not the same as the piece already occupying the current location",
				badOrderInvalidPieceTypeResult.getExecutionDescription());
	}
	
	@Test
	public void validateSecondaryPieceAndTypeInitialLocation() {
		
		Order order = new Order(null, PieceType.ARMY, "Brest", Action.SUPPORTS,
				null, PieceType.ARMY, "Paris", Action.MOVESTO, "Belguim", "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));
		existingPieces.put("Paris", new Piece(null, "France", "Paris", "turnId", "gameId", PieceType.ARMY));
		
		OrderExecutionResult result = new OrderExecutionResult(null, "turnId", null);
		
		myOrderValidator.validateSecondaryPieceAndTypeInInitialLocation(order, result, existingPieces);
		
		assertTrue("good order is valid", result.isValidOrder());
		assertTrue("good order executed successfully", result.wasOrderExecutedSuccessfully());
		assertNull("good order description is correct", result.getExecutionDescription());
	}
	
	@Test
	public void validateSecondaryPieceAndTypeInInitialLocationNoPieceInSecondaryCurrentLocation() {
		
		Order order = new Order(null, PieceType.ARMY, "Brest", Action.SUPPORTS,
				null, PieceType.ARMY, null, Action.MOVESTO, "Belguim", "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));
		
		OrderExecutionResult result = new OrderExecutionResult(null, "turnId", null);
		
		myOrderValidator.validateSecondaryPieceAndTypeInInitialLocation(order, result, existingPieces);
		
		assertFalse("no piece in secondary current location is not valid", result.isValidOrder());
		assertFalse("no piece in secondary current location not executed successfully",
				result.wasOrderExecutedSuccessfully());
		assertEquals("no piece in secondary current location description is correct", "Invalid order - no piece occupies the secondary current location",
				result.getExecutionDescription());
	}
	
	@Test
	public void validateSecondaryPieceAndTypeInitialLocationInvalidPieceType() {
		
		Order order = new Order(null, PieceType.ARMY, "Brest", Action.SUPPORTS,
				null, PieceType.ARMY, "Belguim", Action.MOVESTO, "Paris", "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));
		existingPieces.put("Belguim", new Piece(null, "France", "Belguim", "turnId", "gameId", PieceType.FLEET));
		
		OrderExecutionResult result = new OrderExecutionResult(null, "turnId", null);
		
		myOrderValidator.validateSecondaryPieceAndTypeInInitialLocation(order, result, existingPieces);
		
		assertFalse("invalid piece type is not valid", result.isValidOrder());
		assertFalse("invalid piece type not executed successfully",
				result.wasOrderExecutedSuccessfully());
		assertEquals("Invalid order - secondary piece type in the order is not the same as the piece type already occupying the secondary current location",
				result.getExecutionDescription());
	}

	
	@Test
	public void testValidSecondaryPieceTypeValidation() {

		Order validSecondaryPieceOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.SUPPORTS,
				null, PieceType.ARMY, "Paris", Action.MOVESTO, "Burgundy", "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateSecondaryPieceType(validSecondaryPieceOrder, result1);

		assertTrue("valid secondary piece type order is valid", result1.isValidOrder());
		assertTrue("valid secondary piece order executed successfully", result1.wasOrderExecutedSuccessfully());
		assertNull("valid secondary piece order has no execution description", result1.getExecutionDescription());
	}

	@Test
	public void testInValidSecondaryPieceTypeValidation() {

		Order anOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.SUPPORTS,
				null, null, "Georgia", Action.MOVESTO, "Burgundy", "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateSecondaryPieceType(anOrder, result1);

		assertFalse("invalid secondary piece type order is not valid", result1.isValidOrder());
		assertFalse("invalid secondary piece order did not executed successfully", result1.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - missing secondary piece type", result1.getExecutionDescription());
	}
	
	@Test
	public void testSecondaryPieceActionValidation() {

		Order anOrder = new Order("1", PieceType.ARMY, "currentLocationName", Action.SUPPORTS,
				null, PieceType.ARMY, "Georgia", null, "Burgundy", "France", "turnId", "gameId");

		OrderExecutionResult result1 = new OrderExecutionResult("1", "turnId", "gameId");

		myOrderValidator.validateSecondaryPieceAction(anOrder, result1);

		assertFalse("invalid secondary piece action not valid", result1.isValidOrder());
		assertFalse("invalid secondary piece order did not executed successfully", result1.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - missing secondary piece action", result1.getExecutionDescription());
	}
	
	@Test
	public void testSupportActionEverythingGood() {

		Order moveOrder = new Order("1", PieceType.ARMY, "Brest", Action.MOVESTO,
				"Paris", null, null, null, null, "France", "turnId", "gameId");
		Order supportOrder = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Paris", "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));
		existingPieces.put("Picardy", new Piece(null, "France", "Picardy", "turnId", "gameId", PieceType.ARMY));

		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("Brest", moveOrder);
		allOrders.put("Picardy", supportOrder);

		OrderExecutionResult result = new OrderExecutionResult("2", "turnId", "gameId");

		myOrderValidator.validateSupportAction(supportOrder, result, myGameMap, existingPieces, allOrders);

		assertTrue("support order is valid", result.isValidOrder());
		assertTrue("support order executed successfully", result.wasOrderExecutedSuccessfully());
		assertNull("description is correct", result.getExecutionDescription());
	}
	
	@Test
	public void testSupportActionNoCorrespondingMoveOrder() {

		Order supportOrder = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Paris", "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.ARMY));
		existingPieces.put("Picardy", new Piece(null, "France", "Picardy", "turnId", "gameId", PieceType.ARMY));

		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("Picardy", supportOrder);

		OrderExecutionResult result = new OrderExecutionResult("2", "turnId", "gameId");

		myOrderValidator.validateSupportAction(supportOrder, result, myGameMap, existingPieces, allOrders);

		assertFalse("support order is not valid", result.isValidOrder());
		assertFalse("support order was not executed successfully", result.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - no order was found in "
				+ supportOrder.getSecondaryCurrentLocationName() + " to support", result.getExecutionDescription());
	}
	
	@Test
	public void testSupportActionMismatchedPieceTypes() {

		Order moveOrder = new Order("1", PieceType.FLEET, "Brest", Action.MOVESTO,
				"Paris", null, null, null, null, "France", "turnId", "gameId");
		Order supportOrder = new Order("2", PieceType.ARMY, "Picardy", Action.SUPPORTS,
				null, PieceType.ARMY, "Brest", Action.MOVESTO, "Paris", "France", "turnId", "gameId");
		
		Map<String, Piece> existingPieces = new HashMap<String, Piece>();
		existingPieces.put("Brest", new Piece(null, "France", "Brest", "turnId", "gameId", PieceType.FLEET));
		existingPieces.put("Picardy", new Piece(null, "France", "Picardy", "turnId", "gameId", PieceType.ARMY));

		Map<String, Order> allOrders = new HashMap<String, Order>();
		allOrders.put("Brest", moveOrder);
		allOrders.put("Picardy", supportOrder);

		OrderExecutionResult result = new OrderExecutionResult("2", "turnId", "gameId");

		myOrderValidator.validateSupportAction(supportOrder, result, myGameMap, existingPieces, allOrders);

		assertFalse("support order is not valid", result.isValidOrder());
		assertFalse("support order was not executed successfully", result.wasOrderExecutedSuccessfully());
		assertEquals("description is correct", "Invalid order - support order piece type "
				+ supportOrder.getSecondaryPieceType() + " does not match supported order piece type "
				+ moveOrder.getPieceType(), result.getExecutionDescription());
	}


}
