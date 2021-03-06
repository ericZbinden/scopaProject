package com.server.api;

import util.PlayerName;

import com.msg.Message;
import com.msg.MsgConnect;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.server.exceptions.IllegalInitialConditionException;
import com.server.impl.ServerCSocket;
import com.server.wait.Config;

public interface ServerConnect {

	/**
	 * Send a msg to one client
	 * @param clientID
	 * @param msg
	 */
	public void transfertMsgTo(PlayerName clientID, Message msg);

	/**
	 * Transfer a msg to all client except to the sender
	 * @param msg
	 * @param senderID
	 */
	public void transferMsgToAll(Message msg, PlayerName senderID);

	/**
	 * Update a WaitingRoom slot
	 * @param impactedID
	 * @param state
	 * @param scs the sender of this class
	 */
	public void updateWR(PlayerName impactedID, Config state, ServerCSocket scs);

	/**
	 * Transfer the msgPlay to the playable game
	 * @param play
	 */
	public void play(MsgPlay play);

	/**
	 * Store the game default rule
	 * @param rule
	 */
	public void saveRule(MsgMasterGame rule);

	/**
	 * Store the current rule of the game
	 * @param rule
	 */
	public void saveRule(MsgMasterRule rule);

	/**
	 * @return is all players connected are ready
	 */
	public boolean areAllPlayersReady();

	/** @return the server state */
	public ServerState getServerState();

	/**
	 * Start a new game with last game msg received as rules
	 * @throws IllegalInitialConditionException if initial condition are not
	 *             satisfied or if all players are not ready
	 */
	public void startGame() throws IllegalInitialConditionException;

	/**
	 * Called when a new client wants to connect to this server
	 * @param msg the connect message with id and password
	 * @param scs the sender of this class
	 * @return a message to send back to the client. Either a MsgConfig or a
	 *         MsgReset
	 */
	public Message connect(MsgConnect msg, ServerCSocket scs);

	/**
	 * Called when a client disconnect
	 * @param scs
	 */
	public void disconnect(ServerCSocket scs);

}
