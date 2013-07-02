package scopa.logic;

import java.util.ArrayList;
import java.util.List;

public class ScopaTableCards extends ArrayList<ScopaCard> implements ScopaTable {

	private static final long serialVersionUID = 6791600481344640732L;

	private ScopaTableCards(){
		
	}
	
	public static ScopaTable newTable(){
		return new ScopaTableCards();
	}

	@Override
	public List<ScopaCard> putCard(ScopaCard card) {
		List<ArrayList<ScopaCard>> takes = allPossibleTakeWith(card);
		if(takes.size()==0){
			this.add(card);
			return new ArrayList<ScopaCard>();
		}
		else if (takes.size()==1){
			ArrayList<ScopaCard> t = takes.get(0);
			this.removeAll(t);
			t.add(card);
			return t;

		}
		else return null;
	}
	
	public List<ScopaCard> putCard(ScopaCard card, List<ScopaCard> cards){
		List<ArrayList<ScopaCard>> takes = allPossibleTakeWith(card);

		if(takes.size()==0 && cards.isEmpty()){
			this.add(card);
			return new ArrayList<ScopaCard>();
		}
		
		for(ArrayList<ScopaCard> t : takes){
			if (t.containsAll(cards) && t.size()==cards.size()){
				this.removeAll(t);
				t.add(card);
				return t;
			}
		}
		return null;
	}
	
	public boolean putInitial(List<ScopaCard> cards){
		int value =0;
		int king = 0;
		for(ScopaCard c : cards){
			value += ScopaValue.val(c);
			if(c.getValue()==ScopaValue.king)
				king++;
		}
		if(value <= 10) return false;
		else if(king > 2) return false;
		else {
			this.addAll(cards);
			return true;
		}
		
	}

	@Override
	public List<ScopaCard> takeAll() {
		List<ScopaCard> cards = new ArrayList<ScopaCard>(this);
		this.clear();
		return cards;
	}
	
	@Override
	public boolean isEmpty(){
		return super.isEmpty();
	}

	@Override
	public List<ArrayList<ScopaCard>> allPossibleTakeWith(ScopaCard card) {
		
		List<ArrayList<ScopaCard>> constructPossible = new ArrayList<ArrayList<ScopaCard>>();
		boolean onlyOne = containsSameValueThan(card);
		
		for(ScopaCard c : this){
			if(c.isEqual(card)){
				ArrayList<ScopaCard> one = new ArrayList<ScopaCard>();
				one.add(card);
				constructPossible.add(one);
			}
			else if (c.isSmaller(card) && !onlyOne){
				ArrayList<ScopaCard> one = new ArrayList<ScopaCard>();
				one.add(card);
				for(ArrayList<ScopaCard> cards : constructPossible){
					int sum = 0;
					for(ScopaCard c2 : cards){
						sum+= ScopaValue.val(c2);
					}
					if (sum == ScopaValue.val(card) || sum < ScopaValue.val(card)){
						cards.add(card);
					}
				}	
				constructPossible.add(one);
			}
		}
		
		List<ArrayList<ScopaCard>> possible = new ArrayList<ArrayList<ScopaCard>>();
		for(ArrayList<ScopaCard> cards : constructPossible){
			int sum = 0;
			for(ScopaCard c2 : cards){
				sum+= ScopaValue.val(c2);
			}
			if(sum==ScopaValue.val(card)) possible.add(cards);
		}
		
		
		return possible;
	}
	
	private boolean containsSameValueThan(ScopaCard card){
		for(ScopaCard c: this){
			if(c.getValue()==card.getValue()) return true;
		}
		return false;
	}

	@Override
	public List<ScopaCard> cardsOnTable() {
		return this;
	}
	
}
