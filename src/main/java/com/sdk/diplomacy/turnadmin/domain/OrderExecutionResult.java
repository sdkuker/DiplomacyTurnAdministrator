package com.sdk.diplomacy.turnadmin.domain;

/*
 * I contain the results of an order being executed
 */
public class OrderExecutionResult {

	private String orderId;
	private String turnId;
	private String gameId;
	private boolean orderExecutedSuccessfully = true;
	private String executionDescription;
	private boolean isValidOrder = true;
	
	public OrderExecutionResult(String orderId, String turnId, String gameId) {
		super();
		this.orderId = orderId;
		this.turnId = turnId;
		this.gameId = gameId;
	}
	
	public boolean wasOrderExecutedSuccessfully() {
		return orderExecutedSuccessfully;
	}
	
	public void setOrderExecutedSuccessfully(boolean orderExecutedSuccessfully) {
		this.orderExecutedSuccessfully = orderExecutedSuccessfully;
	}
	
	public String getExecutionDescription() {
		return executionDescription;
	}
	
	public void setExecutionDescription(String executionDescription) {
		this.executionDescription = executionDescription;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getTurnId() {
		return turnId;
	}

	public String getGameId() {
		return gameId;
	}

	public boolean isValidOrder() {
		return isValidOrder;
	}

	public void setIsValidOrder(boolean aBoolean) {
		
		this.isValidOrder = aBoolean;
		
		if (! isValidOrder ) {
			setOrderExecutedSuccessfully(false);
		}
	}
	
	
}
