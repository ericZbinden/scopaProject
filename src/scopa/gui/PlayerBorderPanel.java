package scopa.gui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scopa.logic.card.ScopaCard;
import scopa.logic.hand.ScopaHand;
import util.PlayerName;

public class PlayerBorderPanel extends JPanel {

	private ScopaHandPanel handPanel;

	public PlayerBorderPanel(ScopaHand hand, ScopaGamePanel parent) {

		handPanel = new ScopaHandPanel(hand, parent, ScopaHandPanel.Orientation.horizontal);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(new JLabel(hand.getPlayerName().getName()));
		this.add(handPanel);

		this.setPreferredSize(new Dimension(400, 200));
		this.setBackground(ScopaConstant.backgroundColor);
	}

	public boolean playCard(ScopaCard playedCard) {
		return handPanel.playCard(playedCard);
	}

	public void newHand(List<ScopaCard> newCards) {
		handPanel.newHand(newCards);
		this.repaint();
	}

	public PlayerName getPlayerName() {
		return handPanel.getPlayerName();
	}

}
