package scopa.logic;

import java.util.List;

import scopa.logic.card.ScopaCard;

public interface ScopaDeck {

	/**
	 * Shuffle the deck
	 */
	public void shuffle();

	/**
	 * Pick up a card from the deck
	 * @return a card
	 */
	public ScopaCard drawCard();

	/**
	 * Draw a new hand of three cards
	 * @return 3 cards
	 */
	public List<ScopaCard> draw3Cards();

	/**
	 * 
	 * @return true if the deck is empty
	 */
	public boolean isEmpty();

	/**
	 * 
	 * @return the four initial cards
	 */
	public List<ScopaCard> drawInitialCards();

}
