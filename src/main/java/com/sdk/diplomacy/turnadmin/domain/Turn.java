package com.sdk.diplomacy.turnadmin.domain;

public class Turn {

	public enum Seasons {
		SPRING, FALL
	}
	
	public enum Statuss {
		OPEN, COMPLETE
	}
	
	private String id;
	private String gameId;
	private Seasons season;
	private int year;
	private Statuss status;
	
	public Turn(String id, String gameId, Seasons season, int year, Statuss status) {
		super();
		this.id = id;
		this.gameId = gameId;
		this.season = season;
		this.year = year;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public Seasons getSeason() {
		return season;
	}

	public void setSeason(Seasons season) {
		this.season = season;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Statuss getStatus() {
		return status;
	}

	public void setStatus(Statuss status) {
		this.status = status;
	}
	
	
}
