package scopa.logic;

import java.util.Arrays;
import java.util.List;

import util.PlayerName;
import util.ReservedName;

/**
 * Empty Hand used to be integrated into gui for non playing slot
 * @author Coubii
 *
 */
public class EmptyHand extends ScopaHand {

	public EmptyHand() {
		super(new PlayerName(ReservedName.EMPTY_NAME), 0);
	}
	
	@Override
	public List<ScopaCard> getHand(){
		return Arrays.asList();
	}
	
	@Override
	public void newHand(List<ScopaCard> hand){
		//Do nothing
	}
	
	@Override
	public boolean playCard(ScopaCard card){
		return true;
	}
	
	@Override
	public boolean isEmpty(){
		return true;
	}
	
	@Override
	public int getNumberCardInHand(){
		return 0;
	}
	
	@Override
	public void addCardsToHeap(List<ScopaCard> cards){
		//Do nothing
	}
	
	@Override
	public void addCardToHeap(ScopaCard card){
		//Do nothing
	}
	
	@Override
	public boolean containsGold7(){
		return false;
	}
	
	@Override
	public int totalCards(){
		return 0;
	}
	
	@Override
	public int totalGold(){
		return 0;
	}
	
	@Override
	public int totalSeven(){
		return 0;
	}
	
	@Override
	public List<ScopaCard> bestCardInAllColor(){
		return Arrays.asList();
	}

}
