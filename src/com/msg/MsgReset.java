package com.msg;

public class MsgReset extends Message {

	private static final long serialVersionUID = 1939530548069636263L;
	private String reason;
	
	public MsgReset(String reason) {
		super(MsgType.reset);
		this.reason = reason;
	}

	public String getReason(){
		return reason;
	}

}
