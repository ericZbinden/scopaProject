package com.msg;

import util.PlayerName;

public class MsgReco extends Message {

	private final String password;
	
	public MsgReco(PlayerName senderID, String pwd) {
		super(MsgType.reco,senderID,null);
		this.password=pwd;
	}
	
	public PlayerName getReconnectedPlayer(){
		return this.getSenderID();
	}
	
	public String getPassword(){
		return password;
	}
	
	@Override
	public String toString(){
		return super.toString()+"\nPwd: "+password;
	}

}
