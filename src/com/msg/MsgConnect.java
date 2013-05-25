package com.msg;

public class MsgConnect extends Message {

	private static final long serialVersionUID = -2232962384841195638L;
	private String pwd;
	
	/**
	 * Default constructor
	 * @param senderID the sender id
	 * @param pwd the password
	 */
	public MsgConnect(String senderID, String pwd) {
		super(MsgType.connect, senderID, null);
		this.pwd=pwd;
	}
	
	public String getPwd(){
		return pwd;
	}

}
