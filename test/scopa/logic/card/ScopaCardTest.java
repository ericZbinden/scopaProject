package scopa.logic.card;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import scopa.logic.ScopaDeckImpl;
import scopa.logic.ScopaFactory;
import scopa.logic.ScopaTestUtil;
import scopa.logic.hand.ScopaHand;
import scopa.logic.hand.ScopaHandImpl;
import util.PlayerName;

public class ScopaCardTest {

	public ScopaCardTest() {
	}

	@Test
	public void testCompareValue() {
		assertTrue(0 == ScopaTestUtil.fiveOfCup.getValue().compareTo(ScopaTestUtil.fiveOfGold.getValue()));
		assertTrue(0 < ScopaTestUtil.sevenOfCup.getValue().compareTo(ScopaTestUtil.fiveOfGold.getValue()));
		assertTrue(0 > ScopaTestUtil.fiveOfCup.getValue().compareTo(ScopaTestUtil.sevenOfCup.getValue()));
	}

	@Test
	public void testCompareColor() {
		assertTrue(0 == ScopaTestUtil.fiveOfCup.getColor().compareTo(ScopaTestUtil.sevenOfCup.getColor()));
		// Gold before cup
		assertTrue(0 < ScopaTestUtil.fiveOfCup.getColor().compareTo(ScopaTestUtil.fiveOfGold.getColor()));
		assertTrue(0 > ScopaTestUtil.fiveOfGold.getColor().compareTo(ScopaTestUtil.fiveOfCup.getColor()));
	}

	@Test
	public void testPartialOffuscated() {
		ScopaCard valueOffuscated = new ScopaCard(ScopaValue.seven, ScopaColor.offuscated);
		ScopaCard colorOffuscated = new ScopaCard(ScopaValue.offuscated, ScopaColor.gold);

		assertTrue(valueOffuscated.isOffuscated());
		assertTrue(colorOffuscated.isOffuscated());
	}

	@Test
	public void testTotalOffuscated() {
		assertTrue(ScopaTestUtil.offuscatedCard.isOffuscated());
	}

	@Test
	public void testIsHigherFor7() {
		// best than null
		assertTrue(ScopaTestUtil.twoOfGold.isHigherFor7(null));
		// best than offuscated
		assertTrue(ScopaTestUtil.twoOfGold.isHigherFor7(ScopaTestUtil.offuscatedCard));
		assertFalse(ScopaTestUtil.offuscatedCard.isHigherFor7(ScopaTestUtil.twoOfGold));

		// 7
		ScopaDeckImpl deck = (ScopaDeckImpl) ScopaFactory.getNewScopaDeck();
		for (ScopaCard card : deck) {
			if (ScopaValue.seven.equals(card.getValue())) {
				assertFalse(ScopaTestUtil.sevenOfGold.isHigherFor7(card));
			} else {
				assertTrue(ScopaTestUtil.sevenOfGold.isHigherFor7(card));
			}
		}

		// 6
		for (ScopaCard card : deck) {
			if (ScopaValue.six.equals(card.getValue()) || ScopaValue.seven.equals(card.getValue())) {
				assertFalse(ScopaTestUtil.sixOfGold.isHigherFor7(card));
			} else {
				assertTrue(ScopaTestUtil.sixOfGold.isHigherFor7(card));
			}
		}

		// AS
		for (ScopaCard card : deck) {
			if (ScopaValue.six.equals(card.getValue()) || ScopaValue.seven.equals(card.getValue()) || ScopaValue.as.equals(card.getValue())) {
				assertFalse(ScopaTestUtil.aceOfGold.isHigherFor7(card));
			} else {
				assertTrue(ScopaTestUtil.aceOfGold.isHigherFor7(card));
			}
		}

		// KING
		for (ScopaCard card : deck) {
			if (ScopaValue.six.equals(card.getValue()) || ScopaValue.seven.equals(card.getValue()) || ScopaValue.as.equals(card.getValue())
					|| ScopaValue.king.equals(card.getValue())) {
				assertFalse(ScopaTestUtil.kingOfCup.isHigherFor7(card));
			} else {
				assertTrue(ScopaTestUtil.kingOfCup.isHigherFor7(card));
			}
		}

		// FIVE
		for (ScopaCard card : deck) {
			if (ScopaTestUtil.fiveOfCup.isSmaller(card) || ScopaTestUtil.fiveOfCup.isEqualInValue(card) || ScopaValue.as.equals(card.getValue())) {
				assertFalse(ScopaTestUtil.fiveOfCup.isHigherFor7(card));
			} else {
				assertTrue(ScopaTestUtil.fiveOfCup.isHigherFor7(card));
			}
		}
	}

	@Test
	public void testIsBestFor7() {
		// todo move to ScopaHandImplTest
		ScopaDeckImpl deck = (ScopaDeckImpl) ScopaFactory.getNewScopaDeck();
		ScopaHand hand = new ScopaHandImpl(new PlayerName("Coubii"), 1);

		// SEVEN
		hand.addCardsToHeap(deck);
		Map<ScopaColor, ScopaValue> best = hand.tookBestCardInAllColor();
		assertTrue(best.size() == 4);
		for (ScopaValue val : best.values()) {
			assertTrue(ScopaValue.seven.equals(val));
		}

		hand.resetScore();
		deck.remove(ScopaTestUtil.sevenOfCup);
		deck.remove(ScopaTestUtil.sevenOfGold);
		deck.remove(ScopaTestUtil.sevenOfStick);
		deck.remove(ScopaTestUtil.sevenOfSword);

		// SIX
		hand.addCardsToHeap(deck);
		best = hand.tookBestCardInAllColor();
		assertTrue(best.size() == 4);
		for (ScopaValue val : best.values()) {
			assertTrue(ScopaValue.six.equals(val));
		}

	}

}
