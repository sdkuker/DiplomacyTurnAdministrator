package com.sdk.diplomacy.turnadmin.conflict;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConflictResolutionResults {

	private Set<StandoffProvince> standoffProvinces = new HashSet<StandoffProvince>();
	private Map<String, OrderResolutionResults> orderResolutionResults = new HashMap<String, OrderResolutionResults>();
	
	public Set<StandoffProvince> getStandoffProvinces() {
		return standoffProvinces;
	}
	public void setStandoffProvinces(Set<StandoffProvince> someStandoffProvinces) {
		standoffProvinces.clear();
		standoffProvinces.addAll(someStandoffProvinces);
	}
	public Map<String, OrderResolutionResults> getOrderResolutionResults() {
		return orderResolutionResults;
	}
	public void setOrderResolutionResults(Map<String, OrderResolutionResults> someOrderResolutionResults) {
		orderResolutionResults.clear();
		orderResolutionResults.putAll(someOrderResolutionResults);
	}
	
	
}
