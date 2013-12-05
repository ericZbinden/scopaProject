package gui.api;

import util.PlayerName;

import com.msg.MsgPlay;

public interface PlayMsgSender {

	/**
	 * Send a play msg to the server
	 * @param msg the move done by the client
	 */
	public void sendMsgPlay(MsgPlay msg);

	/**
	 * @return the playerName that play in this frame
	 */
	public PlayerName getLocalClient();

	/**
	 * The client closed the window, close also the game and all connection
	 */
	public void disconnect();

}
