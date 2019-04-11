package com.sdk.diplomacy.turnadmin.conflict;

/*
 * I contain the results of an order being executed
 */
public class OrderResolutionResults {

	private String orderId;
	private String turnId;
	private String gameId;
	// tells if the order was successful
	private boolean orderExecutedSuccessfully = true;
	private String executionDescription;
	// tells if the order was written properly and supporting orders exist so it can be executed
	private boolean isValidOrder = true;
	// tells if order resolution/determination has be finished
	private boolean orderResolutionCompleted = false;
	
	public OrderResolutionResults(String orderId, String turnId, String gameId) {
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
		setOrderResolutionCompleted(true);
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

	public boolean isOrderResolutionCompleted() {
		return orderResolutionCompleted;
	}

	public void setOrderResolutionCompleted(boolean orderResolutionCompleted) {
		this.orderResolutionCompleted = orderResolutionCompleted;
	}
	
	
}
