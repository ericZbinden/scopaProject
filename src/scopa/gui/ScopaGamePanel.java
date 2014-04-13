package scopa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import scopa.com.MsgScopaPlay;
import scopa.logic.ScopaGame;
import scopa.logic.card.OffuscatedScopaCard;
import scopa.logic.card.ScopaCard;
import scopa.logic.hand.EmptyHand;
import scopa.logic.hand.ScopaHand;
import util.Logger;
import util.PlayerName;

public class ScopaGamePanel extends JPanel {

	private Dimension minSize = new Dimension(800, 800);
	private boolean built = false;

	private ScopaGui parent;

	private PlayerBorderPanel south;
	private BorderPanel west;
	private BorderPanel east;
	private BorderPanel north;
	private ScopaTablePanel table;

	public ScopaGamePanel(ScopaGui parent) {
		this.parent = parent;
		this.setPreferredSize(minSize);
		this.setBackground(ScopaConstant.backgroundColor);
		this.setMinimumSize(this.getPreferredSize());
		this.setLayout(new BorderLayout());
	}

	public void build(ScopaHand playerHand, PlayerName eastPlayer, PlayerName northPlayer, PlayerName westPlayer, List<ScopaCard> tableInitial) {

		this.south = new PlayerBorderPanel(playerHand, this);
		if (eastPlayer == null) {
			this.east = new BorderPanel(new EmptyHand(), BorderLayout.EAST);
		} else {
			this.east = new BorderPanel(eastPlayer, 0, BorderLayout.EAST);
		}
		if (westPlayer == null) {
			this.west = new BorderPanel(new EmptyHand(), BorderLayout.WEST);
		} else {
			this.west = new BorderPanel(westPlayer, 0, BorderLayout.WEST);
		}
		if (northPlayer == null) {
			this.north = new BorderPanel(new EmptyHand(), BorderLayout.NORTH);
		} else {
			this.north = new BorderPanel(northPlayer, 0, BorderLayout.NORTH);
		}

		this.table = new ScopaTablePanel();
		table.putInitial(tableInitial);

		this.add(south, BorderLayout.SOUTH);
		this.add(west, BorderLayout.WEST);
		this.add(east, BorderLayout.EAST);
		this.add(north, BorderLayout.NORTH);
		this.add(table, BorderLayout.CENTER);

		built = true;
	}

	public void giveNewHand(List<ScopaCard> playerCards) {
		south.newHand(playerCards);

		List<OffuscatedScopaCard> offuscatedHand = Arrays.asList(new OffuscatedScopaCard(), new OffuscatedScopaCard(), new OffuscatedScopaCard());
		west.newHand(offuscatedHand);
		east.newHand(offuscatedHand);
		north.newHand(offuscatedHand);
	}

	public void southPlay(ScopaCard playedCard) {
		south.playCard(playedCard);
		south.repaint();
	}

	public void northPlay() {
		north.playCard(new OffuscatedScopaCard());
		north.repaint();
	}

	public void eastPlay() {
		east.playCard(new OffuscatedScopaCard());
		east.repaint();
	}

	public void westPlay() {
		west.playCard(new OffuscatedScopaCard());
		west.repaint();
	}

	public void playedByNetworkClient(PlayerName name, ScopaCard playedCard, List<ScopaCard> taken) {
		if (west.getPlayerName().equals(name)) {
			westPlay();
		} else if (east.getPlayerName().equals(name)) {
			eastPlay();
		} else if (north.getPlayerName().equals(name)) {
			northPlay();
		} else {
			Logger.error("Unknown player " + name + ". Move ignored");
			return;
		}

		table.putCard(playedCard, taken);
	}

	//	public List<ScopaCard> clientPlayedOnTable(ScopaCard card) {
	//		List<ScopaCard> cards = table.putCard(card);
	//		return cards;
	//	}

	public List<ScopaCard> getSelectedCardsOnTable() {
		return table.getSelectedCards();
	}

	public List<List<ScopaCard>> allPossibleTakeWith(ScopaCard card) {
		return table.allPossibleTakeWith(card);
	}

	//Called by local client when he play
	public boolean playedByLocalClient(ScopaCard playedCard, List<ScopaCard> taken) {
		List<ScopaCard> cards = table.putCard(playedCard, taken);

		if (cards == null) {
			return false;
		}
		parent.updateLastMove(playedCard, taken);
		parent.updateNextPlayer(ScopaGame.SRV_NAME); // avoid double play
		return true;
	}

	public void sendMsgScopaPlay(ScopaCard played, List<ScopaCard> taken) {
		MsgScopaPlay msg = new MsgScopaPlay(parent.getLocalClient(), played, taken, null);
		parent.sendMsgPlay(msg);
	}

	public void alertPlayerWrongPlay() {
		parent.showMessageDialog("The move you tried to do is not a valid move");
	}

	public void showWarningToPlayer(String message) {
		parent.showMessageDialog(message);
	}

	public boolean isLocalPlayerNextToPlay() {
		return parent.isLocalPlayerNextToPlay();
	}

	public void updateLastMoveWithLocalPlay(ScopaCard played, List<ScopaCard> taken) {
		parent.updateLastMove(played, taken);
	}

	@Override
	public ScopaGamePanel clone() {
		ScopaGamePanel sgp = new ScopaGamePanel(parent);

		if (built) {
			// FIXME does not clone properly
			// sgp.build(south.getHand(), east.getPlayerName(),
			// north.getPlayerName(), west.getPlayerName());
			// sgp.table.putCards(table.cardsOnTable());
			// sgp.nextPlayer = new PlayerName(nextPlayer.getName());
		}

		return sgp;
	}
}
