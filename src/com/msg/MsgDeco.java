package com.msg;

public class MsgDeco extends Message {

	private static final long serialVersionUID = -4884185410061448744L;

	private String decoClient;
	
	private int emptyID;
	
	public MsgDeco(String senderID, String decoClient, int emptyID) {
		super(MsgType.disconnect, senderID, null);
		this.decoClient=decoClient;
		this.emptyID=emptyID;
	}
	
	public String getDecoClient(){
		return decoClient;
	}
	
	public int getEmptyID(){
		return emptyID;
	}

}
