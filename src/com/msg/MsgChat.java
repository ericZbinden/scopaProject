package com.msg;

public class MsgChat extends Message {

	private static final long serialVersionUID = -3118066025456840683L;
	private String txt;

	/**
	 * Public chat msg
	 * @param senderID
	 * @param txt
	 */
	public MsgChat(String senderID, String txt) {
		super(MsgType.chat, senderID, null);
		this.txt = txt;
	}
	/**
	 * Private chat msg
	 * @param senderID
	 * @param receiverID
	 * @param txt
	 */
	public MsgChat(String senderID, String receiverID, String txt) {
		super(MsgType.chat, senderID, receiverID);
		this.txt = txt;
	}
	
	public String getText(){
		return txt;
	}

}
