package scopa.logic;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import scopa.logic.card.ScopaCard;

public class ScopaTableImplTest {

	ScopaTable table;

	@Test
	public void testEmptyTable() {
		table = ScopaFactory.getNewScopaTable();

		assertTrue(table.isEmpty());
		assertTrue(table.allPossibleTakeWith(ScopaTestUtil.sevenOfGold).isEmpty());
	}

	@Test
	public void test2SameValue() {
		table = ScopaFactory.getNewScopaTable();
		assertTrue(table.isEmpty());

		table.putCards(Arrays.asList(ScopaTestUtil.sevenOfCup, ScopaTestUtil.sevenOfSword));
		assertTrue(table.cardsOnTable().size() == 2);

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(ScopaTestUtil.sevenOfGold);

		assertTrue(possibleOutcome.size() == 2);
		// System.out.println(Arrays.toString(possibleOutcome.toArray()));
		assertTrue(possibleOutcome.contains(Arrays.asList(ScopaTestUtil.sevenOfCup)));
		assertTrue(possibleOutcome.contains(Arrays.asList(ScopaTestUtil.sevenOfSword)));
	}

	@Test
	public void testSameValueAndLowerCombinaison() {
		table = ScopaFactory.getNewScopaTable();
		table.putCards(Arrays.asList(ScopaTestUtil.sevenOfCup, ScopaTestUtil.fiveOfGold, ScopaTestUtil.twoOfGold));

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(ScopaTestUtil.sevenOfGold);

		assertTrue(possibleOutcome.size() == 1);
		assertTrue(possibleOutcome.get(0).equals(Arrays.asList(ScopaTestUtil.sevenOfCup)));
	}

	@Test
	public void testSameValueCombinaison() {
		table = ScopaFactory.getNewScopaTable();
		table.putCards(Arrays.asList(ScopaTestUtil.fiveOfCup, ScopaTestUtil.fiveOfGold, ScopaTestUtil.twoOfGold));

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(ScopaTestUtil.sevenOfGold);
		// System.out.println(Arrays.toString(possibleOutcome.toArray()));

		assertTrue(possibleOutcome.size() == 2);
		assertTrue(possibleOutcome.contains(Arrays.asList(ScopaTestUtil.fiveOfCup, ScopaTestUtil.twoOfGold)));
		assertTrue(possibleOutcome.contains(Arrays.asList(ScopaTestUtil.fiveOfGold, ScopaTestUtil.twoOfGold)));
	}

	@Test
	public void test2SameValueInCombinaison() {
		table = ScopaFactory.getNewScopaTable();
		table.putCards(Arrays.asList(ScopaTestUtil.fiveOfCup, ScopaTestUtil.fiveOfGold, ScopaTestUtil.twoOfGold));

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(ScopaTestUtil.kingOfGold);
		// System.out.println(Arrays.toString(possibleOutcome.toArray()));

		assertTrue(possibleOutcome.size() == 1);
		assertTrue(possibleOutcome.contains(Arrays.asList(ScopaTestUtil.fiveOfCup, ScopaTestUtil.fiveOfGold)));
	}

	@Test
	public void testNoCombinaison() {
		table = ScopaFactory.getNewScopaTable();
		table.putCards(Arrays.asList(ScopaTestUtil.fiveOfCup, ScopaTestUtil.fiveOfGold, ScopaTestUtil.kingOfCup));

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(ScopaTestUtil.sevenOfGold);
		// System.out.println(Arrays.toString(possibleOutcome.toArray()));

		assertTrue(possibleOutcome.isEmpty());
	}

	@Test
	public void testPutInitialLessThan10() {
		table = ScopaFactory.getNewScopaTable();

		boolean result = table.putInitial(Arrays.asList(ScopaTestUtil.fiveOfCup, ScopaTestUtil.twoOfGold));

		assertTrue(table.isEmpty());
		assertFalse(result);
	}

	@Test
	public void testPutInitial3Kings() {
		table = ScopaFactory.getNewScopaTable();

		boolean result = table.putInitial(Arrays.asList(ScopaTestUtil.kingOfCup, ScopaTestUtil.kingOfGold, ScopaTestUtil.kingOfSword));

		assertTrue(table.isEmpty());
		assertFalse(result);
	}

	@Test
	public void testPutInitialOk() {
		table = ScopaFactory.getNewScopaTable();

		boolean result = table.putInitial(Arrays
				.asList(ScopaTestUtil.kingOfCup, ScopaTestUtil.sevenOfGold, ScopaTestUtil.fiveOfCup, ScopaTestUtil.fiveOfGold));

		assertTrue(table.cardsOnTable().size() == 4);
		assertTrue(result);
	}

}
