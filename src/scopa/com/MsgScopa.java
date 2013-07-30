package scopa.com;

import game.GameType;

import com.msg.MsgPlay;

public abstract class MsgScopa extends MsgPlay {

	private ScopaMsgType scopaType;
	
	private String nextPlayerToPlay;

	protected MsgScopa(ScopaMsgType scopaType, String nextPlayer) {
		this(scopaType,nextPlayer,null,null);
	}

	protected MsgScopa(ScopaMsgType scopaType, String nextPlayer, String senderID, String receiverID) {
		super(GameType.SCOPA, senderID, receiverID);
		this.scopaType=scopaType;
		this.nextPlayerToPlay=nextPlayer;
	}
	
	public ScopaMsgType getScopaType(){
		return scopaType;
	}
	
	
	public String toString(){
		return super.toString()+"\n\tType: "+scopaType+"\tNextPlayerIs: "+nextPlayerToPlay;
	}
	
	public String nextPlayerToPlayIs(){
		return nextPlayerToPlay;
	}

}
