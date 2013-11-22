package gui.game;

import gui.api.GameGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import util.PlayerName;

import com.msg.MsgPlay;

public abstract class GamePanel extends JPanel implements ActionListener,
		Cloneable {

	private Set<ActionListener> listeners = new HashSet<ActionListener>();

	private GameGui gameGui;

	public GamePanel() {
		listeners = new HashSet<ActionListener>();
	}

	public abstract void update(MsgPlay msg);

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Collect all action from children. Update the source as this panel and
	 * return to all subscribed listeners
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		arg0.setSource(this);
		for (ActionListener listener : listeners) {
			listener.actionPerformed(arg0);
		}
	}

	// package protected
	void setGameGui(GameGui gameGui) {
		this.gameGui = gameGui;
	}

	protected void sendMsgPlay(MsgPlay msg) {
		// TODO add frame when send fails
		gameGui.sendMsgPlay(msg);
	}

	public PlayerName getLocalClient() {
		return gameGui.getLocalClient();
	}

	/**
	 * Generate a deep copy of this panel
	 */
	@Override
	abstract public GamePanel clone();

	public void showWarningToPlayer(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

}
