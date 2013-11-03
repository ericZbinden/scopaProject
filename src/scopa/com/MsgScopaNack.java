package scopa.com;

import util.PlayerName;

public class MsgScopaNack extends MsgScopa {

	private static final long serialVersionUID = 2700273500578796894L;

	public MsgScopaNack(PlayerName nextPlayer) {
		super(ScopaMsgType.nack, nextPlayer);
	}

}
