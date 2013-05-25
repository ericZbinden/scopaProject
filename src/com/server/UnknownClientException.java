package com.server;

public class UnknownClientException extends Exception {

	private static final long serialVersionUID = -8535945183100872531L;

	public UnknownClientException(String clientID){
		super("Client "+clientID+" is unknown.");
	}
	
	public UnknownClientException(){
		super("Unknown client exception.");
	}
}
