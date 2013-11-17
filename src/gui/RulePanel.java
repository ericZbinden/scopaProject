package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgMasterRule;

public abstract class RulePanel extends JPanel implements ActionListener, Cloneable {

	protected boolean canEdit;
	private Set<ActionListener> listeners = new HashSet<ActionListener>();
	
	protected RulePanel() {
		listeners = new HashSet<ActionListener>();
	}
	
	/**
	 * Tell if the user can edit children component inside this panel
	 * @return
	 */
	public boolean canEdit(){
		return canEdit;
	}
	
	public void setEdit(boolean canEdit){
		this.canEdit=canEdit;
	}
	
	/**
	 * Update the panel with data received from the server
	 * @param ruleMsg
	 * @throws MalformedMessageException the message is not of the expected type
	 */
	abstract public void editRules(MsgMasterRule ruleMsg)  throws MalformedMessageException;
	
	/**
	 * Generate a message to send to the server to propagate the update
	 * @return
	 */
	abstract public MsgMasterRule getMsgRule(PlayerName senderID);
	
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Collect all action from children. Update the source as this panel
	 * and return to all suscribed listeners
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		arg0.setSource(this);
		for(ActionListener listener : listeners){
			listener.actionPerformed(arg0);
		}
	}
	
	/**
	 * Generate a deep copy of this panel
	 */
	@Override
	abstract public RulePanel clone();


}
