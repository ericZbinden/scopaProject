package scopa.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScopaTableImpl extends ArrayList<ScopaCard> implements ScopaTable {

	private static final long serialVersionUID = 6791600481344640732L;

	ScopaTableImpl() {

	}

	@Override
	public List<ScopaCard> putCard(ScopaCard card) {
		return putCard(card, new ArrayList<ScopaCard>(0));
	}

	@Override
	public List<ScopaCard> putCard(ScopaCard card, List<ScopaCard> cards) {
		List<List<ScopaCard>> takes = allPossibleTakeWith(card);

		if (cards == null) {
			cards = Arrays.asList(); // avoid null
		}

		if (takes.size() == 0 && cards.isEmpty()) {
			this.add(card);
			return new ArrayList<ScopaCard>();
		}

		for (List<ScopaCard> t : takes) {
			if (t.size() == cards.size() && t.containsAll(cards)) {
				this.removeAll(t);
				t.add(card);
				return t;
			}
		}
		return null;
	}

	@Override
	public boolean putInitial(List<ScopaCard> cards) {
		int value = 0;
		int king = 0;
		for (ScopaCard c : cards) {
			value += ScopaValue.val(c);
			if (ScopaValue.king.equals(c.getValue())) {
				king++;
			}
		}
		if (value <= 10)
			return false;
		else if (king > 2)
			return false;
		else {
			this.addAll(cards);
			return true;
		}

	}

	@Override
	public List<ScopaCard> takeAll() {
		List<ScopaCard> cards = new ArrayList<ScopaCard>(this);
		this.clear();
		return cards;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public List<List<ScopaCard>> allPossibleTakeWith(ScopaCard card) {
		int cardValue = ScopaValue.val(card);
		List<ScopaSum> constructPossible = new ArrayList<>();
		boolean sameValue = containsSameValueThan(card);

		for (ScopaCard c : this) {

			if (c.isEqualInValue(card)) {
				constructPossible.add(new ScopaSum(Arrays.asList(c)));

			} else if (c.isSmaller(card) && !sameValue) {
				int cValue = ScopaValue.val(c);
				List<ScopaSum> newSums = new ArrayList<>();

				for (ScopaSum sum : constructPossible) {
					int total = sum.getSum() + cValue;
					if (total <= cardValue) {
						ScopaSum newSum = new ScopaSum(sum);
						newSum.addCard(c);
						newSums.add(newSum);
					}
				}
				ScopaSum newCurrent = new ScopaSum();
				newCurrent.addCard(c);
				newSums.add(newCurrent);
				constructPossible.addAll(newSums);
			}
		}

		List<List<ScopaCard>> possible = new ArrayList<>();
		for (ScopaSum sum : constructPossible) {
			if (sum.getSum() == cardValue) {
				possible.add(sum.getCardList());
			}
		}

		return possible;
	}

	private boolean containsSameValueThan(ScopaCard card) {
		for (ScopaCard c : this) {
			if (c.isEqualInValue(card))
				return true;
		}
		return false;
	}

	@Override
	public List<ScopaCard> cardsOnTable() {
		return this;
	}

	@Override
	public void putCards(List<ScopaCard> cards) {
		this.addAll(cards);
	}

}
