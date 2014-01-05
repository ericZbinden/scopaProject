package scopa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaHand;
import util.PlayerName;

public class PlayerBorderPanel extends JPanel {

	private ScopaHandPanel handPanel;

	public PlayerBorderPanel(ScopaHand hand, ScopaGamePanel parent) {

		handPanel = new ScopaHandPanel(hand, parent, ScopaHandPanel.Orientation.horizontal);

		Box panel = new Box(BoxLayout.Y_AXIS);
		panel.setOpaque(false);
		panel.add(new JLabel(hand.getPlayerName().getName()));
		panel.add(handPanel);
		this.add(panel, BorderLayout.CENTER);

		this.setPreferredSize(new Dimension(400, 200));
		this.setBackground(ScopaGuiConstant.backgroundColor);
	}

	public boolean playCard(ScopaCard playedCard) {
		return handPanel.playCard(playedCard);
	}

	public void newHand(List<ScopaCard> newCards) {
		handPanel.newHand(newCards);
	}

	public PlayerName getPlayerName() {
		return handPanel.getPlayerName();
	}

}
