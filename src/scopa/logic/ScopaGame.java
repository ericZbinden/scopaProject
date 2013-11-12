package scopa.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scopa.com.MalformedScopaMessageException;
import scopa.com.MsgBaseConf;
import scopa.com.MsgScopa;
import scopa.com.MsgScopaAck;
import scopa.com.MsgScopaHand;
import scopa.com.MsgScopaNack;
import scopa.com.MsgScopaPlay;
import scopa.com.MsgScopaRules;
import scopa.com.MsgScopaScore;
import scopa.com.ScopaMsgType;
import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.msg.MsgType;
import com.server.IllegalInitialConditionException;
import com.server.Server;
import com.server.ServerApi;
import com.server.wait.Config;

import game.GameType;
import game.Playable;

public class ScopaGame implements Playable {

	public final static PlayerName SRV_NAME = Server.SERVER_NAME;
	
	private ServerApi api;
	
	private int nPlayer;
		
	private PlayerName lastPlayerToTake;
	private State donour;
	
	private ScopaScore score;
	
	private State state;
	
	private ScopaDeck deck;
	private ScopaTable table;	
	private Map<PlayerName,ScopaHand> hands; //PLAYERS - HAND
	private Map<State, PlayerName> players; //Player number - Players name
	private Map<ScopaRule,Boolean> rules;
	
	public ScopaGame(){
		this.state = State.notStarted;		
		hands = new HashMap<>(4);
		rules = new HashMap<ScopaRule,Boolean>();
		players = new HashMap<>(4);
	}
	
	@Override
	public void initGame(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {
		/** check condition **/
		checkInitialConfig(configs,rules);
					
		/** players **/
		nPlayer = configs.size();
		
		/** team repartition **/
		Set<Integer> teams = new HashSet<>(configs.size());		
		for(Config c: configs){
			teams.add(c.getTeam());
		}		
		int nbTeam = teams.size();

		//to obtain a random n°1 player to start with
		Collections.shuffle(configs);
		
		//Create all scopa hand
		Config conf = configs.get(0);
		ScopaHand hand1 =  new ScopaHand(conf.getClientID(),conf.getTeam());
		donour = State.p1;
		conf = configs.get(1);
		ScopaHand hand2 =  new ScopaHand(conf.getClientID(),conf.getTeam());
		ScopaHand hand3 = null;
		ScopaHand hand4 = null;
		if(nPlayer > 2){
			conf = configs.get(2);
			hand3 =  new ScopaHand(conf.getClientID(),conf.getTeam());
		}
		if(nPlayer > 3){
			conf = configs.get(3);
			hand4 =  new ScopaHand(conf.getClientID(),conf.getTeam());
		}

		if(nbTeam == nPlayer){
			//Each player play by itself
			hands.put(hand1.getPlayer(),hand1);
			players.put(State.p1, hand1.getPlayer());
			
			hands.put(hand2.getPlayer(),hand2);
			players.put(State.p2, hand2.getPlayer());
			
			if(nPlayer > 2){
				hands.put(hand3.getPlayer(),hand3);
				players.put(State.p3, hand3.getPlayer());
			}
			
			if(nPlayer > 3){
				hands.put(hand4.getPlayer(),hand4);
				players.put(State.p4, hand4.getPlayer());
			}
			
		} else {
			//Play by team of 2
			//Teams are: (P1,P3) and (P2,P4)
			hands.put(hand1.getPlayer(),hand1);
			players.put(State.p1, hand1.getPlayer());
					
			conf = configs.get(1);		
			if(hand1.getTeam()==hand2.getTeam()){ //is in the same team than p1
				//YES : set as p3
				hands.put(hand2.getPlayer(),hand2);
				players.put(State.p3, hand2.getPlayer());

				//the two next player are teaming
				hands.put(hand3.getPlayer(),hand3);
				players.put(State.p2, hand3.getPlayer());
				hands.put(hand4.getPlayer(),hand4);
				players.put(State.p4, hand4.getPlayer());

			} else {
				//NO : set as p2
				hands.put(hand2.getPlayer(),hand2);
				players.put(State.p2, hand2.getPlayer());
				
				conf = configs.get(2);
				if(hand3.getTeam()==hand1.getTeam()){ // is in the same team than p1
					//YES : set as p3
					hands.put(hand3.getPlayer(),hand3);
					players.put(State.p3, hand3.getPlayer());
					
					//Last player set as p4 with p2
					hands.put(hand4.getPlayer(),hand4);
					players.put(State.p4, hand4.getPlayer());
					
				} else {			
					//NO : set as p4
					hands.put(hand3.getPlayer(),hand3);
					players.put(State.p4, hand3.getPlayer());
					
					//Last player set as p3 with p1
					hands.put(hand4.getPlayer(),hand4);
					players.put(State.p3, hand4.getPlayer());
				}
			}
		}
			
		/** rules **/
		MsgScopaRules msg = (MsgScopaRules) rules;
		this.rules.put(ScopaRule.napoli, msg.getNapoli());
		this.rules.put(ScopaRule.reverse, msg.getReverse());
		this.rules.put(ScopaRule.scopa, msg.getScopa());
		
		/** ready to start **/
		state = nextState();
	}

	@Override
	public GameType getGameType(){
		return GameType.SCOPA;
	}
	
	@Override	
	public void checkInitialConfig(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {
				
		if (configs.size() < 2 || configs.size() > 4)	
			throw new IllegalInitialConditionException(
					"Too much or too less player: "+configs.size()+". Need [2,3,4] players");
		
		Map<Integer,Integer> teams = new HashMap<Integer,Integer>(configs.size());
		
		for(Config conf : configs){
			int team = conf.getTeam();
			Integer teamMate = teams.get(team);
			
			if(teamMate == null){
				teams.put(team, 1);
			} else {
				teams.put(team, teamMate+1);
			}
		}
		
		if(teams.size()<2){
			throw new IllegalInitialConditionException(
					"Need at least 2 teams");
		}
		
		int p=0;
		for(Entry<Integer,Integer> players : teams.entrySet()){
			Logger.debug("Team "+players.getKey()+" haz "+players.getValue()+" players");
			if (p==0){
				p = players.getValue();
				continue;
			} else if (p==players.getValue()){
				continue;
			} else {
				throw new IllegalInitialConditionException(
						"Each team should have same number of players");
			}
		}
		
		if(!(rules instanceof MsgScopaRules)){
			throw new IllegalInitialConditionException("The msgRules should be MsgScopaRules type but was: "+rules.getClass());
		}
		
		state = nextState();
	}
		
	@Override
	public void start(ServerApi api) {
		this.api=api;		
		this.newGame();
	}
	
	private MsgBaseConf getMsgGameBaseConf(ScopaHand handy) {
		PlayerName currentPlayer = handy.getPlayer();
		PlayerName north = null,west = null,east =null;
		if(nPlayer == 2){
			if(currentPlayer.equals(players.get(State.p1))){
				north = players.get(State.p2);
			} else {
				north = players.get(State.p1);
			}
		} else {
			if(currentPlayer.equals(players.get(State.p1))){
				east = players.get(State.p2);
				north = players.get(State.p3);
				west = players.get(State.p4);
			} else if (currentPlayer.equals(players.get(State.p2))){
				east = players.get(State.p3);
				north = players.get(State.p4);
				west = players.get(State.p1);
			} else if (currentPlayer.equals(players.get(State.p3))){
				east = players.get(State.p4);
				north = players.get(State.p1);
				west = players.get(State.p2);
			} else {
				east = players.get(State.p1);
				north = players.get(State.p2);
				west = players.get(State.p3);
			}
		}

		return new MsgBaseConf(north,west,east,handy.getHand(),table.cardsOnTable(),this.getNextPlayer());
	}
	
	private ScopaHand getHandByPlayerName(PlayerName name){
		ScopaHand hand = hands.get(name);
		if(hand != null)
			return hand;
		
		Logger.debug("Unable to recover the hand of player "+name+".\n" +
					"Available hands are: "+Arrays.toString(hands.keySet().toArray()));
		return null;
		
	}
	
	private ScopaHand getHandByPlayerState(State playerId){
		PlayerName playerName = players.get(playerId);
		if(playerName != null){
			return getHandByPlayerName(playerName);
		} 
		Logger.debug("Unable to recover the hand of player id "+playerId+".\n" +
					"Available hands are: "+Arrays.toString(players.keySet().toArray()));
		return null;
	}
	
	private PlayerName getNextPlayer(){
		switch (state){
		case p1:
		case p2:
		case p3:
		case p4:
			return players.get(state);
		default:
			return SRV_NAME;
		}
	}
	
	/**
	 * ACTION METHODS
	 */
	
	/** Create a new table, a new deck and distribute first hand*/
	private void newGame(){
		
		table = ScopaFactory.getNewScopaTable();
		boolean ok = false;
		do {
			deck = ScopaFactory.getNewScopaDeck();
			deck.shuffle();
			ok = table.putInitial(deck.drawInitialCards());

		}while(!ok);
		
		for(ScopaHand hand : hands.values()){
			assert hand.isEmpty() : "Hand is not empty ";
			hand.newHand(deck.draw3Cards());
			api.sendMsgTo(hand.getPlayer(), this.getMsgGameBaseConf(hand));
		}
		
		state = nextState();
	}
	

	@Override
	public void receiveMsgPlay(MsgPlay msg) throws MalformedMessageException{
		if(!this.getGameType().equals(msg.getGameType()) || !(msg instanceof MsgScopa)){					
			throw new MalformedMessageException();	
		}
		
		MsgScopa msgScopa = (MsgScopa) msg;
		
		ScopaMsgType scopaType = msgScopa.getScopaType();
			
			switch(msgScopa.getScopaType()){
			case play:
				if (!(msgScopa instanceof MsgScopaPlay)){
					throw new MalformedScopaMessageException(scopaType);
				}
				
				MsgScopaPlay msgPlay = (MsgScopaPlay) msgScopa;
				if (play(msgPlay.getSenderID(),msgPlay.getPlayed(),msgPlay.getTaken())){
					PlayerName nextPlayer = this.getNextPlayer();
					api.sendMsgTo(msgPlay.getSenderID(), new MsgScopaAck(nextPlayer));
					api.sendMsgToAllExcept(msgPlay.getSenderID(), new MsgScopaPlay(msgPlay.getSenderID(),msgPlay.getPlayed(),msgPlay.getTaken(),nextPlayer));
					
					if(nextPlayer.equals(SRV_NAME)){
						switch(state){									
						case takeAll:
							takeAll();
						case endSet:
							if(sendScore()){
								break; //winner, we don't do a new set
							} 
						case giveNewHand:
							giveNewHands();
							break;	
						default:
							Logger.error("SRV should play but do nothing");
							this.closeGameDueToError("Invalid state "+state+", SRV should rather do something");
						}
					}
				} else if (state.equals(State.unknown)){
					this.closeGameDueToError("Unauthorized play: "+msgPlay.toString()+
							"\nwhile on table: "+Arrays.toString(table.cardsOnTable().toArray()));
				} else {
					Logger.debug("Player "+msgPlay.getSenderID()+" tried to play at "+this.getNextPlayer()+" turn. Ignoring this msg");
					api.sendMsgTo(msgPlay.getSenderID(), new MsgScopaNack(this.getNextPlayer()));
				}
				break;
			default:
				Logger.debug("Invalid scopaType: "+msgScopa.getScopaType());
				throw new MalformedMessageException();	
				//api.sendMsgTo(msgPlay.getSenderID(), new MsgScopaNack(this.getNextPlayer())); ??
			}	
	}
	
	private boolean play(PlayerName player, ScopaCard playedCard, List<ScopaCard> takenCards){

		ScopaHand hand = null;
		switch(state){
		case p1:
		case p2:
		case p3:
		case p4:
			hand = this.getHandByPlayerState(state);
			    //Not good player                   //Selected card not in hand
			if (!player.equals(hand.getPlayer()) || !!hand.playCard(playedCard)){
				return false;
			}
			break;
		default: 
			closeGameDueToError("Player "+player.getName()+" played but at "+getNextPlayer().getName()+" to play");
			return false;
		}
		
		if(state!=State.unknown){
			List<ScopaCard> cs = table.putCard(playedCard, takenCards);
			if(cs== null) {
				closeGameDueToError("Invalid move: "+playedCard+" taking: "+Arrays.toString(takenCards.toArray())+
						"\nwith table containing: "+Arrays.toString(table.cardsOnTable().toArray()));
				return false;
			} else if(!cs.isEmpty()){
				hand.addCardsToHeap(cs);
				lastPlayerToTake = player;
			}
					
			state = nextState();
			return true;
		} 
		
		//should not goes there but anyway...
		closeGameDueToError("Unknown state");
		return false; 
	}
	
	private void takeAll(){
		
		ScopaHand hand = this.getHandByPlayerName(lastPlayerToTake);
		if (hand == null){
			closeGameDueToError("Unknown player "+lastPlayerToTake+", unable to take all cards.");
			return;
		}
		
		hand.addCardsToHeap(table.takeAll());
		state = nextState();
		
	}
		
	private void giveNewHands(){
		for(ScopaHand hand : hands.values()){
			hand.newHand(deck.draw3Cards());
		}
		
		state = nextState();		
		sendHands();
	}
	
	private void sendHands(){
		PlayerName nextPlayer = getNextPlayer();
		
		for(ScopaHand hand : hands.values()){
			api.sendMsgTo(hand.getPlayer(), new MsgScopaHand(hand.getHand(),nextPlayer));
		}
	}
	
	private boolean sendScore(){
		api.sendMsgToAll(new MsgScopaScore(score));
		return score.checkWinner();
	}
	
	private void closeGameDueToError(String reason){
		state = State.unknown;
		Logger.error("Update state to unknown. Reason: "+reason);
		//TODO warn players and close the game
	}
		
	/**
	 * STATE MACHINE METHODS
	 */

	private State nextState(){
		switch(state){
		case notStarted:
			return State.readyToStart;
		case readyToStart:
			return donour;
		case p1:
		case p2:
		case p3:
		case p4:
			//FIXME state machine completely wrong
// Previous logic Kept for idea
//		case p4:
//			if(this.getHandByPlayerState(state).isEmpty()) {
//				if(deck.isEmpty())
//					return State.takeAll;
//				else 
//					return State.giveNewHand;
//			} else
//				return State.p1;

		case giveNewHand: 
			return donour;
		case takeAll:
			return State.endSet;
		case endSet:
			if(score.checkWinner()) return State.endMatch;
			else return State.readyToStart;
		case endMatch:
			return State.endMatch; //TODO might be different
		default:
			return State.unknown;
		}	
	}
	
	private enum State {
		notStarted,readyToStart,p1,p2,p3,p4,giveNewHand,takeAll,endSet,endMatch,unknown
	}

	
}
