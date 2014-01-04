package scopa.logic;

import java.util.ArrayList;
import java.util.List;

public class ScopaSum {

	private List<ScopaCard> cards;

	private int sum;

	public ScopaSum() {
		sum = 0;
		cards = new ArrayList<>();
	}

	public ScopaSum(List<ScopaCard> cards) {
		this.cards = new ArrayList<>();
		sum = 0;
		addCards(cards);
	}

	public ScopaSum(ScopaSum sum) {
		this.cards = new ArrayList<>(sum.getCardList());
		this.sum = sum.getSum();
	}

	public void addCard(ScopaCard card) {
		cards.add(card);
		sum += ScopaValue.val(card);
	}

	public void addCards(List<ScopaCard> cards) {
		for (ScopaCard card : cards) {
			addCard(card);
		}
	}

	public int getSum() {
		return sum;
	}

	public List<ScopaCard> getCardList() {
		return cards;
	}

}
