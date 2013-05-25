package com.msg;

public class MsgNewPlayer extends Message {

	private String newPlayerID;
	
	private String openID;
	
	public MsgNewPlayer(String newPlayerID, String openID) {
		super(MsgType.newPlayer);
		this.newPlayerID = newPlayerID;
		this.openID = openID;
	}
	
	public String getNewPlayerID(){
		return newPlayerID;
	}
	
	public String getOpenID(){
		return openID;
	}

}
