package com.msg;


public class MalformedMessageException extends Exception {

	private static final long serialVersionUID = -5385497575062088265L;

	public MalformedMessageException(MsgType type) {
		super("Malformed Message Exception. Message is not of type: "+type );
	}

	public MalformedMessageException() {
		super("Malformed Message Exception");
	}
}
