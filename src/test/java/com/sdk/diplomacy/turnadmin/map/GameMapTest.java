package com.sdk.diplomacy.turnadmin.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.sdk.diplomacy.turnadmin.map.Region.RegionType;

public class GameMapTest {
	
	@Test
	public void testAddRegionWithAllArguments() {
		
		GameMap myMap = new GameMap();
		
		assertEquals("no regions to start with", 0, myMap.regions.size());
		
		Map<String, Region> myRegions = new HashMap<String, Region>();
		myMap.addRegion("regionName", RegionType.INLAND, myRegions);
		
		assertEquals("map regions were not updated", 0, myMap.regions.size());
		
		assertEquals("region was added to the right map", 1, myRegions.size());
		assertNotNull("region was added to the right place in the map", myRegions.get("regionName"));
		assertEquals("the region name is right", "regionName", myRegions.get("regionName").getName());
		assertEquals("the region type is right", RegionType.INLAND, myRegions.get("regionName").getType());
		
	}
	
	@Test
	public void testAddRegionMissingArguments() {
		
		GameMap myMap = new GameMap();
		
		Map<String, Region> myRegions = new HashMap<String, Region>();
		
		myMap.addRegion(null, RegionType.INLAND, myRegions);
		assertEquals("region was not added", 0, myRegions.size());
		
		myMap.addRegion("region name", null, myRegions);
		assertEquals("region was not added", 0, myRegions.size());

		myMap.addRegion("region name", RegionType.INLAND, null);
		assertEquals("region was not added", 0, myRegions.size());

	}
	
	@Test
	public void testDefineRegions() {
		
		GameMap myMap = new GameMap();
		Map<String, Region> regions = myMap.defineRegions();
		
		assertNotNull("a map of regions was created", regions);
		assertEquals("the map has the right number of entries", 82, regions.size());
	}
	
	@Test
	public void testAddingProvinceWithAllArguments() {
		
		GameMap myMap = new GameMap();
		
		Map<String, Province> provinceMap = new HashMap<String, Province>();
		
		myMap.addProvince("myProvinceName", new Region("region1", Region.RegionType.COASTAL), new Region("region2", Region.RegionType.INLAND), null, provinceMap);
		
		assertEquals("province was added", 1, provinceMap.size());
		assertNotNull("was added with the right key", provinceMap.get("myProvinceName"));
		assertEquals("province has the right number of regions", 2, provinceMap.get("myProvinceName").getRegions().size());
		assertEquals("province has the first region added properly", "region1", provinceMap.get("myProvinceName").getRegions().get("region1").getName());
		assertEquals("province has the second region added properly", "region2", provinceMap.get("myProvinceName").getRegions().get("region2").getName());
	}
	
	@Test
	public void testAddingProvinceMissingArguments() {
		
		GameMap myMap = new GameMap();
		
		Map<String, Province> provinceMap = new HashMap<String, Province>();
		
		myMap.addProvince(null, new Region("region1", Region.RegionType.COASTAL), new Region("region2", Region.RegionType.INLAND), null, provinceMap);
		assertEquals("missing province name was not added", 0, provinceMap.size());
		
		myMap.addProvince("provinceName", null, new Region("region2", Region.RegionType.INLAND), null, provinceMap);
		assertEquals("missing region1 name was not added", 0, provinceMap.size());
		
		myMap.addProvince("provinceName", new Region("region1", Region.RegionType.COASTAL), new Region("region2", Region.RegionType.INLAND), null, null);
		assertEquals("missing map name was not added", 0, provinceMap.size());

		myMap.addProvince("myProvinceName", new Region("region1", Region.RegionType.COASTAL), null, null, provinceMap);
		assertEquals("missing region2 was added", 1, provinceMap.size());


	}
	
	@Test
	public void testDefineProvinces() {
		
		GameMap myMap = new GameMap();
		myMap.regions = myMap.defineRegions();
		
		Map<String, Province> myProvinces = myMap.defineProvinces();
		
		assertNotNull("a map of provinces was created", myProvinces);
		assertEquals("the map of provinces has the right number of entries", 76, myProvinces.size());
		
		int numberOfSingleRegionProvinces = 0;
		int numberOfTripleRegionProvinces = 0;
		
		for (Entry<String, Province> anEntry : myProvinces.entrySet()) {
			if (anEntry.getValue().getRegions().size() == 1) {
				if (anEntry.getValue().getRegions().get(anEntry.getKey()) != null) {
					numberOfSingleRegionProvinces++;
				}
			} else {
				if (anEntry.getValue().getRegions().size() == 3) {
					if (anEntry.getKey() == "Bulgaria" || anEntry.getKey() == "Spain" || anEntry.getKey() == "St_Petersburg") {
						numberOfTripleRegionProvinces++;
					}
				}
			}
		}
		
		assertEquals("the single region provinces were created properly", 73, numberOfSingleRegionProvinces);
		assertEquals("the triple region provinces were created properly", 3, numberOfTripleRegionProvinces);
	}
	
	@Test
	public void testSetBoarderingRegions() {
		
		GameMap myMap = new GameMap();
		
		Region region1 = new Region("region1", RegionType.COASTAL);
		Region region2 = new Region("region2", RegionType.COASTAL);
		Region region3 = new Region("region3", RegionType.COASTAL);
		Region region4 = new Region("region4", RegionType.COASTAL);
		
		Map<String, Region> mapOfAllRegions = new HashMap<String, Region>();
		mapOfAllRegions.put("region1", region1);
		mapOfAllRegions.put("region2", region2);
		mapOfAllRegions.put("region3", region3);
		mapOfAllRegions.put("region4", region4);
		
		Set<String> boarderingRegionNames = new HashSet<String>();
		boarderingRegionNames.add("region2");
		boarderingRegionNames.add("region3");
		
		assertEquals("no boardering regions initially region 1", 0, region1.getBoarderingRegions().size());
		assertEquals("no boardering regions initially region 2", 0, region2.getBoarderingRegions().size());
		assertEquals("no boardering regions initially region 3", 0, region3.getBoarderingRegions().size());
		assertEquals("no boardering regions initially region 4", 0, region4.getBoarderingRegions().size());
		
		myMap.setBoarderingRegions("region1", boarderingRegionNames, mapOfAllRegions);
		
		assertEquals("no boardering regions at end region 2", 0, region2.getBoarderingRegions().size());
		assertEquals("no boardering regions at end region 3", 0, region3.getBoarderingRegions().size());
		assertEquals("no boardering regions at end region 4", 0, region4.getBoarderingRegions().size());

		assertEquals("right number of boardering regions set", 2, region1.getBoarderingRegions().size());
		assertEquals("first boardering region set properly", region2, region1.getBoarderingRegions().get("region2"));
		assertEquals("second boardering region set properly", region3, region1.getBoarderingRegions().get("region3"));
		
	}
	
	@Test
	public void testPopulatingBoarderingRegions() {
		
		GameMap myMap = new GameMap();
		Map<String, Region> myRegions = myMap.defineRegions();
		
		boolean allBoarderingRegionsPopulatedProperly = myMap.populateBorderingRegions(myRegions);
		
		assertTrue("all boardering regsions populated properly", allBoarderingRegionsPopulatedProperly);
		assertEquals("NAO", 5, myRegions.get("North_Atlantic_Ocean").getBoarderingRegions().size());
		
		for (Region aRegion: myRegions.values()) {
			assertTrue("boarding regions are not set for: " + aRegion.getName(), aRegion.getBoarderingRegions().size() > 0);
		}
		
		for (Entry<String, Region> myMapEntry : myRegions.entrySet()) {
			for (Region myBoarderingRegion : myMapEntry.getValue().getBoarderingRegions().values()) {
				assertNotNull("Receprical boardering region not found for: " + myMapEntry.getKey() + " - " + myBoarderingRegion.getName(),
						myRegions.get(myBoarderingRegion.getName()).getBoarderingRegions().get(myMapEntry.getKey()));
			}
		}
	}
	
	@Test
	public void testGetProviceContainingRegionByName() {
		
		GameMap myMap = new GameMap();
		myMap.initialize();
		
		Province provinceWithOneRegion = myMap.getProvinceContainingRegionByName("Paris");
		assertNotNull("province with one region found", provinceWithOneRegion);
		assertEquals("correct province with one region was found", "Paris", provinceWithOneRegion.getName());

		Province provinceWithTwoRegions = myMap.getProvinceContainingRegionByName("Spain_(sc)");
		assertNotNull("province with two regions found", provinceWithTwoRegions);
		assertEquals("correct province with two regions was found", "Spain", provinceWithTwoRegions.getName());

	}

}
