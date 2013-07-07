package scopa.logic;

import java.util.ArrayList;
import java.util.List;

public interface ScopaTable {
	
	/**
	 * @return true if no card is on the table
	 */
	public boolean isEmpty();
	/**
	 * Put a card on the field
	 * @param card
	 * @return the taken cards. Can be an empty list when it can't take anything. Is null if multiple possibility apply
	 */
	public List<ScopaCard> putCard(ScopaCard card);
	/**
	 * Put a card on the field and take the cards combinaison
	 * @param card
	 * @param cards
	 * @return the taken card. Can be an empty list when it can't take anything. Is null if this play is not ok
	 */
	public List<ScopaCard> putCard(ScopaCard card, List<ScopaCard> cards);
	/**
	 * At the end of the game, give all card
	 * @return all cards left on the table
	 */
	public List<ScopaCard> takeAll();
	/**
	 * Put the 4 first cards on the table
	 * @param cards
	 * @return true if game is ok, false if we need to redistribute
	 */
	public boolean putInitial(List<ScopaCard> cards);
	
/**
 * @param card
 * @return all possible outcome
 */
	public List<ArrayList<ScopaCard>> allPossibleTakeWith(ScopaCard card);
	
	/**
	 * 
	 * @return the list of cards on the table
	 */
	public List<ScopaCard> cardsOnTable();

}