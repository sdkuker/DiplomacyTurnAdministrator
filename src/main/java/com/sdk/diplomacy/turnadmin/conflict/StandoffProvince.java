package com.sdk.diplomacy.turnadmin.conflict;

public class StandoffProvince {

	private String id;
	private String provinceName;
	private String turnId;
	private String gameId;
	
	public StandoffProvince(String id, String provinceName, String turnId, String gameId) {
		super();
		this.id = id;
		this.provinceName = provinceName;
		this.turnId = turnId;
		this.gameId = gameId;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String anId) {
		id = anId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public String getTurnId() {
		return turnId;
	}

	public String getGameId() {
		return gameId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gameId == null) ? 0 : gameId.hashCode());
		result = prime * result + ((provinceName == null) ? 0 : provinceName.hashCode());
		result = prime * result + ((turnId == null) ? 0 : turnId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StandoffProvince other = (StandoffProvince) obj;
		if (gameId == null) {
			if (other.gameId != null)
				return false;
		} else if (!gameId.equals(other.gameId))
			return false;
		if (provinceName == null) {
			if (other.provinceName != null)
				return false;
		} else if (!provinceName.equals(other.provinceName))
			return false;
		if (turnId == null) {
			if (other.turnId != null)
				return false;
		} else if (!turnId.equals(other.turnId))
			return false;
		return true;
	}
	
	
}
