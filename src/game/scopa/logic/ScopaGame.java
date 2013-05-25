package game.scopa.logic;

import java.util.List;

import game.GameType;
import game.Playable;

public class ScopaGame extends Playable {

	private int nPlayer = 2;
	
	private int lastPlayerToTake;
	
	private ScopaScore score;
	
	private State state;
	
	private ScopaDeck deck;
	private ScopaTable table;
	
	private ScopaHand handP1;	
	private ScopaHand handP2;
	private ScopaHand handP3;
	private ScopaHand handP4;
	private ScopaHand handP5;
	private ScopaHand handP6;
	
	public ScopaGame(int nPlayer){
		if(nPlayer < 2 || nPlayer > 6 || nPlayer == 5) throw new RuntimeException("Too much or too less player: "+nPlayer+". Need [2,3,4 or 6] players");
		
		this.type = GameType.SCOPA;
		this.nPlayer = nPlayer;
		this.score = new ScopaScore(nPlayer);
		
		this.state = State.start;
		
	}

	
	//FIXME p2 should not see other hand
	
	public ScopaHand getHandP1(){
		return handP1;
	}	
	
	public ScopaHand getHandP2(){
		return handP2;
	}
	
	public ScopaHand getHandP3(){
		return handP3;
	}
	
	public ScopaHand getHandP4(){
		return handP4;
	}
	
	public ScopaHand getHandP5(){
		return handP5;
	}
	
	public ScopaHand getHandP6(){
		return handP6;
	}
	
	public ScopaTable getTable(){
		return table;
	}
	
	public ScopaScore getScore(){
		return score;
	}
	
	
	/**
	 * ACTION METHODS
	 */
	
	
	public void newGame(){
		
		boolean ok = false;
		do {
			deck = ScopaDeckCards.newDeck();
			deck.shuffle();
			ok = table.putInitial(deck.drawInitialCards());

		}while(!ok);
		state = nextState();
	}
	
	public void endSet(){
		
		
		state = nextState();
		
	}
	
	public void endMatch(){
		
		state = nextState();
	}
	
	
	public boolean play(int playerNumber, ScopaCard card, List<ScopaCard> cards){
		switch(state){
		case p1:
			if(playerNumber != 1) return false;
			if(!handP1.playCard(card)) return false;
			break;
		case p2:
			if(playerNumber != 2) return false;
			if(!handP2.playCard(card)) return false;
			break;
		case p3:
			if(playerNumber != 3) return false;
			if(!handP3.playCard(card)) return false;
			break;
		case p4:
			if(playerNumber != 4) return false;
			if(!handP4.playCard(card)) return false;
			break;
		case p5:
			if(playerNumber != 5) return false;
			if(!handP5.playCard(card)) return false;
			break;
		case p6:
			if(playerNumber != 6) return false;
			if(!handP6.playCard(card)) return false;
			break;
		default: state = State.unknown;
		}
		if(state!=State.unknown){
			List<ScopaCard> cs = table.putCard(card, cards);
			if(cs== null) {
				//FIXME too ugly to stay like this
				//put card into hand because wrong play
				switch(playerNumber){
				case 1:
					handP1.getHand().add(card);
					break;
				case 2:
					handP2.getHand().add(card);
					break;
				case 3:
					handP3.getHand().add(card);
					break;
				case 4:
					handP4.getHand().add(card);
					break;
				case 5:
					handP5.getHand().add(card);
					break;
				case 6:
					handP6.getHand().add(card);
					break;
				default:
				}

			} else {
				if(cs.size()>0) this.lastPlayerToTake = playerNumber;
				switch(state){
				case p1:
					handP1.addCardsToHeap(cs);
					break;
				case p2:
					handP2.addCardsToHeap(cs);
					break;
				case p3:
					handP3.addCardsToHeap(cs);
					break;
				case p4:
					handP4.addCardsToHeap(cs);
					break;
				case p5:
					handP5.addCardsToHeap(cs);
					break;
				case p6:
					handP6.addCardsToHeap(cs);
					break;
				default:
				}
				state = nextState();
				return true;
			}

		} 
		return false;
	}
	
	public void takeAll(){
		switch(lastPlayerToTake){
		case 1:
			handP1.addCardsToHeap(table.takeAll());
			break;
		case 2:
			handP2.addCardsToHeap(table.takeAll());
			break;
		case 3:
			handP3.addCardsToHeap(table.takeAll());
			break;
		case 4:
			handP4.addCardsToHeap(table.takeAll());
			break;
		case 5:
			handP5.addCardsToHeap(table.takeAll());
			break;
		case 6:
			handP6.addCardsToHeap(table.takeAll());
			break;
		default:
			state = State.unknown;
		}
		state = nextState();
	}
		
	private void giveNewHands(){
		handP1.newHand(deck.draw3Cards());
		handP2.newHand(deck.draw3Cards());
		if(nPlayer > 2)
			handP3.newHand(deck.draw3Cards());
		if(nPlayer > 3)
			handP4.newHand(deck.draw3Cards());
		if(nPlayer > 4){
			handP5.newHand(deck.draw3Cards());
			handP6.newHand(deck.draw3Cards());
		}
		state = nextState();
	}
	
	/**
	 * STATE MACHINE METHODS
	 */

	public State nextState(){
		switch(state){
		case start:
			return State.p1;
		case p1:
			return State.p2;
		case p2:
			if(nPlayer > 2) return State.p3;
			else {
				if(handP2.emptyHand()) {
					if(deck.isEmpty())
						return State.takeAll;
					else 
						return State.giveNewHand;
				} else
					return State.p1;
			}
		case p3:
			if(nPlayer > 3) return State.p4;
			else {
				if(handP3.emptyHand()) {
					if(deck.isEmpty())
						return State.takeAll;
					else 
						return State.giveNewHand;
				} else
					return State.p1;
			}
		case p4:
			if(nPlayer > 4) return State.p5;
			else {
				if(handP3.emptyHand()) {
					if(deck.isEmpty())
						return State.takeAll;
					else 
						return State.giveNewHand;
				} else
					return State.p1;
			}
		case p5:
			return State.p6;
		case p6:
			if(handP6.emptyHand()) {
				if(deck.isEmpty())
					return State.takeAll;
				 else
					return State.giveNewHand;
			} else
				return State.p1;
		case giveNewHand: 
			return State.p1;
		case takeAll:
			return State.endSet;
		case endSet:
			if(score.checkWinner()) return State.endMatch;
			else return State.start;
		case endMatch:
			return State.endMatch; //TODO might be different
		default:
			return State.unknown;
		}	
	}
	
	private enum State {
		start,p1,p2,p3,p4,p5,p6,giveNewHand,takeAll,endSet,endMatch,unknown
	}
	
}
