package scopa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scopa.logic.EmptyHand;
import scopa.logic.OffuscatedHand;
import scopa.logic.ScopaCard;
import scopa.logic.ScopaHand;
import util.Logger;
import util.PlayerName;

public class BorderPanel extends JPanel {

	private String borderLayoutProperty; // region du board
	private ScopaHand hand;
	private JLabel playerName;

	public BorderPanel(PlayerName otherPlayer, int team, String borderLayoutProperty) {
		this(new OffuscatedHand(otherPlayer, team), borderLayoutProperty);
	}

	/**
	 * Default constructor for default properties
	 * @param hand
	 * @param borderLayoutProperty
	 */
	private BorderPanel(String borderLayoutProperty) {
		this.borderLayoutProperty = borderLayoutProperty;

		switch (borderLayoutProperty) {
		case BorderLayout.NORTH:
			this.setPreferredSize(new Dimension(400, 200));
			break;
		case BorderLayout.EAST:
			this.setPreferredSize(new Dimension(200, 400));
			break;
		case BorderLayout.WEST:
			this.setPreferredSize(new Dimension(200, 400));
			break;
		default:
			throw new IllegalArgumentException("BorderLayoutProperty should be BorderLayout.[NORTH/EAST/WEST], but was: " + borderLayoutProperty);
		}

		this.setBackground(ScopaGuiConstant.backgroundColor);
	}

	/**
	 * Empty border panel
	 * @param hand
	 * @param borderLayoutProperty
	 */
	public BorderPanel(EmptyHand hand, String borderLayoutProperty) {
		this(borderLayoutProperty);
		this.hand = hand;
	}

	/**
	 * Default public constructor with an offuscated hand
	 * @param hand
	 * @param borderLayoutProperty
	 */
	public BorderPanel(OffuscatedHand hand, String borderLayoutProperty) {
		this(borderLayoutProperty);

		if (BorderLayout.NORTH.equals(this.borderLayoutProperty)) {
			this.hand = new ScopaHandPanel(hand, ScopaHandPanel.Orientation.horizontal);
		} else {
			this.hand = new ScopaHandPanel(hand, ScopaHandPanel.Orientation.vertical);
		}

		playerName = new JLabel(hand.getPlayerName().getName());

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		if (BorderLayout.NORTH.equals(this.borderLayoutProperty)) {
			this.add((ScopaHandPanel) this.hand);
			this.add(playerName);
		} else {
			this.add(playerName);
			this.add((ScopaHandPanel) this.hand);
		}
	}

	public PlayerName getPlayerName() {
		return hand.getPlayerName();
	}

	public void newHand(List<ScopaCard> newCards) {
		hand.newHand(newCards);
		this.invalidate();
	}

	public boolean playCard(ScopaCard playedCard) {
		boolean ok = hand.playCard(playedCard);
		if (!ok) {
			Logger.error("Unable to play card " + playedCard.toString());
		}
		return ok;
	}

}