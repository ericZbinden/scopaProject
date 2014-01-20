package scopa.logic;

import scopa.logic.card.OffuscatedScopaCard;
import scopa.logic.card.ScopaCard;
import scopa.logic.card.ScopaColor;
import scopa.logic.card.ScopaValue;

public class ScopaTestUtil {

	public static ScopaCard kingOfGold = new ScopaCard(ScopaValue.king, ScopaColor.gold);
	public static ScopaCard kingOfSword = new ScopaCard(ScopaValue.king, ScopaColor.sword);
	public static ScopaCard kingOfCup = new ScopaCard(ScopaValue.king, ScopaColor.cup);

	public static ScopaCard sevenOfGold = new ScopaCard(ScopaValue.seven, ScopaColor.gold);
	public static ScopaCard sevenOfCup = new ScopaCard(ScopaValue.seven, ScopaColor.cup);
	public static ScopaCard sevenOfSword = new ScopaCard(ScopaValue.seven, ScopaColor.sword);
	public static ScopaCard sevenOfStick = new ScopaCard(ScopaValue.seven, ScopaColor.stick);

	public static ScopaCard sixOfGold = new ScopaCard(ScopaValue.six, ScopaColor.gold);

	public static ScopaCard fiveOfGold = new ScopaCard(ScopaValue.five, ScopaColor.gold);
	public static ScopaCard fiveOfCup = new ScopaCard(ScopaValue.five, ScopaColor.cup);

	public static ScopaCard twoOfGold = new ScopaCard(ScopaValue.two, ScopaColor.gold);

	public static ScopaCard aceOfGold = new ScopaCard(ScopaValue.as, ScopaColor.gold);

	public static ScopaCard offuscatedCard = new OffuscatedScopaCard();

}
