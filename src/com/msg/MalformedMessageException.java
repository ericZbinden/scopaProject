package com.msg;


public class MalformedMessageException extends Exception {


	public MalformedMessageException(String message) {
		super(message);
	}
	
	public MalformedMessageException() {
		super("Malformed Message error");
	}
	
	public MalformedMessageException(Exception e){
		super(e);
	}
	
	public MalformedMessageException(String message, Exception e){
		super(message,e);
	}

}
