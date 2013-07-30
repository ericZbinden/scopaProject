package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import com.msg.MsgPlay;

public abstract class GamePanel extends JPanel implements ActionListener, Cloneable {
	
	private Set<ActionListener> listeners = new HashSet<ActionListener>();

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
	abstract public GamePanel clone();

}
