package com.sdk.diplomacy.turnadmin.map;

import java.util.HashMap;
import java.util.Map;

public class Region {

	public enum RegionType {
		WATER, COASTAL, INLAND
	}
	
	private String name;
	private RegionType type;
	private Map<String, Region> boarderingRegions = new HashMap<String, Region>();
	
	public Region(String name, RegionType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public RegionType getType() {
		return type;
	}

	public Map<String, Region> getBoarderingRegions() {
		return boarderingRegions;
	}

	protected void setBoarderingRegions(Map<String, Region> boarderingRegions) {
		this.boarderingRegions = boarderingRegions;
	}
	
}
