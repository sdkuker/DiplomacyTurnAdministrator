package com.sdk.diplomacy.turnadmin.domain;

public class Piece {

	public enum PieceType {
		FLEET, ARMY;
		public static PieceType from(String text) {
			if (text == null) {
				return null;
			} else {
				return valueOf(text.toUpperCase());
			}
		}
	}

	private String id;
	private String owningCountryName; // this is which country owns the piece
	private String nameOfLocationAtBeginningOfTurn;
	private String nameOfLocationAtEndOfTurn;
	private boolean mustRetreatAtEndOfTurn;
	private String turnId;
	private String gameId;
	private PieceType type;

	public Piece(String id, String owningCountryName, String nameOfLocationAtBeginningOfTurn, String turnId,
			String gameId, PieceType type) {
		super();
		this.id = id;
		this.owningCountryName = owningCountryName;
		this.nameOfLocationAtBeginningOfTurn = nameOfLocationAtBeginningOfTurn;
		this.turnId = turnId;
		this.gameId = gameId;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getOwningCountryName() {
		return owningCountryName;
	}

	public String getNameOfLocationAtBeginningOfTurn() {
		return nameOfLocationAtBeginningOfTurn;
	}

	public String getTurnId() {
		return turnId;
	}

	public String getGameId() {
		return gameId;
	}

	public PieceType getType() {
		return type;
	}

	public String getNameOfLocationAtEndOfTurn() {
		return nameOfLocationAtEndOfTurn;
	}

	public void setNameOfLocationAtEndOfTurn(String nameOfLocationAtEndOfTurn) {
		this.nameOfLocationAtEndOfTurn = nameOfLocationAtEndOfTurn;
	}

	public boolean getMustRetreatAtEndOfTurn() {
		return mustRetreatAtEndOfTurn;
	}

	public void setMustRetreatAtEndOfTurn(boolean mustRetreatAtEndOfTurn) {
		this.mustRetreatAtEndOfTurn = mustRetreatAtEndOfTurn;
	}

}
