package scopa.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scopa.com.MsgBaseConf;
import scopa.com.MsgScopa;
import scopa.com.MsgScopaAck;
import scopa.com.MsgScopaHand;
import scopa.com.MsgScopaPlay;
import util.Logger;

import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.server.IllegalInitialConditionException;
import com.server.ServerApi;
import com.server.wait.Config;

import game.GameType;
import game.Playable;

public class ScopaGame implements Playable {

	public final static String SRV = "server";
	
	private ServerApi api;
	
	private int nPlayer;
		
	private int lastPlayerToTake;
	
	private ScopaScore score;
	
	private State state;
	
	private ScopaDeck deck;
	private ScopaTable table;
	
	private ScopaHand handP1;	//play with 3 when play in team
	private ScopaHand handP2;	//play with 4 when play in team
	private ScopaHand handP3;
	private ScopaHand handP4;
	
	public ScopaGame(){
		this.state = State.notStarted;		
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

		if(nbTeam == nPlayer){
			//Each player play by itself
			handP1 = new ScopaHand(configs.get(0).getClientID(),configs.get(0).getTeam());
			handP2 = new ScopaHand(configs.get(1).getClientID(),configs.get(1).getTeam());
			
			if(nPlayer > 2){
				handP3 = new ScopaHand(configs.get(2).getClientID(),configs.get(2).getTeam());
			} else {
				handP3 = new EmptyHand();
			}
			
			if(nPlayer > 3){
				handP4 = new ScopaHand(configs.get(3).getClientID(),configs.get(3).getTeam());
			} else {
				handP4 = new EmptyHand();
			}
		} else {
			//Play by team of 2
			Config c = configs.get(0);
			handP1 = new ScopaHand(c.getClientID(),c.getTeam());
			
			c = configs.get(1);
			if(c.getTeam()==handP1.getTeam()){
				handP3 = new ScopaHand(c.getClientID(),c.getTeam());
				
				c = configs.get(2);
				handP2 = new ScopaHand(c.getClientID(),c.getTeam());
				c = configs.get(3);
				handP4 = new ScopaHand(c.getClientID(),c.getTeam());

			} else {
				handP2 = new ScopaHand(c.getClientID(),c.getTeam());
				
				c = configs.get(2);
				if(c.getTeam()==handP1.getTeam()){
					handP3 = new ScopaHand(c.getClientID(),c.getTeam());
					
					c = configs.get(3);
					handP4 = new ScopaHand(c.getClientID(),c.getTeam());
					
				} else {				
					handP4 = new ScopaHand(c.getClientID(),c.getTeam());
					
					c = configs.get(3);
					handP3 = new ScopaHand(c.getClientID(),c.getTeam());
				}
			}
		}
			
		/** rules **/
		//TODO
		
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
		
		state = nextState();
	}
		
	@Override
	public void start() {
		this.newGame();
	}
	

	@Override
	public void setServerApi(ServerApi api) {
		this.api=api;		
	}
	
	public void receivedMsg(MsgPlay msg){
		
		if(msg.getGameType().equals(GameType.SCOPA)){
			MsgScopa msgScopa = (MsgScopa) msg;
			
			switch(msgScopa.getScopaType()){
			case play:
				MsgScopaPlay msgPlay = (MsgScopaPlay) msgScopa;
				if (play(msgPlay.getSenderID(),msgPlay.getPlayed(),msgPlay.getTaken())){
					String nextPlayer = this.getNextPlayer();
					api.sendMsgTo(msgPlay.getSenderID(), new MsgScopaAck(nextPlayer));
					api.sendMsgToAllExcept(msgPlay.getSenderID(), new MsgScopaPlay(msgPlay.getSenderID(),msgPlay.getPlayed(),msgPlay.getTaken(),nextPlayer));
					
					if(nextPlayer.equals(SRV));
				} else {
					//FIXME We have a problem!
				}
			//case ack:
			//case baseConf:
			default:
				Logger.debug("Unknown scopaType: "+msgScopa.getScopaType()+", ignore this message.");
			}
			
			
		} else {
			Logger.debug("Unknown gameType: "+msg.getGameType().getGameType()+", ignore this message.");
		}		
	}
	
	private MsgBaseConf getMsgGameBaseConf(ScopaHand handy) {
		String north ="",west ="",east ="";
		if(nPlayer == 2){
			if(handy.equals(handP1)){
				north = handP2.getPlayer();
			} else {
				north = handP2.getPlayer();
			}
		} else {
			if(handy.equals(handP1)){
				east = handP2.getPlayer();
				north = handP3.getPlayer();
				west = handP4.getPlayer();
			} else if (handy.equals(handP2)){
				east = handP3.getPlayer();
				north = handP4.getPlayer();
				west = handP1.getPlayer();
			} else if (handy.equals(handP3)){
				east = handP4.getPlayer();
				north = handP1.getPlayer();
				west = handP2.getPlayer();
			} else {
				east = handP1.getPlayer();
				north = handP2.getPlayer();
				west = handP3.getPlayer();
			}
		}
			
		return new MsgBaseConf(north,west,east,handy.getHand(),table.cardsOnTable(),handP1.getPlayer());
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
	
	private String getNextPlayer(){
		switch (state){
		case p1:
			return handP1.getPlayer();
		case p2:
			return handP2.getPlayer();
		case p3:
			return handP3.getPlayer();
		case p4:
			return handP4.getPlayer();
		default:
			return SRV;
		}
	}
	
//	public ScopaHand getHandP1(){
//		return handP1;
//	}	
//	
//	public ScopaHand getHandP2(){
//		return handP2;
//	}
//	
//	public ScopaHand getHandP3(){
//		return handP3;
//	}
//	
//	public ScopaHand getHandP4(){
//		return handP4;
//	}
//	
//	public ScopaTable getTable(){
//		return table;
//	}
//	
//	public ScopaScore getScore(){
//		return score;
//	}
	
	
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
		
		handP1.newHand(deck.draw3Cards());
		api.sendMsgTo(handP1.getPlayer(), this.getMsgGameBaseConf(handP1));
		handP2.newHand(deck.draw3Cards());
		api.sendMsgTo(handP2.getPlayer(), this.getMsgGameBaseConf(handP2));

		
		if(nPlayer > 2){
			handP3.newHand(deck.draw3Cards());
			api.sendMsgTo(handP3.getPlayer(), this.getMsgGameBaseConf(handP3));
		}
		
		if(nPlayer > 3){
			handP4.newHand(deck.draw3Cards());
			api.sendMsgTo(handP4.getPlayer(), this.getMsgGameBaseConf(handP4));
		}
		
		state = nextState();
	}
	
//	public void endSet(){
//				
//		state = nextState();
//		
//	}
//	
//	public void endMatch(){
//		
//		state = nextState();
//	}
	
	
	public boolean play(String player, ScopaCard playedCard, List<ScopaCard> takenCards){
		ScopaHand handy =null;
		switch(state){
		case p1:
			handy = handP1;
			if(!player.equals(handP1.getPlayer())) return false;
			if(!handP1.playCard(playedCard)) return false;
			break;
		case p2:
			handy = handP2;
			if(!player.equals(handP2.getPlayer())) return false;
			if(!handP2.playCard(playedCard)) return false;
			break;
		case p3:
			handy = handP3;
			if(!player.equals(handP3.getPlayer())) return false;
			if(!handP3.playCard(playedCard)) return false;
			break;
		case p4:
			handy = handP4;
			if(!player.equals(handP3.getPlayer())) return false;
			if(!handP4.playCard(playedCard)) return false;
			break;
		default: state = State.unknown;
		//FIXME alert srv an error occurs
		}
		if(state!=State.unknown){
			List<ScopaCard> cs = table.putCard(playedCard, takenCards);
			if(cs== null) {
				//put card into hand because wrong play
				handy.getHand().add(playedCard);
				//FIXME be trustfull or quit
			}
			
			handy.addCardsToHeap(cs);

			state = nextState();
			return true;
		} 
		
		return false;
	}
	
	private void takeAll(){
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
		
		state = nextState();
		
		sendHands();
	}
	
	private void sendHands(){
		String nextPlayer = ""; //TODO complete me
		api.sendMsgTo(handP1.getPlayer(), new MsgScopaHand(handP1.getHand(),nextPlayer));
		api.sendMsgTo(handP2.getPlayer(), new MsgScopaHand(handP2.getHand(),nextPlayer));
		
		if(nPlayer>2)
			api.sendMsgTo(handP3.getPlayer(), new MsgScopaHand(handP3.getHand(),nextPlayer));
		if(nPlayer>3)
			api.sendMsgTo(handP4.getPlayer(), new MsgScopaHand(handP4.getHand(),nextPlayer));
	}
	
	/**
	 * STATE MACHINE METHODS
	 */

	private State nextState(){
		switch(state){
		case notStarted:
			return State.readyToStart;
		case readyToStart:
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
			if(handP3.isEmpty()) {
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
