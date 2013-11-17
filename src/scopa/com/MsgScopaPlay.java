package scopa.com;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import scopa.logic.ScopaCard;
import util.PlayerName;

public class MsgScopaPlay extends MsgScopa {

	private static final long serialVersionUID = -8920860205631914208L;

	private ScopaCard played;
	
	private List<ScopaCard> taken;
	
	public MsgScopaPlay(PlayerName senderID, ScopaCard played, List<ScopaCard> taken, PlayerName nextPlayer) {
		super(ScopaMsgType.play, nextPlayer, senderID,null);
		this.played = played;
		this.taken = taken;
	}
	
	public ScopaCard getPlayed(){
		return played;
	}
	
	public List<ScopaCard> getTaken(){
		return taken;
	}
	
	
	@Override
	public String toString(){
		return super.toString()+"\n\tplayed: "+played+"\n\ttaken: "+StringUtils.join(taken.toArray());
	}


}
