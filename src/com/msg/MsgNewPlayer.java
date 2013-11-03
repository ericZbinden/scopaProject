package com.msg;

import util.PlayerName;

public class MsgNewPlayer extends Message {

	private static final long serialVersionUID = 4028890435094273600L;

	private String newPlayerID;
	
	private String openID;
	
	public MsgNewPlayer(PlayerName newPlayerID, PlayerName openID) {
		super(MsgType.newPlayer);
		
		if(newPlayerID != null)
			this.newPlayerID = newPlayerID.getName();
		
		if(openID != null)
			this.openID = openID.getName();
	}
	
	public PlayerName getNewPlayerID(){
		if(newPlayerID != null)
			return new PlayerName(newPlayerID);
		
		return null;
	}
	
	public PlayerName getOpenID(){
		if(openID != null)
			return new PlayerName(openID);
		
		return null;
	}

}
