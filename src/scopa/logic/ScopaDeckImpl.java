package scopa.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scopa.logic.card.ScopaCard;
import scopa.logic.card.ScopaColor;
import scopa.logic.card.ScopaValue;

public class ScopaDeckImpl extends ArrayList<ScopaCard> implements ScopaDeck {

	private static final long serialVersionUID = 7062358612411467718L;

	ScopaDeckImpl() {
		super();

		for (ScopaColor color : ScopaColor.values()) {
			if (ScopaColor.offuscated.equals(color)) {
				continue;
			}

			for (ScopaValue value : ScopaValue.values()) {
				if (ScopaValue.offuscated.equals(value)) {
					continue;
				}

				ScopaCard card = new ScopaCard(value, color);
				this.add(card);
			}
		}
	}

	@Override
	public void shuffle() {
		Collections.shuffle(this);

	}

	@Override
	public ScopaCard drawCard() {
		if (this.isEmpty())
			throw new IllegalStateException("can not draw card from an empty deck");

		return this.remove(0);
	}

	private List<ScopaCard> drawNCards(int n) {
		List<ScopaCard> cards = new ArrayList<ScopaCard>(n);
		for (int i = 1; i <= n; i++) {
			cards.add(this.drawCard());
		}
		return cards;

	}

	@Override
	public List<ScopaCard> draw3Cards() {
		return drawNCards(3);
	}

	@Override
	public List<ScopaCard> drawInitialCards() {
		return drawNCards(4);
	}

}
