package com.msg;

import com.server.Server;

public class MsgStartNack extends Message {

	private static final long serialVersionUID = -396769360945825320L;
	
	private String reason;

	public MsgStartNack(String reason) {
		super(MsgType.startNack, Server.SERVER_NAME,null);
		this.reason=reason;
	}
	
	public String getReason(){
		return reason;
	}

}
