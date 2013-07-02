package scopa.logic;

import java.util.Arrays;
import java.util.List;

/**
 * Empty Hand used to be integrated into gui for non playing slot
 * @author Coubii
 *
 */
public class EmptyHand extends ScopaHand {

	public EmptyHand() {
		super("", 0);
	}
	
	public List<ScopaCard> getHand(){
		return Arrays.asList();
	}
	
	public void newHand(List<ScopaCard> hand){
		throw new IllegalStateException("EmptyHand can not be filled");
	}
	
	public boolean playCard(ScopaCard card){
		throw new IllegalStateException("EmptyHand can not play a card");

	}
	
	public boolean isEmpty(){
		return true;
	}
	
	public void addCardsToHeap(List<ScopaCard> cards){
		throw new IllegalStateException("EmptyHand can not have a heap");
	}
	
	public void addCardToHeap(ScopaCard card){
		throw new IllegalStateException("EmptyHand can not have a heap");
	}
	
	public boolean containsGold7(){
		return false;
	}
	
	public int totalCards(){
		return 0;
	}
	
	public int totalGold(){
		return 0;
	}
	
	public int totalSeven(){
		return 0;
	}
	
	public List<ScopaCard> bestCardInAllColor(){
		return Arrays.asList();
	}

}
