package com.server.exceptions;

import util.PlayerName;

public class UnknownClientException extends Exception {

	private static final long serialVersionUID = -8535945183100872531L;

	public UnknownClientException(PlayerName clientID){
		this(clientID.getName());
	}
	
	protected UnknownClientException(String clientID){
		super("Client "+clientID+" is unknown.");
	}
	
	public UnknownClientException(){
		super("Unknown client exception.");
	}
}
