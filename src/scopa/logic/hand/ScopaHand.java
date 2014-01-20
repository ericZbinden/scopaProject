package scopa.logic.hand;

import java.util.List;
import java.util.Map;

import scopa.logic.card.ScopaCard;
import scopa.logic.card.ScopaColor;
import scopa.logic.card.ScopaValue;
import util.PlayerName;

public interface ScopaHand {

	// PLAY METHODS

	public int getNumberCardInHand();

	public void newHand(List<ScopaCard> hand);

	public boolean playCard(ScopaCard card);

	public boolean isEmpty();

	public void addCardsToHeap(List<ScopaCard> taken);

	// MISC

	public PlayerName getPlayerName();

	public boolean isOffuscated();

	public List<ScopaCard> getCardsInHand();

	// RESULTS METHODS

	public void resetScore();

	public boolean took7OfGold();

	public int tookCardsTotal();

	public int tookGoldTotal();

	public int tookSevenTotal();

	public Map<ScopaColor, ScopaValue> tookBestCardInAllColor();

}