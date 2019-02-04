package com.sdk.diplomacy.turnadmin.domain;

public class Game {
	
	private String id;
	private String name;
	
	public Game(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
}
