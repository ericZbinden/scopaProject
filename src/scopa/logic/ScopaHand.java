package scopa.logic;

import java.util.List;

import util.PlayerName;

public interface ScopaHand {

	public int getNumberCardInHand();

	public void newHand(List<ScopaCard> hand);

	public boolean playCard(ScopaCard card);

	public boolean isEmpty();

	public PlayerName getPlayerName();

	public List<ScopaCard> getHand();

	public void addCardsToHeap(List<ScopaCard> taken);

	public boolean isOffuscated();

}
