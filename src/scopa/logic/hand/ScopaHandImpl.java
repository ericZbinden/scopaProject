package scopa.logic.hand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scopa.logic.card.ScopaCard;
import scopa.logic.card.ScopaColor;
import scopa.logic.card.ScopaValue;
import util.PlayerName;

public class ScopaHandImpl implements ScopaHand {

	// Kept for scores
	private List<ScopaCard> cardsTaken;
	private int goldTaken;
	private boolean sevenOfGoldTaken;
	private int sevenTaken;
	private Map<ScopaColor, ScopaValue> bestInAllColorTaken;

	// hands
	private List<ScopaCard> hand;
	private int team;
	private PlayerName player;

	public ScopaHandImpl(PlayerName player, int team) {
		this.cardsTaken = new ArrayList<ScopaCard>();
		this.hand = new ArrayList<ScopaCard>(3);
		this.player = player;
		this.team = team;

		goldTaken = 0;
		sevenOfGoldTaken = false;
		sevenTaken = 0;
		bestInAllColorTaken = new HashMap<>(4);
	}

	@Override
	public void resetScore() {
		cardsTaken = new ArrayList<>();
		goldTaken = 0;
		sevenOfGoldTaken = false;
		sevenTaken = 0;
		bestInAllColorTaken.clear();
		hand.clear();
	}

	@Override
	public List<ScopaCard> getCardsInHand() {
		return hand;
	}

	@Override
	public int getNumberCardInHand() {
		return hand.size();
	}

	@Override
	public void newHand(List<ScopaCard> hand) {
		this.hand = hand;
	}

	@Override
	public boolean playCard(ScopaCard card) {
		if (hand.contains(card)) {
			hand.remove(card);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isEmpty() {
		return hand.isEmpty();
	}

	@Override
	public void addCardsToHeap(List<ScopaCard> cards) {
		for (ScopaCard card : cards) {
			addCardToHeap(card);
		}
	}

	public int getTeam() {
		return team;
	}

	private void addCardToHeap(ScopaCard card) {
		cardsTaken.add(card);
		if (ScopaColor.gold.equals(card.getColor())) {
			goldTaken++;
			if (ScopaValue.seven.equals(card.getValue())) {
				sevenOfGoldTaken = true;
				sevenTaken++;
			}
		} else {
			if (ScopaValue.seven.equals(card.getValue())) {
				sevenTaken++;
				bestInAllColorTaken.put(card.getColor(), card.getValue());
			}
		}
		updateBestCardTaken(card);
	}

	private void updateBestCardTaken(ScopaCard card) {
		ScopaValue currentBest = bestInAllColorTaken.get(card.getColor());
		if (card.getValue().isHigherFor7(currentBest)) {
			bestInAllColorTaken.put(card.getColor(), card.getValue());
		}
	}

	@Override
	public boolean took7OfGold() {
		return sevenOfGoldTaken;
	}

	@Override
	public int tookCardsTotal() {
		return cardsTaken.size();
	}

	@Override
	public int tookGoldTotal() {
		return goldTaken;
	}

	@Override
	public int tookSevenTotal() {
		return sevenTaken;
	}

	@Override
	public Map<ScopaColor, ScopaValue> tookBestCardInAllColor() {
		return bestInAllColorTaken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + team;
		return result;
	}

	/**
	 * Equals based on PlayerName & team
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScopaHandImpl)) {
			return false;
		}
		ScopaHandImpl other = (ScopaHandImpl) obj;
		if (player == null) {
			if (other.player != null) {
				return false;
			}
		} else if (!player.equals(other.player)) {
			return false;
		}
		if (team != other.team) {
			return false;
		}
		return true;
	}

	@Override
	public PlayerName getPlayerName() {
		return player;
	}

	@Override
	public boolean isOffuscated() {
		return false;
	}

	@Override
	public String toString() {
		return "hand=" + hand + "\nplayer=" + player;
	}

}
