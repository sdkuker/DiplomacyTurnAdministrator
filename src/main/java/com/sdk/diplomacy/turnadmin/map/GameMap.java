package com.sdk.diplomacy.turnadmin.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sdk.diplomacy.turnadmin.map.Region.RegionType;

public class GameMap {

	protected Map<String, Region> regions = new HashMap<String, Region>();
	protected Map<String, Province> provinces = new HashMap<String, Province>();

	public void initialize() {

		Map<String, Region> tempRegions = defineRegions();
		populateBorderingRegions(tempRegions);
		regions = tempRegions;
		provinces = defineProvinces();
	}
	
	public Region getRegion(String regionName) {
		
		return regions.get(regionName);
	}

	protected boolean populateBorderingRegions(Map<String, Region> myRegions) {

		boolean allBoarderingRegionsSetProperly = true;
		Set<String> boarderingRegionNames = new HashSet<String>();

		// oceans
		
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("Norwegian_Sea");
		boarderingRegionNames.add("Clyde");
		boarderingRegionNames.add("Liverpool");
		boarderingRegionNames.add("Irish_Sea");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("North_Atlantic_Ocean", boarderingRegionNames, myRegions);
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("North_Atlantic_Ocean");
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("Liverpool");
		boarderingRegionNames.add("Wales");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Irish_Sea", boarderingRegionNames, myRegions);
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("Irish_Sea");
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Wales");
		boarderingRegionNames.add("London");
		boarderingRegionNames.add("Belguim");
		boarderingRegionNames.add("Picardy");
		boarderingRegionNames.add("Brest");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("English_Channel", boarderingRegionNames, myRegions);
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Atlantic_Ocean");
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Barents_Sea");
		boarderingRegionNames.add("Clyde");
		boarderingRegionNames.add("Edinburgh");
		boarderingRegionNames.add("Norway");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Norwegian_Sea", boarderingRegionNames, myRegions);
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("Norwegian_Sea");
		boarderingRegionNames.add("Norway");
		boarderingRegionNames.add("St_Petersburg_(nc)");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Barents_Sea", boarderingRegionNames, myRegions);
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("Norwegian_Sea");
		boarderingRegionNames.add("Skagerrak");
		boarderingRegionNames.add("Heloland_Bight");
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("Edinburgh");
		boarderingRegionNames.add("Yorkshire");
		boarderingRegionNames.add("London");
		boarderingRegionNames.add("Holland");
		boarderingRegionNames.add("Belguim");
		boarderingRegionNames.add("Norway");
		boarderingRegionNames.add("Denmark");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("North_Sea", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Denmark");
		boarderingRegionNames.add("Kiel");
		boarderingRegionNames.add("Holland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Heloland_Bight", boarderingRegionNames, myRegions);
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Norway");
		boarderingRegionNames.add("Sweden");
		boarderingRegionNames.add("Denmark");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Skagerrak", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Skagerrak");
		boarderingRegionNames.add("Gulf_of_Bothnia");
		boarderingRegionNames.add("Sweden");
		boarderingRegionNames.add("Denmark");
		boarderingRegionNames.add("Kiel");
		boarderingRegionNames.add("Berlin");
		boarderingRegionNames.add("Prussia");
		boarderingRegionNames.add("Livonia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Baltic_Sea", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Sweden");
		boarderingRegionNames.add("Finland");
		boarderingRegionNames.add("St_Petersburg_(sc)");
		boarderingRegionNames.add("Livonia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Gulf_of_Bothnia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Atlantic_Ocean");
		boarderingRegionNames.add("Irish_Sea");
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("Western_Mediterranean");
		boarderingRegionNames.add("Brest");
		boarderingRegionNames.add("Gascony");
		boarderingRegionNames.add("Spain_(nc)");
		boarderingRegionNames.add("Portugal");
		boarderingRegionNames.add("North_Africa");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Mid_Atlantic_Ocean", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("Gulf_of_Lyon");
		boarderingRegionNames.add("Tyrrhenian_Sea");
		boarderingRegionNames.add("Spain_(sc)");
		boarderingRegionNames.add("North_Africa");
		boarderingRegionNames.add("Tunis");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Western_Mediterranean", boarderingRegionNames, myRegions);


		boarderingRegionNames.clear();
		boarderingRegionNames.add("Western_Mediterranean");
		boarderingRegionNames.add("Tyrrhenian_Sea");
		boarderingRegionNames.add("Spain_(sc)");
		boarderingRegionNames.add("Marseilles");
		boarderingRegionNames.add("Piedmont");
		boarderingRegionNames.add("Tuscany");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Gulf_of_Lyon", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Western_Mediterranean");
		boarderingRegionNames.add("Gulf_of_Lyon");
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Tuscany");
		boarderingRegionNames.add("Rome");
		boarderingRegionNames.add("Naples");
		boarderingRegionNames.add("Tunis");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Tyrrhenian_Sea", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Tyrrhenian_Sea");
		boarderingRegionNames.add("Adriatic_Sea");
		boarderingRegionNames.add("Aegean_Sea");
		boarderingRegionNames.add("Eastern_Mediterranean");
		boarderingRegionNames.add("Tunis");
		boarderingRegionNames.add("Naples");
		boarderingRegionNames.add("Apulia");
		boarderingRegionNames.add("Albania");
		boarderingRegionNames.add("Greece");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Ionian_Sea", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Apulia");
		boarderingRegionNames.add("Venice");
		boarderingRegionNames.add("Trieste");
		boarderingRegionNames.add("Albania");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Adriatic_Sea", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Black_Sea");
		boarderingRegionNames.add("Eastern_Mediterranean");
		boarderingRegionNames.add("Greece");
		boarderingRegionNames.add("Bulgaria_(sc)");
		boarderingRegionNames.add("Constantinople");
		boarderingRegionNames.add("Smyrna");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Aegean_Sea", boarderingRegionNames, myRegions);
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Aegean_Sea");
		boarderingRegionNames.add("Smyrna");
		boarderingRegionNames.add("Syria");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Eastern_Mediterranean", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Aegean_Sea");
		boarderingRegionNames.add("Constantinople");
		boarderingRegionNames.add("Bulgaria_(ec)");
		boarderingRegionNames.add("Rumania");
		boarderingRegionNames.add("Sevastopol");
		boarderingRegionNames.add("Armenia");
		boarderingRegionNames.add("Ankara");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Black_Sea", boarderingRegionNames, myRegions);

		// coastal
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Atlantic_Ocean");
		boarderingRegionNames.add("Norwegian_Sea");
		boarderingRegionNames.add("Edinburgh");
		boarderingRegionNames.add("Liverpool");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Clyde", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Atlantic_Ocean");
		boarderingRegionNames.add("Irish_Sea");
		boarderingRegionNames.add("Clyde");
		boarderingRegionNames.add("Edinburgh");
		boarderingRegionNames.add("Yorkshire");
		boarderingRegionNames.add("Wales");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Liverpool", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Norwegian_Sea");
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Clyde");
		boarderingRegionNames.add("Liverpool");
		boarderingRegionNames.add("Yorkshire");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Edinburgh", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Edinburgh");
		boarderingRegionNames.add("Liverpool");
		boarderingRegionNames.add("Wales");
		boarderingRegionNames.add("London");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Yorkshire", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Irish_Sea");
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("Liverpool");
		boarderingRegionNames.add("Yorkshire");
		boarderingRegionNames.add("London");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Wales", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Wales");
		boarderingRegionNames.add("Yorkshire");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("London", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Adriatic_Sea");
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Trieste");
		boarderingRegionNames.add("Serbia");
		boarderingRegionNames.add("Greece");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Albania", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Black_Sea");
		boarderingRegionNames.add("Constantinople");
		boarderingRegionNames.add("Armenia");
		boarderingRegionNames.add("Smyrna");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Ankara", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Adriatic_Sea");
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Naples");
		boarderingRegionNames.add("Rome");
		boarderingRegionNames.add("Venice");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Apulia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Black_Sea");
		boarderingRegionNames.add("Sevastopol");
		boarderingRegionNames.add("Ankara");
		boarderingRegionNames.add("Smyrna");
		boarderingRegionNames.add("Syria");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Armenia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Picardy");
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Ruhr");
		boarderingRegionNames.add("Holland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Belguim", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Kiel");
		boarderingRegionNames.add("Munich");
		boarderingRegionNames.add("Silesia");
		boarderingRegionNames.add("Prussia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Berlin", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("Picardy");
		boarderingRegionNames.add("Paris");
		boarderingRegionNames.add("Gascony");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Brest", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Black_Sea");
		boarderingRegionNames.add("Rumania");
		boarderingRegionNames.add("Constantinople");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Bulgaria_(ec)", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Aegean_Sea");
		boarderingRegionNames.add("Greece");
		boarderingRegionNames.add("Serbia");
		boarderingRegionNames.add("Constantinople");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Bulgaria_(sc)", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Aegean_Sea");
		boarderingRegionNames.add("Black_Sea");
		boarderingRegionNames.add("Bulgaria_(sc)");
		boarderingRegionNames.add("Bulgaria_(ec)");
		boarderingRegionNames.add("Ankara");
		boarderingRegionNames.add("Smyrna");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Constantinople", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Heloland_Bight");
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Skagerrak");
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Sweden");
		boarderingRegionNames.add("Kiel");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Denmark", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Gulf_of_Bothnia");
		boarderingRegionNames.add("Norway");
		boarderingRegionNames.add("Sweden");
		boarderingRegionNames.add("St_Petersburg_(sc)");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Finland", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("Brest");
		boarderingRegionNames.add("Paris");
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Marseilles");
		boarderingRegionNames.add("Spain_(nc)");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Gascony", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Aegean_Sea");
		boarderingRegionNames.add("Albania");
		boarderingRegionNames.add("Serbia");
		boarderingRegionNames.add("Bulgaria_(sc)");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Greece", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Heloland_Bight");
		boarderingRegionNames.add("Kiel");
		boarderingRegionNames.add("Ruhr");
		boarderingRegionNames.add("Belguim");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Holland", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Heloland_Bight");
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Holland");
		boarderingRegionNames.add("Denmark");
		boarderingRegionNames.add("Berlin");
		boarderingRegionNames.add("Munich");
		boarderingRegionNames.add("Ruhr");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Kiel", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Gulf_of_Bothnia");
		boarderingRegionNames.add("St_Petersburg_(sc)");
		boarderingRegionNames.add("Moscow");
		boarderingRegionNames.add("Warsaw");
		boarderingRegionNames.add("Prussia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Livonia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Gulf_of_Lyon");
		boarderingRegionNames.add("Spain_(sc)");
		boarderingRegionNames.add("Gascony");
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Piedmont");
		boarderingRegionNames.add("Switzerland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Marseilles", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Tyrrhenian_Sea");
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("Rome");
		boarderingRegionNames.add("Apulia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Naples", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("Western_Mediterranean");
		boarderingRegionNames.add("Tunis");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("North_Africa", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Skagerrak");
		boarderingRegionNames.add("North_Sea");
		boarderingRegionNames.add("Norwegian_Sea");
		boarderingRegionNames.add("Barents_Sea");
		boarderingRegionNames.add("St_Petersburg_(nc)");
		boarderingRegionNames.add("Finland");
		boarderingRegionNames.add("Sweden");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Norway", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("English_Channel");
		boarderingRegionNames.add("Brest");
		boarderingRegionNames.add("Belguim");
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Paris");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Picardy", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Gulf_of_Lyon");
		boarderingRegionNames.add("Marseilles");
		boarderingRegionNames.add("Tuscany");
		boarderingRegionNames.add("Venice");
		boarderingRegionNames.add("Tyrolia");
		boarderingRegionNames.add("Switzerland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Piedmont", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("Spain_(nc)");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Portugal", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Berlin");
		boarderingRegionNames.add("Livonia");
		boarderingRegionNames.add("Warsaw");
		boarderingRegionNames.add("Silesia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Prussia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Tyrrhenian_Sea");
		boarderingRegionNames.add("Tuscany");
		boarderingRegionNames.add("Venice");
		boarderingRegionNames.add("Apulia");
		boarderingRegionNames.add("Naples");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Rome", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Black_Sea");
		boarderingRegionNames.add("Ukraine");
		boarderingRegionNames.add("Sevastopol");
		boarderingRegionNames.add("Bulgaria_(ec)");
		boarderingRegionNames.add("Serbia");
		boarderingRegionNames.add("Budapest");
		boarderingRegionNames.add("Galicia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Rumania", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Black_Sea");
		boarderingRegionNames.add("Ukraine");
		boarderingRegionNames.add("Moscow");
		boarderingRegionNames.add("Armenia");
		boarderingRegionNames.add("Rumania");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Sevastopol", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Eastern_Mediterranean");
		boarderingRegionNames.add("Aegean_Sea");
		boarderingRegionNames.add("Constantinople");
		boarderingRegionNames.add("Ankara");
		boarderingRegionNames.add("Armenia");
		boarderingRegionNames.add("Syria");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Smyrna", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Mid_Atlantic_Ocean");
		boarderingRegionNames.add("Portugal");
		boarderingRegionNames.add("Gascony");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Spain_(nc)", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Western_Mediterranean");
		boarderingRegionNames.add("Gulf_of_Lyon");
		boarderingRegionNames.add("Marseilles");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Spain_(sc)", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Barents_Sea");
		boarderingRegionNames.add("Norway");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("St_Petersburg_(nc)", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Gulf_of_Bothnia");
		boarderingRegionNames.add("Livonia");
		boarderingRegionNames.add("Moscow");
		boarderingRegionNames.add("Finland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("St_Petersburg_(sc)", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Skagerrak");
		boarderingRegionNames.add("Baltic_Sea");
		boarderingRegionNames.add("Gulf_of_Bothnia");
		boarderingRegionNames.add("Denmark");
		boarderingRegionNames.add("Norway");
		boarderingRegionNames.add("Finland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Sweden", boarderingRegionNames, myRegions);
	
		boarderingRegionNames.clear();
		boarderingRegionNames.add("Eastern_Mediterranean");
		boarderingRegionNames.add("Smyrna");
		boarderingRegionNames.add("Armenia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Syria", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Adriatic_Sea");
		boarderingRegionNames.add("Venice");
		boarderingRegionNames.add("Tyrolia");
		boarderingRegionNames.add("Vienna");
		boarderingRegionNames.add("Budapest");
		boarderingRegionNames.add("Serbia");
		boarderingRegionNames.add("Albania");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Trieste", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Western_Mediterranean");
		boarderingRegionNames.add("Tyrrhenian_Sea");
		boarderingRegionNames.add("Ionian_Sea");
		boarderingRegionNames.add("North_Africa");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Tunis", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Gulf_of_Lyon");
		boarderingRegionNames.add("Tyrrhenian_Sea");
		boarderingRegionNames.add("Piedmont");
		boarderingRegionNames.add("Venice");
		boarderingRegionNames.add("Rome");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Tuscany", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Adriatic_Sea");
		boarderingRegionNames.add("Apulia");
		boarderingRegionNames.add("Rome");
		boarderingRegionNames.add("Tuscany");
		boarderingRegionNames.add("Piedmont");
		boarderingRegionNames.add("Tyrolia");
		boarderingRegionNames.add("Trieste");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Venice", boarderingRegionNames, myRegions);

		// INLAND
		
		boarderingRegionNames.clear();
		boarderingRegionNames.add("Munich");
		boarderingRegionNames.add("Silesia");
		boarderingRegionNames.add("Galicia");
		boarderingRegionNames.add("Vienna");
		boarderingRegionNames.add("Tyrolia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Bohemia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Trieste");
		boarderingRegionNames.add("Vienna");
		boarderingRegionNames.add("Galicia");
		boarderingRegionNames.add("Rumania");
		boarderingRegionNames.add("Serbia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Budapest", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Gascony");
		boarderingRegionNames.add("Paris");
		boarderingRegionNames.add("Picardy");
		boarderingRegionNames.add("Belguim");
		boarderingRegionNames.add("Ruhr");
		boarderingRegionNames.add("Munich");
		boarderingRegionNames.add("Marseilles");
		boarderingRegionNames.add("Switzerland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Burgundy", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Vienna");
		boarderingRegionNames.add("Bohemia");
		boarderingRegionNames.add("Silesia");
		boarderingRegionNames.add("Warsaw");
		boarderingRegionNames.add("Ukraine");
		boarderingRegionNames.add("Rumania");
		boarderingRegionNames.add("Budapest");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Galicia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Livonia");
		boarderingRegionNames.add("St_Petersburg_(sc)");
		boarderingRegionNames.add("Sevastopol");
		boarderingRegionNames.add("Ukraine");
		boarderingRegionNames.add("Warsaw");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Moscow", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Ruhr");
		boarderingRegionNames.add("Kiel");
		boarderingRegionNames.add("Berlin");
		boarderingRegionNames.add("Silesia");
		boarderingRegionNames.add("Bohemia");
		boarderingRegionNames.add("Tyrolia");
		boarderingRegionNames.add("Switzerland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Munich", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Brest");
		boarderingRegionNames.add("Picardy");
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Gascony");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Paris", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Belguim");
		boarderingRegionNames.add("Holland");
		boarderingRegionNames.add("Kiel");
		boarderingRegionNames.add("Munich");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Ruhr", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Albania");
		boarderingRegionNames.add("Trieste");
		boarderingRegionNames.add("Budapest");
		boarderingRegionNames.add("Rumania");
		boarderingRegionNames.add("Bulgaria_(sc)");
		boarderingRegionNames.add("Greece");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Serbia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Bohemia");
		boarderingRegionNames.add("Munich");
		boarderingRegionNames.add("Berlin");
		boarderingRegionNames.add("Prussia");
		boarderingRegionNames.add("Warsaw");
		boarderingRegionNames.add("Galicia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Silesia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Munich");
		boarderingRegionNames.add("Bohemia");
		boarderingRegionNames.add("Vienna");
		boarderingRegionNames.add("Trieste");
		boarderingRegionNames.add("Venice");
		boarderingRegionNames.add("Piedmont");
		boarderingRegionNames.add("Switzerland");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Tyrolia", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Galicia");
		boarderingRegionNames.add("Warsaw");
		boarderingRegionNames.add("Moscow");
		boarderingRegionNames.add("Sevastopol");
		boarderingRegionNames.add("Rumania");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Ukraine", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Tyrolia");
		boarderingRegionNames.add("Bohemia");
		boarderingRegionNames.add("Galicia");
		boarderingRegionNames.add("Budapest");
		boarderingRegionNames.add("Trieste");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Vienna", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Silesia");
		boarderingRegionNames.add("Prussia");
		boarderingRegionNames.add("Livonia");
		boarderingRegionNames.add("Moscow");
		boarderingRegionNames.add("Ukraine");
		boarderingRegionNames.add("Galicia");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Warsaw", boarderingRegionNames, myRegions);

		boarderingRegionNames.clear();
		boarderingRegionNames.add("Marseilles");
		boarderingRegionNames.add("Burgundy");
		boarderingRegionNames.add("Munich");
		boarderingRegionNames.add("Tyrolia");
		boarderingRegionNames.add("Piedmont");
		allBoarderingRegionsSetProperly = allBoarderingRegionsSetProperly
				&& setBoarderingRegions("Switzerland", boarderingRegionNames, myRegions);

		return allBoarderingRegionsSetProperly;


	}

	protected boolean setBoarderingRegions(String nameOfRegionToSetBoardersFor, Set<String> namesOfBoarderingRegions,
			Map<String, Region> mapOfAllRegions) {

		boolean allBoardersSetProperly = true;

		for (String nameOfBoarderingRegion : namesOfBoarderingRegions) {
			if (mapOfAllRegions.containsKey(nameOfBoarderingRegion)) {
				mapOfAllRegions.get(nameOfRegionToSetBoardersFor).getBoarderingRegions().put(nameOfBoarderingRegion,
						mapOfAllRegions.get(nameOfBoarderingRegion));
			} else {
				allBoardersSetProperly = false;
				System.out.println("boarders set badly for region: " + nameOfRegionToSetBoardersFor);
			}
		}

		return allBoardersSetProperly;
	}

	protected Map<String, Region> defineRegions() {

		Map<String, Region> myRegions = new HashMap<String, Region>();

		addRegion("North_Atlantic_Ocean", RegionType.WATER, myRegions);
		addRegion("Irish_Sea", RegionType.WATER, myRegions);
		addRegion("English_Channel", RegionType.WATER, myRegions);
		addRegion("Norwegian_Sea", RegionType.WATER, myRegions);
		addRegion("Barents_Sea", RegionType.WATER, myRegions);
		addRegion("North_Sea", RegionType.WATER, myRegions);
		addRegion("Heloland_Bight", RegionType.WATER, myRegions);
		addRegion("Skagerrak", RegionType.WATER, myRegions);
		addRegion("Baltic_Sea", RegionType.WATER, myRegions);
		addRegion("Gulf_of_Bothnia", RegionType.WATER, myRegions);
		addRegion("Mid_Atlantic_Ocean", RegionType.WATER, myRegions);
		addRegion("Western_Mediterranean", RegionType.WATER, myRegions);
		addRegion("Gulf_of_Lyon", RegionType.WATER, myRegions);
		addRegion("Tyrrhenian_Sea", RegionType.WATER, myRegions);
		addRegion("Ionian_Sea", RegionType.WATER, myRegions);
		addRegion("Adriatic_Sea", RegionType.WATER, myRegions);
		addRegion("Aegean_Sea", RegionType.WATER, myRegions);
		addRegion("Eastern_Mediterranean", RegionType.WATER, myRegions);
		addRegion("Black_Sea", RegionType.WATER, myRegions);

		addRegion("Clyde", RegionType.COASTAL, myRegions);
		addRegion("Liverpool", RegionType.COASTAL, myRegions);
		addRegion("Edinburgh", RegionType.COASTAL, myRegions);
		addRegion("Yorkshire", RegionType.COASTAL, myRegions);
		addRegion("Wales", RegionType.COASTAL, myRegions);
		addRegion("London", RegionType.COASTAL, myRegions);
		addRegion("Albania", RegionType.COASTAL, myRegions);
		addRegion("Ankara", RegionType.COASTAL, myRegions);
		addRegion("Apulia", RegionType.COASTAL, myRegions);
		addRegion("Armenia", RegionType.COASTAL, myRegions);
		addRegion("Belguim", RegionType.COASTAL, myRegions);
		addRegion("Berlin", RegionType.COASTAL, myRegions);
		addRegion("Brest", RegionType.COASTAL, myRegions);
		addRegion("Bulgaria_(ec)", RegionType.COASTAL, myRegions);
		addRegion("Bulgaria_(sc)", RegionType.COASTAL, myRegions);
		addRegion("Constantinople", RegionType.COASTAL, myRegions);
		addRegion("Denmark", RegionType.COASTAL, myRegions);
		addRegion("Finland", RegionType.COASTAL, myRegions);
		addRegion("Gascony", RegionType.COASTAL, myRegions);
		addRegion("Greece", RegionType.COASTAL, myRegions);
		addRegion("Holland", RegionType.COASTAL, myRegions);
		addRegion("Kiel", RegionType.COASTAL, myRegions);
		addRegion("Livonia", RegionType.COASTAL, myRegions);
		addRegion("Marseilles", RegionType.COASTAL, myRegions);
		addRegion("Naples", RegionType.COASTAL, myRegions);
		addRegion("North_Africa", RegionType.COASTAL, myRegions);
		addRegion("Norway", RegionType.COASTAL, myRegions);
		addRegion("Picardy", RegionType.COASTAL, myRegions);
		addRegion("Piedmont", RegionType.COASTAL, myRegions);
		addRegion("Portugal", RegionType.COASTAL, myRegions);
		addRegion("Prussia", RegionType.COASTAL, myRegions);
		addRegion("Rome", RegionType.COASTAL, myRegions);
		addRegion("Rumania", RegionType.COASTAL, myRegions);
		addRegion("Sevastopol", RegionType.COASTAL, myRegions);
		addRegion("Smyrna", RegionType.COASTAL, myRegions);
		addRegion("Spain_(nc)", RegionType.COASTAL, myRegions);
		addRegion("Spain_(sc)", RegionType.COASTAL, myRegions);
		addRegion("St_Petersburg_(nc)", RegionType.COASTAL, myRegions);
		addRegion("St_Petersburg_(sc)", RegionType.COASTAL, myRegions);
		addRegion("Sweden", RegionType.COASTAL, myRegions);
		addRegion("Syria", RegionType.COASTAL, myRegions);
		addRegion("Trieste", RegionType.COASTAL, myRegions);
		addRegion("Tunis", RegionType.COASTAL, myRegions);
		addRegion("Tuscany", RegionType.COASTAL, myRegions);
		addRegion("Venice", RegionType.COASTAL, myRegions);

		addRegion("Switzerland", RegionType.INLAND, myRegions);
		addRegion("Bohemia", RegionType.INLAND, myRegions);
		addRegion("Budapest", RegionType.INLAND, myRegions);
		addRegion("Burgundy", RegionType.INLAND, myRegions);
		addRegion("Galicia", RegionType.INLAND, myRegions);
		addRegion("Moscow", RegionType.INLAND, myRegions);
		addRegion("Munich", RegionType.INLAND, myRegions);
		addRegion("Paris", RegionType.INLAND, myRegions);
		addRegion("Ruhr", RegionType.INLAND, myRegions);
		addRegion("Serbia", RegionType.INLAND, myRegions);
		addRegion("Silesia", RegionType.INLAND, myRegions);
		addRegion("Tyrolia", RegionType.INLAND, myRegions);
		addRegion("Ukraine", RegionType.INLAND, myRegions);
		addRegion("Vienna", RegionType.INLAND, myRegions);
		addRegion("Warsaw", RegionType.INLAND, myRegions);

		return myRegions;
	}

	protected void addRegion(String regionName, RegionType regionType, Map<String, Region> regionMap) {

		if (regionName != null && regionType != null && regionMap != null) {
			regionMap.put(regionName, new Region(regionName, regionType));
		}
	}

	protected Map<String, Province> defineProvinces() {

		Map<String, Province> myProvinces = new HashMap<String, Province>();

		addProvince("North_Atlantic_Ocean", regions.get("North_Atlantic_Ocean"), null, myProvinces);
		addProvince("Irish_Sea", regions.get("Irish_Sea"), null, myProvinces);
		addProvince("English_Channel", regions.get("English_Channel"), null, myProvinces);
		addProvince("Norwegian_Sea", regions.get("Norwegian_Sea"), null, myProvinces);
		addProvince("Barents_Sea", regions.get("Barents_Sea"), null, myProvinces);
		addProvince("North_Sea", regions.get("North_Sea"), null, myProvinces);
		addProvince("Heloland_Bight", regions.get("Heloland_Bight"), null, myProvinces);
		addProvince("Skagerrak", regions.get("Skagerrak"), null, myProvinces);
		addProvince("Baltic_Sea", regions.get("Baltic_Sea"), null, myProvinces);
		addProvince("Gulf_of_Bothnia", regions.get("Gulf_of_Bothnia"), null, myProvinces);
		addProvince("Mid_Atlantic_Ocean", regions.get("Mid_Atlantic_Ocean"), null, myProvinces);
		addProvince("Western_Mediterranean", regions.get("Western_Mediterranean"), null, myProvinces);
		addProvince("Gulf_of_Lyon", regions.get("Gulf_of_Lyon"), null, myProvinces);
		addProvince("Tyrrhenian_Sea", regions.get("Tyrrhenian_Sea"), null, myProvinces);
		addProvince("Ionian_Sea", regions.get("Ionian_Sea"), null, myProvinces);
		addProvince("Adriatic_Sea", regions.get("Adriatic_Sea"), null, myProvinces);
		addProvince("Aegean_Sea", regions.get("Aegean_Sea"), null, myProvinces);
		addProvince("Eastern_Mediterranean", regions.get("Eastern_Mediterranean"), null, myProvinces);
		addProvince("Black_Sea", regions.get("Black_Sea"), null, myProvinces);

		addProvince("Clyde", regions.get("Clyde"), null, myProvinces);
		addProvince("Liverpool", regions.get("Liverpool"), null, myProvinces);
		addProvince("Edinburgh", regions.get("Edinburgh"), null, myProvinces);
		addProvince("Yorkshire", regions.get("Yorkshire"), null, myProvinces);
		addProvince("Wales", regions.get("Wales"), null, myProvinces);
		addProvince("London", regions.get("London"), null, myProvinces);
		addProvince("Albania", regions.get("Albania"), null, myProvinces);
		addProvince("Ankara", regions.get("Ankara"), null, myProvinces);
		addProvince("Apulia", regions.get("Apulia"), null, myProvinces);
		addProvince("Armenia", regions.get("Armenia"), null, myProvinces);
		addProvince("Belguim", regions.get("Belguim"), null, myProvinces);
		addProvince("Berlin", regions.get("Berlin"), null, myProvinces);
		addProvince("Brest", regions.get("Brest"), null, myProvinces);
		addProvince("Bulgaria", regions.get("Bulgaria_(ec)"), regions.get("Bulgaria_(sc)"), myProvinces);
		addProvince("Constantinople", regions.get("Constantinople"), null, myProvinces);
		addProvince("Denmark", regions.get("Denmark"), null, myProvinces);
		addProvince("Finland", regions.get("Finland"), null, myProvinces);
		addProvince("Gascony", regions.get("Gascony"), null, myProvinces);
		addProvince("Greece", regions.get("Greece"), null, myProvinces);
		addProvince("Holland", regions.get("Holland"), null, myProvinces);
		addProvince("Kiel", regions.get("Kiel"), null, myProvinces);
		addProvince("Livonia", regions.get("Livonia"), null, myProvinces);
		addProvince("Marseilles", regions.get("Marseilles"), null, myProvinces);
		addProvince("Naples", regions.get("Naples"), null, myProvinces);
		addProvince("North_Africa", regions.get("North_Africa"), null, myProvinces);
		addProvince("Norway", regions.get("Norway"), null, myProvinces);
		addProvince("Picardy", regions.get("Picardy"), null, myProvinces);
		addProvince("Piedmont", regions.get("Piedmont"), null, myProvinces);
		addProvince("Portugal", regions.get("Portugal"), null, myProvinces);
		addProvince("Prussia", regions.get("Prussia"), null, myProvinces);
		addProvince("Rome", regions.get("Rome"), null, myProvinces);
		addProvince("Rumania", regions.get("Rumania"), null, myProvinces);
		addProvince("Sevastopol", regions.get("Sevastopol"), null, myProvinces);
		addProvince("Smyrna", regions.get("Smyrna"), null, myProvinces);
		addProvince("Spain", regions.get("Spain_(nc)"), regions.get("Spain_(sc)"), myProvinces);
		addProvince("St_Petersburg", regions.get("St_Petersburg_(nc)"), regions.get("St_Petersburg_(sc)"), myProvinces);
		addProvince("Sweden", regions.get("Sweden"), null, myProvinces);
		addProvince("Syria", regions.get("Syria"), null, myProvinces);
		addProvince("Trieste", regions.get("Trieste"), null, myProvinces);
		addProvince("Tunis", regions.get("Tunis"), null, myProvinces);
		addProvince("Tuscany", regions.get("Tuscany"), null, myProvinces);
		addProvince("Venice", regions.get("Venice"), null, myProvinces);
		addProvince("Vienna", regions.get("Vienna"), null, myProvinces);

		addProvince("Switzerland", regions.get("Switzerland"), null, myProvinces);
		addProvince("Bohemia", regions.get("Bohemia"), null, myProvinces);
		addProvince("Budapest", regions.get("Budapest"), null, myProvinces);
		addProvince("Burgundy", regions.get("Burgundy"), null, myProvinces);
		addProvince("Galicia", regions.get("Galicia"), null, myProvinces);
		addProvince("Moscow", regions.get("Moscow"), null, myProvinces);
		addProvince("Munich", regions.get("Munich"), null, myProvinces);
		addProvince("Paris", regions.get("Paris"), null, myProvinces);
		addProvince("Ruhr", regions.get("Ruhr"), null, myProvinces);
		addProvince("Serbia", regions.get("Serbia"), null, myProvinces);
		addProvince("Silesia", regions.get("Silesia"), null, myProvinces);
		addProvince("Tyrolia", regions.get("Tyrolia"), null, myProvinces);
		addProvince("Ukraine", regions.get("Ukraine"), null, myProvinces);
		addProvince("Warsaw", regions.get("Warsaw"), null, myProvinces);

		return myProvinces;
	}

	protected void addProvince(String provinceName, Region region1, Region region2, Map<String, Province> provinceMap) {

		if (provinceName != null && region1 != null && provinceMap != null) {
			provinceMap.put(provinceName, new Province(provinceName, region1, region2));
		}
	}

}
