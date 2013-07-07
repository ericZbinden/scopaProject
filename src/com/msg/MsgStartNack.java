package com.msg;

public class MsgStartNack extends Message {

	private String reason;

	public MsgStartNack(String reason) {
		super(MsgType.startNack, "server",null);
		this.reason=reason;
	}
	
	public String getReason(){
		return reason;
	}

}
