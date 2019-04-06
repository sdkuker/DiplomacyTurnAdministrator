package com.sdk.diplomacy.turnadmin.conflict;

import java.util.Map;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.OrderExecutionResult;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.domain.Order.Action;
import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;
import com.sdk.diplomacy.turnadmin.map.GameMap;
import com.sdk.diplomacy.turnadmin.map.Region;
import com.sdk.diplomacy.turnadmin.map.Region.RegionType;

public class OrderValidator {

	/*
	 * The keys to the existing pieces map and the all order map are the pieces
	 * current/starting location name of the piece or primary piece in the order
	 */
	public void validateOrder(Order anOrder, OrderExecutionResult anOrderResult, GameMap aGameMap,
			Map<String, Piece> existingPieces, Map<String, Order> allOrders) {

		validateId(anOrder, anOrderResult);
		validateTurnId(anOrder, anOrderResult);
		validateGameId(anOrder, anOrderResult);
		validatePieceType(anOrder, anOrderResult);
		validateCurrentLocationName(anOrder, anOrderResult, aGameMap, existingPieces);
		validatePieceAndTypeInInitialLocation(anOrder, anOrderResult, existingPieces);

		if (anOrder.getAction() != null) {
			switch (anOrder.getAction()) {
			case CONVOYS:
				// TODO validateConvoyAction(anOrder, anOrderResult, aGameMap);
				break;
			case HOLDS:
				validateHoldAction(anOrder, anOrderResult, aGameMap);
				break;
			case MOVESTO:
				validateMovesToAction(anOrder, anOrderResult, aGameMap);
				break;
			case SUPPORTS:
				validateSupportAction(anOrder, anOrderResult, aGameMap, existingPieces, allOrders);
				break;
			default:
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription("Invalid order - unknown action");
				break;
			}
		} else {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing action");
		}
	}

	public void validatePieceAndTypeInInitialLocation(Order anOrder, OrderExecutionResult anOrderResult,
			Map<String, Piece> existingPieces) {

		Piece existingPiece = existingPieces.get(anOrder.getCurrentLocationName());
		if (existingPiece != null) {
			if (anOrder.getPieceType() != existingPiece.getType()) {
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription(
						"Invalid order - piece type in the order is not the same as the piece already occupying the current location");
			}

		} else {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - no piece occupies the current location");
		}
	}

	public void validateId(Order anOrder, OrderExecutionResult anOrderResult) {
		if (anOrder.getId() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - id");
		}
	}

	public void validateTurnId(Order anOrder, OrderExecutionResult anOrderResult) {
		if (anOrder.getTurnId() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - turn id");
		}
	}

	public void validateGameId(Order anOrder, OrderExecutionResult anOrderResult) {
		if (anOrder.getGameId() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - game id");
		}
	}

	public void validatePieceType(Order anOrder, OrderExecutionResult anOrderResult) {
		if (anOrder.getPieceType() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing piece type");
		}
	}

	public void validateSecondaryPieceType(Order anOrder, OrderExecutionResult anOrderResult) {
		if (anOrder.getSecondaryPieceType() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing secondary piece type");
		}
	}

	public void validateCurrentLocationName(Order anOrder, OrderExecutionResult anOrderResult, GameMap aGameMap,
			Map<String, Piece> existingPieces) {
		if (anOrder.getCurrentLocationName() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing current location");
		} else {
			if (aGameMap.getRegion(anOrder.getCurrentLocationName()) == null) {
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription(
						"Invalid order - unknown current location: " + anOrder.getCurrentLocationName());
			}
		}
	}

	public void validateEndingLocationName(Order anOrder, OrderExecutionResult anOrderResult, GameMap aGameMap) {
		if (anOrder.getEndingLocationName() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing ending location");
		} else {
			if (aGameMap.getRegion(anOrder.getCurrentLocationName()) == null) {
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription(
						"Invalid order - unknown ending location: " + anOrder.getCurrentLocationName());
			}
		}
	}

	public void validateSecondaryEndingLocationName(Order anOrder, OrderExecutionResult anOrderResult,
			GameMap aGameMap) {
		if (anOrder.getSecondaryEndingLocationName() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing secondary ending location");
		} else {
			if (aGameMap.getRegion(anOrder.getSecondaryEndingLocationName()) == null) {
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription("Invalid order - unknown secondary ending location: "
						+ anOrder.getSecondaryEndingLocationName());
			}
		}
	}

	public void validateHoldAction(Order anOrder, OrderExecutionResult anOrderResult, GameMap aGameMap) {

		// ending location name is optional. If provided it must be the same as the
		// current location
		if (anOrder.getEndingLocationName() != null) {
			if (anOrder.getEndingLocationName() != anOrder.getCurrentLocationName()) {
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription("Invalid order - must hold in current location");
			}
		}
		if (anOrder.getSecondaryAction() != null || anOrder.getSecondaryCurrentLocationName() != null
				|| anOrder.getSecondaryEndingLocationName() != null || anOrder.getSecondaryPieceType() != null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult
					.setExecutionDescription("Invalid order - no secondary fields can be specified for hold actions");
		}
	}

	public void validateMovesToAction(Order anOrder, OrderExecutionResult anOrderResult, GameMap aGameMap) {

		validateEndingLocationName(anOrder, anOrderResult, aGameMap);

		if (anOrderResult.isValidOrder()) {
			// if current and ending are coastal and it's an army it might be convoyed.
			// That's okay.
			// else, the current and ending have to be adjacent
			boolean armyMightBeConvoying = false;

			if (anOrder.getPieceType() == PieceType.ARMY) {
				Region currentRegion = aGameMap.getRegion(anOrder.getCurrentLocationName());
				Region endingRegion = aGameMap.getRegion(anOrder.getEndingLocationName());
				if (currentRegion.getType() == RegionType.COASTAL && endingRegion.getType() == RegionType.COASTAL) {
					armyMightBeConvoying = true;
				}
			}

			Region endingLocationBordersCurrentLocation = aGameMap.getRegion(anOrder.getCurrentLocationName())
					.getBoarderingRegions().get(anOrder.getEndingLocationName());
			if (endingLocationBordersCurrentLocation == null && armyMightBeConvoying == false) {
				anOrderResult.setIsValidOrder(false);
				anOrderResult
						.setExecutionDescription("Invalid order - ending location does not border current location");
			}
		}

		if (anOrder.getSecondaryAction() != null || anOrder.getSecondaryCurrentLocationName() != null
				|| anOrder.getSecondaryEndingLocationName() != null || anOrder.getSecondaryPieceType() != null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription(
					"Invalid order - no secondary fields can be specified for move to actions");
		}

		if (anOrderResult.isValidOrder()) {
			switch (aGameMap.getRegion(anOrder.getEndingLocationName()).getType()) {
			case WATER:
				if (anOrder.getPieceType() == PieceType.ARMY) {
					anOrderResult.setIsValidOrder(false);
					anOrderResult.setExecutionDescription("Invalid order - armies can't move to water provinces");
				}
				break;
			case INLAND:
				if (anOrder.getPieceType() == PieceType.FLEET) {
					anOrderResult.setIsValidOrder(false);
					anOrderResult.setExecutionDescription("Invalid order - fleets can't move to inland provinces");
				}
				break;
			case COASTAL:
				// both piece types can move here
				break;
			}
		}
	}

	public void validateSupportAction(Order anOrder, OrderExecutionResult anOrderResult, GameMap aGameMap,
			Map<String, Piece> existingPieces, Map<String, Order> allOrders) {

		validateSecondaryOrderFields(anOrder, anOrderResult, aGameMap, existingPieces);
		if (anOrderResult.isValidOrder()) {
			// validate that the piece in the current location can move to the place
			// it's providing support to
			Order currentPieceMoveToSupportedLocationOrder = new Order("tempId", anOrder.getPieceType(),
					anOrder.getCurrentLocationName(), Action.MOVESTO, anOrder.getSecondaryEndingLocationName(), null,
					null, null, null, anOrder.getOwningCountryName(), anOrder.getTurnId(), anOrder.getGameId());
			OrderExecutionResult aResult = new OrderExecutionResult("tempId",
					currentPieceMoveToSupportedLocationOrder.getTurnId(),
					currentPieceMoveToSupportedLocationOrder.getGameId());
			validateMovesToAction(currentPieceMoveToSupportedLocationOrder, aResult, aGameMap);
			if (aResult.isValidOrder()) {
				// validate that the support order matches the order it's trying to support
				Order supportedOrder = allOrders.get(anOrder.getSecondaryCurrentLocationName());
				if (supportedOrder != null) {
					if (anOrder.getSecondaryPieceType() != supportedOrder.getPieceType()) {
						anOrderResult.setIsValidOrder(false);
						anOrderResult.setExecutionDescription("Invalid order - support order piece type "
								+ anOrder.getSecondaryPieceType() + " does not match supported order piece type "
								+ supportedOrder.getPieceType());
					} else {
						if (anOrder.getSecondaryAction() != supportedOrder.getAction()) {
							anOrderResult.setIsValidOrder(false);
							anOrderResult.setExecutionDescription("Invalid order - support order action "
									+ anOrder.getSecondaryAction() + " does not match supported order action "
									+ supportedOrder.getAction());

						} else {
							if (anOrder.getSecondaryEndingLocationName() != supportedOrder.getEndingLocationName()) {
								anOrderResult.setIsValidOrder(false);
								anOrderResult.setExecutionDescription("Invalid order - support order ending location "
										+ anOrder.getSecondaryEndingLocationName() + " does not match supported order ending location "
										+ supportedOrder.getEndingLocationName());
							}
						}
					}

				} else {
					anOrderResult.setIsValidOrder(false);
					anOrderResult.setExecutionDescription("Invalid order - no order was found in "
							+ anOrder.getSecondaryCurrentLocationName() + " to support");
				}

			} else {
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription(
						aResult.getExecutionDescription() + " - supporting piece can't provide support");
			}
		}
	}

	public void validateSecondaryOrderFields(Order anOrder, OrderExecutionResult anOrderResult, GameMap aGameMap,
			Map<String, Piece> existingPieces) {

		validateSecondaryPieceType(anOrder, anOrderResult);
		validateSecondaryCurrentLocationName(anOrder, anOrderResult, aGameMap, existingPieces);
		validateSecondaryPieceAction(anOrder, anOrderResult);
		validateSecondaryEndingLocationName(anOrder, anOrderResult, aGameMap);
	}

	public void validateSecondaryPieceAction(Order anOrder, OrderExecutionResult anOrderResult) {

		if (anOrder.getSecondaryAction() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing secondary piece action");
		}
	}

	public void validateSecondaryCurrentLocationName(Order anOrder, OrderExecutionResult anOrderResult,
			GameMap aGameMap, Map<String, Piece> existingPieces) {
		if (anOrder.getSecondaryCurrentLocationName() == null) {
			anOrderResult.setIsValidOrder(false);
			anOrderResult.setExecutionDescription("Invalid order - missing secondary current location");
		} else {
			if (aGameMap.getRegion(anOrder.getSecondaryCurrentLocationName()) == null) {
				anOrderResult.setIsValidOrder(false);
				anOrderResult.setExecutionDescription("Invalid order - unknown secondary current location: "
						+ anOrder.getSecondaryCurrentLocationName());
			}
		}
	}

}
