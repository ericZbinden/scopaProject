package gui.wait;

import util.PlayerName;

import com.msg.Message;

public interface ControlPanel {

	/** Update the control panel with the received message  
	 * @param msg
	 */
	public void update(Message msg);
	
	public PlayerName getClientID();
	
	/** The down connection from the server is down */
	public void serverDown();
	
	/** Called when the ui frame should be closed and disposed */
	public void dispose();
	
}
