package com.msg;

import util.PlayerName;

public class MsgDeco extends Message {

	private static final long serialVersionUID = -4884185410061448744L;

	private String decoClient;
	
	private int emptyID;
	
	public MsgDeco(PlayerName senderID, PlayerName decoClient, int emptyID) {
		super(MsgType.disconnect, senderID, null);
		if(decoClient != null)
			this.decoClient=decoClient.getName();
		
		this.emptyID=emptyID;
	}
	
	public PlayerName getDecoClient(){
		if(decoClient != null)
			return new PlayerName(decoClient);
		
		return null;
	}
	
	public int getEmptyID(){
		return emptyID;
	}

}
