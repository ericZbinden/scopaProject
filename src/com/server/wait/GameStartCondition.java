package com.server.wait;

public class GameStartCondition {

	private boolean conditionsStatus;
	
	private String reason;
	
	public GameStartCondition(boolean conditionsStatus, String reason){
		this.conditionsStatus=conditionsStatus;
		this.reason=reason;
	}

	public boolean getConditionsStatus() {
		return conditionsStatus;
	}

	public String getReason() {
		return reason;
	}
}
