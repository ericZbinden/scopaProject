package scopa.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaHand;
import util.PlayerName;

public class ScopaHandPanel implements ScopaHand {

	private ScopaHand hand;

	private Map<ScopaCard, CardLabel> cards;

	public ScopaHandPanel(ScopaHand hand) {
		this.hand = hand;
		cards = new HashMap<>();
		addAllCardsInGui();
	}

	private void addAllCardsInGui() {
		for (ScopaCard card : hand.getHand()) {
			cards.put(card, new CardLabel(card));
			// TODO add me into panel
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
			cards.remove(card);
			// TODO replace me from panel
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
	public List<ScopaCard> getHand() {
		return hand.getHand();
	}

	@Override
	public void addCardsToHeap(List<ScopaCard> taken) {
		hand.addCardsToHeap(taken);
	}

}
