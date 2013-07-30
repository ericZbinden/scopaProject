package scopa.com;

import java.util.List;

import scopa.logic.ScopaCard;

public class MsgBaseConf extends MsgScopa {
			
	private String playerNorth;
	
	private String playerWest;
	
	private String playerEast;
	
	private List<ScopaCard> hand;
	
	private List<ScopaCard> table;
	
	public MsgBaseConf(String playerNorth, String playerWest, String playerEast, List<ScopaCard> hand, List<ScopaCard> table, String nextPlayer) {
		super(ScopaMsgType.baseConf, nextPlayer, null ,null);
		this.playerNorth=playerNorth;
		this.playerWest=playerWest;
		this.playerEast=playerEast;
		this.hand=hand;
		this.table=table;
	}


	public List<ScopaCard> getHand() {
		return hand;
	}

	public List<ScopaCard> getTable() {
		return table;
	}


	public String getPlayerNorth() {
		return playerNorth;
	}


	public String getPlayerWest() {
		return playerWest;
	}


	public String getPlayerEast() {
		return playerEast;
	}


}
