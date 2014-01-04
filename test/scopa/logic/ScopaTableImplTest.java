package scopa.logic;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ScopaTableImplTest {

	ScopaTable table;
	ScopaCard sevenOfGold = new ScopaCard(ScopaValue.seven, ScopaColor.gold);
	ScopaCard sevenOfCup = new ScopaCard(ScopaValue.seven, ScopaColor.cup);
	ScopaCard sevenOfSword = new ScopaCard(ScopaValue.seven, ScopaColor.sword);
	ScopaCard twoOfGold = new ScopaCard(ScopaValue.two, ScopaColor.gold);
	ScopaCard fiveOfGold = new ScopaCard(ScopaValue.five, ScopaColor.gold);
	ScopaCard fiveOfCup = new ScopaCard(ScopaValue.five, ScopaColor.cup);
	ScopaCard kingOfGold = new ScopaCard(ScopaValue.king, ScopaColor.gold);
	ScopaCard kingOfSword = new ScopaCard(ScopaValue.king, ScopaColor.sword);
	ScopaCard kingOfCup = new ScopaCard(ScopaValue.king, ScopaColor.cup);

	@Test
	public void testEmptyTable() {
		table = ScopaFactory.getNewScopaTable();

		assertTrue(table.isEmpty());
		assertTrue(table.allPossibleTakeWith(sevenOfGold).isEmpty());
	}

	@Test
	public void test2SameValue() {
		table = ScopaFactory.getNewScopaTable();
		assertTrue(table.isEmpty());

		table.putCards(Arrays.asList(sevenOfCup, sevenOfSword));
		assertTrue(table.cardsOnTable().size() == 2);

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(sevenOfGold);

		assertTrue(possibleOutcome.size() == 2);
		// System.out.println(Arrays.toString(possibleOutcome.toArray()));
		assertTrue(possibleOutcome.contains(Arrays.asList(sevenOfCup)));
		assertTrue(possibleOutcome.contains(Arrays.asList(sevenOfSword)));
	}

	@Test
	public void testSameValueAndLowerCombinaison() {
		table = ScopaFactory.getNewScopaTable();
		table.putCards(Arrays.asList(sevenOfCup, fiveOfGold, twoOfGold));

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(sevenOfGold);

		assertTrue(possibleOutcome.size() == 1);
		assertTrue(possibleOutcome.get(0).equals(Arrays.asList(sevenOfCup)));
	}

	@Test
	public void testSameValueCombinaison() {
		table = ScopaFactory.getNewScopaTable();
		table.putCards(Arrays.asList(fiveOfCup, fiveOfGold, twoOfGold));

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(sevenOfGold);
		// System.out.println(Arrays.toString(possibleOutcome.toArray()));

		assertTrue(possibleOutcome.size() == 2);
		assertTrue(possibleOutcome.contains(Arrays.asList(fiveOfCup, twoOfGold)));
		assertTrue(possibleOutcome.contains(Arrays.asList(fiveOfGold, twoOfGold)));
	}

	@Test
	public void test2SameValueInCombinaison() {
		table = ScopaFactory.getNewScopaTable();
		table.putCards(Arrays.asList(fiveOfCup, fiveOfGold, twoOfGold));

		List<List<ScopaCard>> possibleOutcome = table.allPossibleTakeWith(kingOfGold);
		// System.out.println(Arrays.toString(possibleOutcome.toArray()));

		assertTrue(possibleOutcome.size() == 1);
		assertTrue(possibleOutcome.contains(Arrays.asList(fiveOfCup, fiveOfGold)));
	}

	@Test
	public void testPutInitialLessThan10() {
		table = ScopaFactory.getNewScopaTable();

		boolean result = table.putInitial(Arrays.asList(fiveOfCup, twoOfGold));

		assertTrue(table.isEmpty());
		assertFalse(result);
	}

	@Test
	public void testPutInitial3Kings() {
		table = ScopaFactory.getNewScopaTable();

		boolean result = table.putInitial(Arrays.asList(kingOfCup, kingOfGold, kingOfSword));

		assertTrue(table.isEmpty());
		assertFalse(result);
	}

	@Test
	public void testPutInitialOk() {
		table = ScopaFactory.getNewScopaTable();

		boolean result = table.putInitial(Arrays.asList(kingOfCup, sevenOfGold, fiveOfCup, fiveOfGold));

		assertTrue(table.cardsOnTable().size() == 4);
		assertTrue(result);
	}

}
