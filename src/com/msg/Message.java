package com.msg;

import java.io.Serializable;

import util.PlayerName;

public class Message implements Serializable {

	private static final long serialVersionUID = 5339075828532666409L;
	
	private MsgType type;
	
	private String senderID;
	
	private String receiverID;
	
	public MsgType getType(){
		return type;
	}
	
	public Message(MsgType type){
		this.type = type;
		senderID = null;
		receiverID = null;
	}
	
	public Message(MsgType type, PlayerName senderID, PlayerName receiverID){
		this(type);
		if(senderID != null)
			this.senderID = senderID.getName();
		if(receiverID != null)
			this.receiverID = receiverID.getName();
	}
	
	public PlayerName getSenderID(){
		if(senderID != null)
			return new PlayerName(senderID);
		
		return null;
	}
	
	public PlayerName getReceiverID(){
		if(receiverID != null)
			return new PlayerName(receiverID);
		
		return null;
	}
	
	@Override
	public String toString(){
		
		String msg = "Message:\n\tType: "+type
			  +"\n\tSender: "+senderID
			  +"\n\treceiver: "+receiverID;
		
		return msg;
	}
	
	
}
