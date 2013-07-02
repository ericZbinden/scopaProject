package scopa.com;

import java.util.List;

import scopa.logic.ScopaCard;

import game.GameType;

import com.msg.MsgGameBaseConf;

public class MsgScopaBaseConf extends MsgGameBaseConf {

	private List<String> playerOrder;
	
	private List<ScopaCard> hand;
	
	private List<ScopaCard> table;
	
	public MsgScopaBaseConf(List<String> playerOrder, List<ScopaCard> hand, List<ScopaCard> table, String senderID) {
		super(GameType.SCOPA, senderID);
		this.playerOrder=playerOrder;
		this.hand=hand;
		this.table=table;
	}

	public List<String> getPlayerOrder() {
		return playerOrder;
	}

	public List<ScopaCard> getHand() {
		return hand;
	}

	public List<ScopaCard> getTable() {
		return table;
	}

}
