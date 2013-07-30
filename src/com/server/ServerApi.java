package com.server;

import com.msg.MsgPlay;

public interface ServerApi {
	
	/**
	 * Send a msg to one player
	 * @param client
	 * @param msg
	 */
	public void sendMsgTo(String client, MsgPlay msg);
	
	/**
	 * Send a same msg to all players
	 * @param client
	 * @param msg
	 */
	public void sendMsgToAll(MsgPlay msg); 

	/**
	 * Send a msg to all client Except to the specified
	 * (used to transfer a msg played by one player to the others)
	 * @param client
	 * @param msg
	 */
	public void sendMsgToAllExcept(String client, MsgPlay msg);
	
	/**
	 * Send a txt msg to a player
	 * @param client
	 * @param txt
	 */
	public void writeIntoClientChat(String client, String txt);
	
	/**
	 * Send a txt msg to all players
	 * @param txt
	 */
	public void writeIntoAllChat(String txt);
	
}
