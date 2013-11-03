package com.msg;

import util.PlayerName;

public class MsgStart extends Message {

	private static final long serialVersionUID = 6519199863234004704L;

	public MsgStart() {
		super(MsgType.start);
	}
	
	public MsgStart(PlayerName senderID) {
		super(MsgType.start,senderID,null);
	}
	
}
