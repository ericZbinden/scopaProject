package scopa.com;

import util.PlayerName;
import game.GameType;

import com.msg.MsgPlay;

public abstract class MsgScopa extends MsgPlay {

	private static final long serialVersionUID = 1L;

	private ScopaMsgType scopaType;
	
	private String nextPlayerToPlay;

	protected MsgScopa(ScopaMsgType scopaType, PlayerName nextPlayer) {
		this(scopaType,nextPlayer,null,null);
	}

	protected MsgScopa(ScopaMsgType scopaType, PlayerName nextPlayer, PlayerName senderID, PlayerName receiverID) {
		super(GameType.SCOPA, senderID, receiverID);
		this.scopaType=scopaType;
		this.nextPlayerToPlay=nextPlayer.getName();
	}
	
	public ScopaMsgType getScopaType(){
		return scopaType;
	}
	
	
	@Override
	public String toString(){
		return super.toString()+"\n\tType: "+scopaType+"\tNextPlayerIs: "+nextPlayerToPlay;
	}
	
	public PlayerName nextPlayerToPlayIs(){
		return new PlayerName(nextPlayerToPlay);
	}

}
