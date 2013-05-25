package gui.wait;

import com.msg.Message;

public interface ControlPanel {

	/** Update the control panel with the received message  
	 * @param msg
	 */
	public void update(Message msg);
	
	public String getClientID();
	
	/** The down connection from the server is down */
	public void serverDown();
	
}
