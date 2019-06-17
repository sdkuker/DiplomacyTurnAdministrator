package com.sdk.diplomacy.turnadmin.domain;

import com.sdk.diplomacy.turnadmin.domain.Turn.Phases;

public class PieceLocation {
	
	private String id;
	private String pieceId;
	private String turnId;
	private Phases turnPhase;
	private String gameId;
	private String nameOfLocationAtBeginningOfPhase;
	private String nameOfLocationAtEndOfPhase;
	private boolean mustRetreatAtEndOfTurn;
	
	public PieceLocation(String id, String pieceId, String turnId, Phases turnPhase, String gameId,
			String nameOfLocationAtBeginningOfPhase, String nameOfLocationAtEndOfPhase,
			boolean mustRetreatAtEndOfTurn) {
		super();
		this.id = id;
		this.pieceId = pieceId;
		this.turnId = turnId;
		this.turnPhase = turnPhase;
		this.gameId = gameId;
		this.nameOfLocationAtBeginningOfPhase = nameOfLocationAtBeginningOfPhase;
		this.nameOfLocationAtEndOfPhase = nameOfLocationAtEndOfPhase;
		this.mustRetreatAtEndOfTurn = mustRetreatAtEndOfTurn;
	}

	public String getId() {
		return id;
	}

	public String getPieceId() {
		return pieceId;
	}

	public String getTurnId() {
		return turnId;
	}

	public Phases getTurnPhase() {
		return turnPhase;
	}

	public String getGameId() {
		return gameId;
	}

	public String getNameOfLocationAtBeginningOfPhase() {
		return nameOfLocationAtBeginningOfPhase;
	}

	public String getNameOfLocationAtEndOfPhase() {
		return nameOfLocationAtEndOfPhase;
	}

	public boolean isMustRetreatAtEndOfTurn() {
		return mustRetreatAtEndOfTurn;
	}

	public void setNameOfLocationAtEndOfPhase(String nameOfLocationAtEndOfPhase) {
		this.nameOfLocationAtEndOfPhase = nameOfLocationAtEndOfPhase;
	}
	
	public void setNameOfLocationAtBeginningOfPhase(String nameOfLocationAtBeginningOfPhase) {
		this.nameOfLocationAtBeginningOfPhase = nameOfLocationAtBeginningOfPhase;
	}

	public void setMustRetreatAtEndOfTurn(boolean mustRetreatAtEndOfTurn) {
		this.mustRetreatAtEndOfTurn = mustRetreatAtEndOfTurn;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPieceId(String pieceId) {
		this.pieceId = pieceId;
	}
	
	
}
