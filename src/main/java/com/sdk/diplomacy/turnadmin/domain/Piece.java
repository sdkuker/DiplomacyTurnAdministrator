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
	private String owningCountryName;
	private String gameId;
	private PieceType type;
	private PieceLocation pieceLocation;

	public Piece(String id, String owningCountryName,
			String gameId, PieceType type, PieceLocation aPieceLocation) {
		super();
		this.id = id;
		this.owningCountryName = owningCountryName;
		this.gameId = gameId;
		this.type = type;
		this.pieceLocation = aPieceLocation;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String anId) {
		id = anId;
	}

	public String getOwningCountryName() {
		return owningCountryName;
	}

	public String getNameOfLocationAtBeginningOfPhase() {
		return pieceLocation.getNameOfLocationAtBeginningOfPhase();
	}
	
	public void setNameOfLocationAtBeginningOfPhase(String nameOfLocationAtBeginningOfPhase) {
		pieceLocation.setNameOfLocationAtBeginningOfPhase(nameOfLocationAtBeginningOfPhase);
	}

	public String getGameId() {
		return gameId;
	}

	public PieceType getType() {
		return type;
	}

	public String getNameOfLocationAtEndOfPhase() {
		return pieceLocation.getNameOfLocationAtEndOfPhase();
	}

	public void setNameOfLocationAtEndOfPhase(String nameOfLocationAtEndOfPhase) {
		pieceLocation.setNameOfLocationAtEndOfPhase(nameOfLocationAtEndOfPhase);
	}

	public boolean getMustRetreatAtEndOfTurn() {
		return pieceLocation.isMustRetreatAtEndOfTurn();
	}

	public void setMustRetreatAtEndOfTurn(boolean mustRetreatAtEndOfTurn) {
		pieceLocation.setMustRetreatAtEndOfTurn(getMustRetreatAtEndOfTurn());
	}

	public PieceLocation getPieceLocation() {
		return pieceLocation;
	}

	public void setPieceLocation(PieceLocation pieceLocation) {
		this.pieceLocation = pieceLocation;
	}

}
