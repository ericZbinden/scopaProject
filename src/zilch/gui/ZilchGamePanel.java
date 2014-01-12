package zilch.gui;

import gui.game.GamePanel;

import com.msg.MsgPlay;

public class ZilchGamePanel extends GamePanel {

	public ZilchGamePanel() {
	}

	@Override
	public void update(MsgPlay msg) {
		// nothing

	}

	@Override
	public GamePanel clone() {
		return new ZilchGamePanel();
	}

}
