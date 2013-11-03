package scopa.com;

import java.util.List;

import scopa.logic.ScopaCard;
import util.PlayerName;

public class MsgScopaHand extends MsgScopa {

	private static final long serialVersionUID = -2577710923199177410L;
	
	private List<ScopaCard> cards;
	
	public MsgScopaHand(List<ScopaCard> cards, PlayerName nextPlayer) {
		this(cards,nextPlayer,null,null);
	}

	public MsgScopaHand(List<ScopaCard> cards,  PlayerName nextPlayer,
			PlayerName senderID, PlayerName receiverID) {
		super(ScopaMsgType.hand, nextPlayer, senderID, receiverID);
		this.cards = cards; 
	}
	
	public List<ScopaCard> getCards(){
		return cards;
	}

}
