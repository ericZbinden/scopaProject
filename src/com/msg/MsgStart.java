package com.msg;

public class MsgStart extends Message {

	public MsgStart() {
		super(MsgType.start);
	}
	
	public MsgStart(String senderID) {
		super(MsgType.start,senderID,null);
	}
	
}
