package scopa.com;

import util.PlayerName;

public class MsgScopaAck extends MsgScopa {

	private static final long serialVersionUID = 4076294795269779313L;

	public MsgScopaAck(PlayerName nextPlayer) {
		super(ScopaMsgType.ack, nextPlayer);
	}
}
