package game.scopa.logic;

import java.util.ArrayList;
import java.util.List;

public class ScopaHand {
	
	private List<ScopaCard> heap;
	
	private List<ScopaCard> hand;
	
	public ScopaHand(){
		this.heap = new ArrayList<ScopaCard>();
		this.hand = new ArrayList<ScopaCard>(3);
	}
	
	public List<ScopaCard> getHand(){
		return hand;
	}
	
	public void newHand(List<ScopaCard> hand){
		this.hand = hand;
	}
	
	/*public List<ScopaCard> getHeap(){
		return heap;
	}*/
	
	/*public ScopaCard playCard(int index){
		return hand.remove(index);
	}*/
	
	public boolean playCard(ScopaCard card){
		if(hand.contains(card)){
			hand.remove(card);
			return true;
		} else return false;
	}
	
	public boolean emptyHand(){
		return hand.isEmpty();
	}
	
	public void addCardsToHeap(List<ScopaCard> cards){
		heap.addAll(cards);
	}
	
	
	public void addCardToHeap(ScopaCard card){
		heap.add(card);
	}
	
	public boolean containsGold7(){
		return heap.contains(new ScopaCard(ScopaValue.seven,ScopaColor.gold));
	}
	
	public int totalCards(){
		return heap.size();
	}
	
	public int totalGold(){
		int gold = 0;
		for(ScopaCard card : heap){
			if(card.getColor()==ScopaColor.gold)
				gold++;
		}
		return gold;
	}
	
	public int totalSeven(){
		int seven = 0;
		for(ScopaCard card : heap){
			if(card.getValue()==ScopaValue.seven)
				seven++;
		}
		return seven;
	}
	
	public List<ScopaCard> bestCardInAllColor(){
		ScopaCard gold = null;
		ScopaCard stick = null;
		ScopaCard sword = null;
		ScopaCard cup = null;
		
		for(ScopaCard card : heap){
			switch(card.getColor()){
			case gold:
				if(gold==null) gold = card;
				if(card.isHigherFor7(gold)) gold = card;
				break;
			case stick:
				if(stick==null) stick = card;
				if(card.isHigherFor7(stick)) stick = card;
				break;
			case sword:
				if(sword==null) sword = card;
				if(card.isHigherFor7(sword)) sword = card;
				break;
			case cup:
				if(cup==null) cup = card;
				if(card.isHigherFor7(cup)) cup = card;
				break;
			default:
				throw new RuntimeException("Unknown color of card: "+card.getColor());
			}
		}
		
		List<ScopaCard> cards = new ArrayList<ScopaCard>();
		if(cup!= null) 	  cards.add(cup);
		if(gold != null)  cards.add(gold);
		if(sword != null) cards.add(sword);
		if(stick != null) cards.add(stick);
		
		return cards;		
	}
	
	

}
