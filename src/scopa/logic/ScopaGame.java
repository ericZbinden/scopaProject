package scopa.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scopa.com.MsgScopaBaseConf;
import util.Logger;

import com.msg.MsgGameBaseConf;
import com.msg.MsgMasterRule;
import com.server.IllegalInitialConditionException;
import com.server.wait.Config;
import com.server.wait.GameStartCondition;

import game.GameType;
import game.Playable;

public class ScopaGame implements Playable {

	private int nPlayer;
	
	private transient List<String> playerOrders;
	
	private int lastPlayerToTake;
	
	private ScopaScore score;
	
	private State state;
	
	private ScopaDeck deck;
	private ScopaTable table;
	
	private ScopaHand handP1;	
	private ScopaHand handP2;
	private ScopaHand handP3;
	private ScopaHand handP4;
	//private ScopaHand handP5;
	//private ScopaHand handP6;
	
	public ScopaGame(){
		this.state = State.notStarted;		
	}
	
	public void initGame(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {
		checkInitialConfig(configs,rules);
			
		nPlayer = configs.size();
			
		//TODO handle team game and rules
		state = nextState();
		this.newGame();
	}
	
	public GameType getGameType(){
		return GameType.SCOPA;
	}
	
	
	public void checkInitialConfig(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {
		
//		if (configs.size()==5 || configs.size() < 2 || configs.size() > 6)		
		if (configs.size() < 2 || configs.size() > 4)	
			throw new IllegalInitialConditionException(
					"Too much or too less player: "+configs.size()+". Need [2,3,4] players");
		
		Map<Integer,Integer> teams = new HashMap<Integer,Integer>(6);
		
		for(int i=1;i<=6;i++){
			teams.put(i, 0);
		}
		for(Config conf : configs){
			int team = conf.getTeam();
			teams.put(team, teams.get(team));		
		}
		int p=0;
		for(Integer players : teams.values()){
			if (p==0){
				p = players;
				continue;
			} else if (p==players){
				continue;
			} else {
				throw new IllegalInitialConditionException(
						"Each team should have same number of players");
			}
		}
		
		//rules don't care in this game
	}
		
	
	public MsgGameBaseConf getMsgGameBaseConf(String playerId) throws IllegalStateException{
		ScopaHand handy = getHandByPlayerId(playerId);
		if(handy != null){
			return new MsgScopaBaseConf(getPlayerOrder(),handy.getHand(),table.cardsOnTable(),handy.getPlayer());
		}
		throw new IllegalStateException("Unable to recover the desired msgBaseConf");
	}
	
	
	private List<String> getPlayerOrder(){
		if(playerOrders == null || playerOrders.isEmpty()){
			//build the list
			playerOrders = new ArrayList<String>(nPlayer);
			playerOrders.add(handP1.getPlayer());
			playerOrders.add(handP2.getPlayer());
			playerOrders.add(handP3.getPlayer());
			playerOrders.add(handP4.getPlayer());	
		}
		
		return playerOrders;		
	}
	
	private ScopaHand getHandByPlayerId(String id){
		if(handP1 != null && handP1.getPlayer().equals(id)){
			return handP1;
		} else if(handP2 != null && handP2.getPlayer().equals(id)){
			return handP2;
		} else if(handP3 != null && handP3.getPlayer().equals(id)){
			return handP3;
		} else if(handP4 != null && handP4.getPlayer().equals(id)){
			return handP4;
		} else {
			Logger.debug("Unable to recover the hand of player "+id+".\n" +
					"Available hands are: ["+handP1.getPlayer()+","+handP2.getPlayer()+
					","+handP3.getPlayer()+","+handP3.getPlayer()+"]");
			return null;
		}
	}
	
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
//	
//	public ScopaHand getHandP5(){
//		return handP5;
//	}
//	
//	public ScopaHand getHandP6(){
//		return handP6;
//	}
	
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
//		case p5:
//			if(playerNumber != 5) return false;
//			if(!handP5.playCard(card)) return false;
//			break;
//		case p6:
//			if(playerNumber != 6) return false;
//			if(!handP6.playCard(card)) return false;
//			break;
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
//				case 5:
//					handP5.getHand().add(card);
//					break;
//				case 6:
//					handP6.getHand().add(card);
//					break;
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
//				case p5:
//					handP5.addCardsToHeap(cs);
//					break;
//				case p6:
//					handP6.addCardsToHeap(cs);
//					break;
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
//		case 5:
//			handP5.addCardsToHeap(table.takeAll());
//			break;
//		case 6:
//			handP6.addCardsToHeap(table.takeAll());
//			break;
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
//		if(nPlayer > 4){
//			handP5.newHand(deck.draw3Cards());
//			handP6.newHand(deck.draw3Cards());
//		}
		state = nextState();
	}
	
	/**
	 * STATE MACHINE METHODS
	 */

	public State nextState(){
		switch(state){
		case notStarted:
			return State.start;
		case start:
			return State.p1;
		case p1:
			return State.p2;
		case p2:
			if(nPlayer > 2) return State.p3;
			else {
				if(handP2.isEmpty()) {
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
				if(handP3.isEmpty()) {
					if(deck.isEmpty())
						return State.takeAll;
					else 
						return State.giveNewHand;
				} else
					return State.p1;
			}
		case p4:
//			if(nPlayer > 4) return State.p5;
//			else {
				if(handP3.isEmpty()) {
					if(deck.isEmpty())
						return State.takeAll;
					else 
						return State.giveNewHand;
				} else
					return State.p1;
//			}
//		case p5:
//			return State.p6;
//		case p6:
//			if(handP6.isEmpty()) {
//				if(deck.isEmpty())
//					return State.takeAll;
//				 else
//					return State.giveNewHand;
//			} else
//				return State.p1;
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
		notStarted,start,p1,p2,p3,p4,/*p5,p6,*/giveNewHand,takeAll,endSet,endMatch,unknown
	}

	
}
