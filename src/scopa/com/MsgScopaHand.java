package scopa.com;

import java.util.List;

import scopa.logic.ScopaCard;

public class MsgScopaHand extends MsgScopa {

	private List<ScopaCard> cards;
	
	public MsgScopaHand(List<ScopaCard> cards, String nextPlayer) {
		this(cards,nextPlayer,null,null);
	}

	public MsgScopaHand(List<ScopaCard> cards,  String nextPlayer,
			String senderID, String receiverID) {
		super(ScopaMsgType.hand, nextPlayer, senderID, receiverID);
		this.cards = cards; 
	}
	
	public List<ScopaCard> getCards(){
		return cards;
	}

}
