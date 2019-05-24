package com.sdk.diplomacy.turnadmin.domain;

import com.sdk.diplomacy.turnadmin.domain.Piece.PieceType;

public class Order {

	public enum Action {
		HOLDS, MOVESTO, CONVOYS, SUPPORTS;
		public static Action from(String text) {
			if (text == null) {
				return null;
			} else {
				return valueOf(text.toUpperCase());
			}
		}
	}
	
	private String id;
	private PieceType pieceType;
	private String currentLocationName;
	private Action action;
	private String endingLocationName;
	private PieceType secondaryPieceType;
	private String secondaryCurrentLocationName;
	private Action secondaryAction;
	private String secondaryEndingLocationName;
	private String owningCountryName;
	private String turnId;
	private String gameId;
	
	public Order(String id, PieceType pieceType, String currentLocationName, Action action, String endingLocationName,
			PieceType secondaryPieceType, String secondaryCurrentLocationName, Action secondaryAction,
			String secondaryEndingLocationName, String owningCountryName, String turnId, String gameId) {
		super();
		this.id = id;
		this.pieceType = pieceType;
		this.currentLocationName = currentLocationName;
		this.action = action;
		this.endingLocationName = endingLocationName;
		this.secondaryPieceType = secondaryPieceType;
		this.secondaryCurrentLocationName = secondaryCurrentLocationName;
		this.secondaryAction = secondaryAction;
		this.secondaryEndingLocationName = secondaryEndingLocationName;
		this.owningCountryName = owningCountryName;
		this.turnId = turnId;
		this.gameId = gameId;
	}

	public String getDescription() {
		
		StringBuilder aBuilder = new StringBuilder();
		aBuilder.append(pieceType);
		aBuilder.append(" in ");
		aBuilder.append(currentLocationName);
		if (action == Action.HOLDS) {
			aBuilder.append(" Holds");
		} else {
			if (action == Action.MOVESTO) {
				aBuilder.append(" moves to ");
				aBuilder.append(endingLocationName);
			} else {
				if (action == Action.SUPPORTS) {
					aBuilder.append(" supports ");
					aBuilder.append(secondaryPieceType);
					aBuilder.append(" in ");
					aBuilder.append(secondaryCurrentLocationName);
					if (secondaryAction == Action.HOLDS) {
						aBuilder.append(" Holds");
					} else {
						if (secondaryAction == Action.MOVESTO) {
							aBuilder.append(" moves to ");
							aBuilder.append(secondaryEndingLocationName);
						} else {
							if (secondaryAction == Action.CONVOYS) {
								aBuilder.append(" convoys to ");
								aBuilder.append(secondaryEndingLocationName);
							}
						}
					}
				} else {
					if (action == Action.CONVOYS) {
						aBuilder.append(" convoys ");
						aBuilder.append(secondaryPieceType);
						aBuilder.append(" in ");
						aBuilder.append(secondaryCurrentLocationName);
						aBuilder.append(" to ");
						aBuilder.append(secondaryEndingLocationName);
					} else {
						aBuilder.append(" unknown action");
					}
				}
			}
		}
		
		return aBuilder.toString();
	}
	public String getId() {
		return id;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public String getCurrentLocationName() {
		return currentLocationName;
	}

	public Action getAction() {
		return action;
	}

	public String getEndingLocationName() {
		return endingLocationName;
	}

	public PieceType getSecondaryPieceType() {
		return secondaryPieceType;
	}

	public String getSecondaryCurrentLocationName() {
		return secondaryCurrentLocationName;
	}

	public Action getSecondaryAction() {
		return secondaryAction;
	}

	public String getSecondaryEndingLocationName() {
		return secondaryEndingLocationName;
	}

	public String getOwningCountryName() {
		return owningCountryName;
	}

	public String getTurnId() {
		return turnId;
	}

	public String getGameId() {
		return gameId;
	}
	
	public boolean requiresSecondaryOrder() {
		
		if (getAction() != null) {
			return Action.CONVOYS == getAction() || Action.SUPPORTS == getAction();
		} else {
			return false;
		}
	}
	
	public String getEffectiveEndingLocationName() {
		
		if (requiresSecondaryOrder()) {
			return getSecondaryEndingLocationName();
		} else {
			return getEndingLocationName();
		}
	}
	
}
