package scopa.logic;

import game.GameType;

import java.util.List;

import com.msg.MsgPlay;

public class ScopaPlayMsg extends MsgPlay {

	private static final long serialVersionUID = -8920860205631914208L;

	private ScopaCard played;
	
	private List<ScopaCard> taken;

	public ScopaPlayMsg(String senderID, ScopaCard played, List<ScopaCard> taken) {
		super(GameType.SCOPA, senderID);
		this.played = played;
		this.taken = taken;
	}
	
	public ScopaCard getPlayed(){
		return played;
	}
	
	public List<ScopaCard> getTaken(){
		return taken;
	}

}
