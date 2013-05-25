package game.scopa.logic;

import java.util.List;

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
