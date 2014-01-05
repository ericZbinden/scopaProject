package scopa.logic;

import java.util.ArrayList;
import java.util.List;

import util.PlayerName;

public class ScopaHandImpl implements ScopaHand {

	private List<ScopaCard> heap;

	private List<ScopaCard> hand;

	private int team;

	private PlayerName player;

	public ScopaHandImpl(PlayerName player, int team) {
		this.heap = new ArrayList<ScopaCard>();
		this.hand = new ArrayList<ScopaCard>(3);
		this.player = player;
		this.team = team;
	}

	@Override
	public List<ScopaCard> getHand() {
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
		} else
			return false;
	}

	@Override
	public boolean isEmpty() {
		return hand.isEmpty();
	}

	@Override
	public void addCardsToHeap(List<ScopaCard> cards) {
		heap.addAll(cards);
	}

	public int getTeam() {
		return team;
	}

	public void addCardToHeap(ScopaCard card) {
		heap.add(card);
	}

	public boolean containsGold7() {
		return heap.contains(new ScopaCard(ScopaValue.seven, ScopaColor.gold));
	}

	public int totalCards() {
		return heap.size();
	}

	public int totalGold() {
		int gold = 0;
		for (ScopaCard card : heap) {
			if (card.getColor() == ScopaColor.gold) {
				gold++;
			}
		}
		return gold;
	}

	public int totalSeven() {
		int seven = 0;
		for (ScopaCard card : heap) {
			if (card.getValue() == ScopaValue.seven) {
				seven++;
			}
		}
		return seven;
	}

	public List<ScopaCard> bestCardInAllColor() {
		ScopaCard gold = null;
		ScopaCard stick = null;
		ScopaCard sword = null;
		ScopaCard cup = null;

		for (ScopaCard card : heap) {
			switch (card.getColor()) {
			case gold:
				if (gold == null) {
					gold = card;
				}
				if (card.isHigherFor7(gold)) {
					gold = card;
				}
				break;
			case stick:
				if (stick == null) {
					stick = card;
				}
				if (card.isHigherFor7(stick)) {
					stick = card;
				}
				break;
			case sword:
				if (sword == null) {
					sword = card;
				}
				if (card.isHigherFor7(sword)) {
					sword = card;
				}
				break;
			case cup:
				if (cup == null) {
					cup = card;
				}
				if (card.isHigherFor7(cup)) {
					cup = card;
				}
				break;
			default:
				throw new RuntimeException("Unknown color of card: " + card.getColor());
			}
		}

		List<ScopaCard> cards = new ArrayList<ScopaCard>();
		if (cup != null) {
			cards.add(cup);
		}
		if (gold != null) {
			cards.add(gold);
		}
		if (sword != null) {
			cards.add(sword);
		}
		if (stick != null) {
			cards.add(stick);
		}

		return cards;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + team;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ScopaHandImpl))
			return false;
		ScopaHandImpl other = (ScopaHandImpl) obj;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (team != other.team)
			return false;
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

}
