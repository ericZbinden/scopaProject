package gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class GamePanel extends JPanel {

	public GamePanel() {
	}

	public GamePanel(LayoutManager arg0) {
		super(arg0);
	}

	public GamePanel(boolean arg0) {
		super(arg0);
	}

	public GamePanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
	}

}
