package com.sdk.diplomacy.turnadmin.map;

import java.util.HashMap;
import java.util.Map;

public class Province {

	private String name;
	private Map<String, Region> regions = new HashMap<String, Region>();
	
	public Province(String aName, Region region1, Region region2) {
		
		super();
		
		if (aName != null) {
			name = aName;
		} else {
			if (region1 != null) {
				name = region1.getName();
			}
		}
		if (region1 != null) {
			regions.put(region1.getName(), region1);
		}
		if (region2 != null) {
			regions.put(region2.getName(), region2);
		}
	}

	public String getName() {
		return name;
	}

	public Map<String, Region> getRegions() {
		return regions;
	}
	
}
