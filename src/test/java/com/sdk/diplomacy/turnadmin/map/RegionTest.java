package com.sdk.diplomacy.turnadmin.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sdk.diplomacy.turnadmin.map.Region.RegionType;

public class RegionTest {

	@Test
	public void testGetBoarderingRegionsOfType() {
		
		Region regionUnderTest = new Region("Region1", RegionType.COASTAL);
		Region borderingWaterRegion1 = new Region ("Region2", RegionType.WATER);
		Region borderingWaterRegion2 = new Region("Region3", RegionType.WATER);
		Region borderingInlandRegion1 = new Region("Region4", RegionType.INLAND);
		
		Map<String, Region> borderingRegions = new HashMap<String, Region>();
		borderingRegions.put("Region2", borderingWaterRegion1);
		borderingRegions.put("Region3", borderingWaterRegion2);
		borderingRegions.put("Region4", borderingInlandRegion1);
		
		regionUnderTest.setBoarderingRegions(borderingRegions);
		
		Map<String, Region> borderingWaterRegions = regionUnderTest.getBoarderingRegionsOfType(RegionType.WATER);
		assertEquals("number of bordering water regions", 2, borderingWaterRegions.size());
		assertNotNull("region2 should be bordering water region", borderingWaterRegions.get("Region2"));
		assertNotNull("region3 should be bordering water region", borderingWaterRegions.get("Region3"));
		
		Map<String, Region> borderingInlandRegions = regionUnderTest.getBoarderingRegionsOfType(RegionType.INLAND);
		assertEquals("number of bordering inland regions", 1, borderingInlandRegions.size());
		assertNotNull("Region4 should be bordering inland", borderingInlandRegions.get("Region4"));

	}
}
