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
	}
	
	public boolean playCard(ScopaCard card){
		return true;
	}
	
	public boolean isEmpty(){
		return true;
	}
	
	public int getNumberCardInHand(){
		return 0;
	}
	
	public void addCardsToHeap(List<ScopaCard> cards){
	}
	
	public void addCardToHeap(ScopaCard card){
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
