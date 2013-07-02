package com.msg;

public class MsgStartNack extends Message {

	public MsgStartNack() {
		super(MsgType.startNack);
	}

	public MsgStartNack(String senderID) {
		super(MsgType.startNack, senderID,null);
	}

}
