package com.sdk.diplomacy.turnadmin.conflict;

import java.util.HashMap;
import java.util.Map;

import com.sdk.diplomacy.turnadmin.domain.Order;
import com.sdk.diplomacy.turnadmin.domain.OrderExecutionResult;
import com.sdk.diplomacy.turnadmin.domain.Piece;
import com.sdk.diplomacy.turnadmin.map.GameMap;

public class ConflictResolver {

	/*
	 * The key to the map is the effective ending location name. If it's a move it's
	 * the ending location. If it's a hold it's the current location. If it's a
	 * support or convoy it's the secondary piece location.
	 */
	protected Map<String, Order> orders = new HashMap<String, Order>();

	/*
	 * The key is the id of the order that the results are for.
	 */
	protected Map<String, OrderExecutionResult> orderResults = new HashMap<String, OrderExecutionResult>();

	/*
	 * The key is current location of the piece
	 */
	protected Map<String, Piece> pieces = new HashMap<String, Piece>();
	
	protected GameMap myGameMap;
	
	public void validateOrder(Order anOrder, OrderExecutionResult anOrderResult) {
		
		
	}
}
