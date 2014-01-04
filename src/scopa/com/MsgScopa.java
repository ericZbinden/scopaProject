package scopa.com;

import game.GameType;
import util.PlayerName;

import com.msg.MsgPlay;

public abstract class MsgScopa extends MsgPlay {

	private static final long serialVersionUID = 1L;

	private ScopaMsgType scopaType;

	private String nextPlayerToPlay;

	protected MsgScopa(ScopaMsgType scopaType, PlayerName nextPlayer) {
		this(scopaType, nextPlayer, null, null);
	}

	protected MsgScopa(ScopaMsgType scopaType, PlayerName nextPlayer, PlayerName senderID, PlayerName receiverID) {
		super(GameType.SCOPA, senderID, receiverID);
		this.scopaType = scopaType;
		if (nextPlayer != null) {
			this.nextPlayerToPlay = nextPlayer.getName();
		} else {
			this.nextPlayerToPlay = null;
		}
	}

	public ScopaMsgType getScopaType() {
		return scopaType;
	}

	@Override
	public String toString() {
		return super.toString() + "\n\tScopaType: " + scopaType + "\tNextPlayerIs: " + nextPlayerToPlay;
	}

	public PlayerName nextPlayerToPlayIs() {
		if (nextPlayerToPlay == null)
			return null;

		return new PlayerName(nextPlayerToPlay);
	}

}
