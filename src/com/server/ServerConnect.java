package com.server;

import java.io.IOException;

import com.msg.Message;
import com.msg.MsgConnect;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.server.wait.Config;

public interface ServerConnect {
	
	/**
	 * Send a msg to one client
	 * @param clientID
	 * @param msg
	 * @throws IOException
	 * @throws UnknownClientException this client does not exist
	 */
	public void transfertMsgTo(String clientID, Message msg) throws IOException, UnknownClientException;
	
	/**
	 * Transfer a msg to all client except to the sender
	 * @param msg
	 * @param senderID
	 * @throws IOException
	 */
	public void transferMsgToAll(Message msg, String senderID) throws IOException;
	
	/**
	 * Update a WaitingRoom slot
	 * @param impactedID
	 * @param state
	 * @param scs the sender of this class
	 */
	public void updateWR(String impactedID, Config state, ServerCSocket scs);
	
	/**
	 * Store the current rule of the game
	 * @param rule
	 */
	public void saveRule(MsgMasterGame rule);
	
	/**
	 * Called when a new client wants to connect to this server
	 * @param msg the connect message with id and password
	 * @param scs the sender of this class
	 * @return a message to send back to the client. Either a MsgConfig or a MsgReset
	 */
	public Message connect(MsgConnect msg, ServerCSocket scs);
	
	/**
	 * Called when a client disconnect
	 * @param scs
	 */
	public void disconnect(ServerCSocket scs);

}
