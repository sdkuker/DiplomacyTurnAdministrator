package com.sdk.diplomacy.turnadmin.domain;

public class Turn {

	public enum Seasons {
		SPRING, FALL
	}

	public enum Statuss {
		OPEN, COMPLETE
	}

	public enum Phases {
		DIPLOMATIC {
			@Override
			public Phases nextPhase() {
				return ORDER_WRITING;
			}
		}, 
		ORDER_WRITING {
			@Override
			public Phases nextPhase() {
				return ORDER_RESOLUTION;
			}
		}, 
		ORDER_RESOLUTION {
			@Override
			public Phases nextPhase() {
				return RETREAT_AND_DISBANDING;
			}
		}, 
		RETREAT_AND_DISBANDING {
			@Override
			public Phases nextPhase() {
				return GAINING_AND_LOSING_UNITS;
			}
		}, 
		GAINING_AND_LOSING_UNITS {
			@Override
			public Phases nextPhase() {
				return DIPLOMATIC;
			}
		};
		
		public abstract Phases nextPhase();
	}

	private String id;
	private String gameId;
	private Seasons season;
	private long year;
	private Statuss status;
	private Phases phase;

	public Turn(String id, String gameId, Seasons season, long year, Statuss status, Phases aPhase) {
		super();
		this.id = id;
		this.gameId = gameId;
		this.season = season;
		this.year = year;
		this.status = status;
		this.phase = aPhase;
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

	public long getYear() {
		return year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	public Statuss getStatus() {
		return status;
	}

	public void setStatus(Statuss status) {
		this.status = status;
	}

	public Phases getPhase() {
		return phase;
	}

}
