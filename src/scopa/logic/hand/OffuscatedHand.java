package scopa.logic.hand;

import java.util.ArrayList;
import java.util.List;

import scopa.logic.card.OffuscatedScopaCard;
import scopa.logic.card.ScopaCard;
import util.PlayerName;

public class OffuscatedHand extends ScopaHandImpl {

	private int nbCards;

	public OffuscatedHand(PlayerName playerName, int team) {
		super(playerName, team);
		nbCards = 3;
	}

	@Override
	public List<ScopaCard> getCardsInHand() {

		List<ScopaCard> cards = new ArrayList<>(nbCards);

		for (int i = 1; i <= nbCards; i++) {
			cards.add(new OffuscatedScopaCard());
		}
		return cards;
	}

	@Override
	public int getNumberCardInHand() {
		return nbCards;
	}

	@Override
	public <T extends ScopaCard> void newHand(List<T> hand) {
		nbCards = hand.size();
	}

	@Override
	public boolean playCard(ScopaCard card) {
		nbCards--;
		return true;
	}

	@Override
	public boolean isEmpty() {
		return nbCards == 0;
	}

	@Override
	public boolean isOffuscated() {
		return true;
	}
}