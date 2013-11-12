package com.msg;


public class MalformedMessageException extends Exception {

	private static final long serialVersionUID = -5385497575062088265L;
	
	protected static final String DEFAULT_ERR_MSG = "Malformed Message Exception";
	
	protected final MsgType type;

	public MalformedMessageException(MsgType type) {
		super();
		this.type=type;
	}
	
	public MalformedMessageException() {
		super("Malformed Message error"); //TODO pas satisfaisant :(
		this.type = null;
	}

	
	public String getMessage(){
		return DEFAULT_ERR_MSG+". Message is not of type: "+type;
	}
	
	public String getLocalizedMessage(){
		return getMessage();
	}


}
