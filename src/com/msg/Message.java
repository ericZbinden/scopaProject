package com.msg;

import java.io.Serializable;

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
	
	public Message(MsgType type, String senderID, String receiverID){
		this(type);
		this.senderID = senderID;
		this.receiverID = receiverID;
	}
	
	public String getSenderID(){
		return senderID;
	}
	
	public String getReceiverID(){
		return receiverID;
	}
	
	public String toString(){
		
		String msg = "Message:\n\tType: "+type
			  +"\n\tSender: "+senderID
			  +"\n\treceiver: "+receiverID;
		
		return msg;
	}
	
	
}
