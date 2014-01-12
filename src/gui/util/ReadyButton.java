package gui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;

import javax.swing.JButton;

public class ReadyButton extends JButton {

	private boolean ready = false;
	private boolean disable = false;

	public ReadyButton() throws HeadlessException {
	}

	public ReadyButton(String arg0) throws HeadlessException {
		super(arg0);
	}

	@Override
	public void paint(Graphics g) {
		if (disable) {
			this.setBackground(Color.GRAY);
		} else if (ready) {
			this.setBackground(Color.GREEN);
		} else {
			this.setBackground(Color.RED);
		}

		super.paint(g);
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
		this.invalidate();
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
		this.invalidate();
	}

}
