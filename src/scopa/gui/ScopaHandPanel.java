package scopa.gui;

import gui.util.EmptyPanel;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import scopa.logic.card.ScopaCard;
import scopa.logic.card.ScopaColor;
import scopa.logic.card.ScopaValue;
import scopa.logic.hand.ScopaHand;
import util.Logger;
import util.PlayerName;

public class ScopaHandPanel extends JPanel implements ScopaHand, MouseListener {

	public enum Orientation {
		vertical, horizontal
	}

	private ScopaHand hand;
	private ScopaGamePanel parent; // FIXME should be an interface
	private boolean hasEventHandler = false;

	private CardLabel card1;
	private CardLabel card2;
	private CardLabel card3;

	/**
	 * Constructor without mouseEventHandler
	 * @param hand
	 */
	public ScopaHandPanel(ScopaHand hand, Orientation orientation) {
		this.hand = hand;

		card1 = new CardLabel();
		card2 = new CardLabel();
		card3 = new CardLabel();

		if (Orientation.horizontal.equals(orientation)) {
			this.setLayout(new GridLayout(1, 7));
			this.add(new EmptyPanel());
			this.add(new EmptyPanel());
		} else if (Orientation.vertical.equals(orientation)) {
			this.setLayout(new GridLayout(3, 1));
		}

		this.add(card1);
		this.add(card2);
		this.add(card3);

		if (Orientation.horizontal.equals(orientation)) {
			this.add(new EmptyPanel());
			this.add(new EmptyPanel());
		}

		this.setOpaque(false);

		addAllCardsInGui();
	}

	/**
	 * Constructor with mouseEventHandler
	 * @param hand
	 * @param parent
	 */
	public ScopaHandPanel(ScopaHand hand, ScopaGamePanel parent, Orientation orientation) {
		this(hand, orientation);
		this.parent = parent;
		hasEventHandler = true;
		card1.addMouseListener(this);
		card2.addMouseListener(this);
		card3.addMouseListener(this);
	}

	private void addAllCardsInGui() {
		List<ScopaCard> handy = hand.getCardsInHand();

		if (handy.size() == 3) {
			card1.setCard(handy.get(0));
			card1.setTransferHandler(new ScopaCardTransfertHandler(parent));

			card2.setCard(handy.get(1));
			card2.setTransferHandler(new ScopaCardTransfertHandler(parent));

			card3.setCard(handy.get(2));
			card3.setTransferHandler(new ScopaCardTransfertHandler(parent));

			this.revalidate();
		} else {
			Logger.debug("Hum... strange... hand size is: " + handy.size() + " but was espected 3");
		}
	}

	@Override
	public int getNumberCardInHand() {
		return hand.getNumberCardInHand();
	}

	@Override
	public void newHand(List<ScopaCard> newHand) {
		this.hand.newHand(newHand);
		addAllCardsInGui();

	}

	@Override
	public boolean playCard(ScopaCard card) {
		boolean ok = hand.playCard(card);
		if (ok) {

			if (hand.isOffuscated()) {
				if (!card1.isEmpty()) {
					card1.setCard(null);
				} else if (!card2.isEmpty()) {
					card2.setCard(null);
				} else if (!card3.isEmpty()) {
					card3.setCard(null);
				} else {
					Logger.error(hand.getPlayerName() + " tried to play " + card.toString() + " but was not in player hand");
				}
			} else {
				if (card.equals(card1.getCard())) {
					card1.setCard(null);
				} else if (card.equals(card2.getCard())) {
					card2.setCard(null);
				} else if (card.equals(card3.getCard())) {
					card3.setCard(null);
				} else {
					Logger.error("You played a card you don't have.\n\tPlayed: " + card.toString() + "\n\tHave: "
							+ Arrays.toString(hand.getCardsInHand().toArray()));
				}
			}

			this.repaint();
		} else {
			Logger.debug("Move not valid, ignore it");
		}
		return ok;
	}

	@Override
	public boolean isEmpty() {
		return hand.isEmpty();
	}

	@Override
	public PlayerName getPlayerName() {
		return hand.getPlayerName();
	}

	@Override
	public List<ScopaCard> getCardsInHand() {
		return hand.getCardsInHand();
	}

	@Override
	public void addCardsToHeap(List<ScopaCard> taken) {
		hand.addCardsToHeap(taken);
	}

	private void tryPlay(ScopaCard playedCard, List<ScopaCard> takenCards) {
		if (parent.play(playedCard, takenCards)) {
			playCard(playedCard);
			parent.sendMsgScopaPlay(playedCard, takenCards);
		} else {
			parent.showWarningToPlayer("Invalid move");
		}
	}

	@Override
	public boolean isOffuscated() {
		return hand.isOffuscated();
	}

	private boolean isLocalClientNextPlayer() {
		return parent.getNextPlayerToPlay().equals(this.getPlayerName());
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (!hasEventHandler) {
			return;
		}

		if (!isLocalClientNextPlayer()) {
			Logger.debug("Not your time to play dear " + getPlayerName());
			return;
		}

		Object src = arg0.getSource();
		if (src instanceof CardLabel) {
			CardLabel source = (CardLabel) src;

			if (source.isEmpty()) {
				return;
			}

			ScopaCard playedCard = source.getCard();
			List<ScopaCard> selectedOnTable = parent.getSelectedCardsOnTable();

			if (selectedOnTable.isEmpty()) {
				List<List<ScopaCard>> possibleOutcome = parent.allPossibleTakeWith(playedCard);
				if (possibleOutcome.size() == 1) {
					tryPlay(playedCard, possibleOutcome.get(0));
				} else if (possibleOutcome.isEmpty()) {
					tryPlay(playedCard, new ArrayList<ScopaCard>(0));
				} else {
					parent.showWarningToPlayer("Multiple choice, please select one.");
				}
			} else {
				tryPlay(playedCard, selectedOnTable);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Nothing
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO drag & drop
		// Object src = arg0.getSource();
		// if (src instanceof CardLabel) {
		// CardLabel source = (CardLabel) src;
		// source.getTransferHandler().exportAsDrag(source, arg0,
		// TransferHandler.MOVE);
		// }
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Nothing
	}

	@Override
	public void resetScore() {
		hand.resetScore();
	}

	@Override
	public boolean took7OfGold() {
		return hand.took7OfGold();
	}

	@Override
	public int tookCardsTotal() {
		return hand.tookCardsTotal();
	}

	@Override
	public int tookGoldTotal() {
		return hand.tookGoldTotal();
	}

	@Override
	public int tookSevenTotal() {
		return hand.tookSevenTotal();
	}

	@Override
	public Map<ScopaColor, ScopaValue> tookBestCardInAllColor() {
		return hand.tookBestCardInAllColor();
	}

}
