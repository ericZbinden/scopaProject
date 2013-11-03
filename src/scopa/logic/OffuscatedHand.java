package scopa.logic;

import java.util.Arrays;
import java.util.List;

import util.PlayerName;

public class OffuscatedHand extends ScopaHand {
	
	private int nbCards;
	
	public OffuscatedHand(PlayerName playerName, int team){
		super(playerName, team);
		nbCards = 0;
	}
	
	
	public List<ScopaCard> getHand(){
		return Arrays.asList();
	}
	
	public int getNumberCardInHand(){
		return nbCards;
	}
	
	public void newHand(List<ScopaCard> hand){
		nbCards = hand.size();
	}
	
	public boolean playCard(ScopaCard card){
		nbCards --;
		return true;
	}
	
	public boolean isEmpty(){
		return nbCards == 0;
	}
}