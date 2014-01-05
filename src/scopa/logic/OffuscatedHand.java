package scopa.logic;

import java.util.ArrayList;
import java.util.List;

import util.PlayerName;

public class OffuscatedHand implements ScopaHand {

	private int nbCards;
	private PlayerName playerName;
	private int team;
	private int cardsTaken;

	public OffuscatedHand(PlayerName playerName, int team) {
		this.playerName = playerName;
		this.team = team;
		nbCards = 3;
		cardsTaken = 0;
	}

	@Override
	public List<ScopaCard> getHand() {

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
	public void newHand(List<ScopaCard> hand) {
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
	public PlayerName getPlayerName() {
		return playerName;
	}

	@Override
	public void addCardsToHeap(List<ScopaCard> taken) {
		cardsTaken += taken.size();
	}

	@Override
	public boolean isOffuscated() {
		return true;
	}
}