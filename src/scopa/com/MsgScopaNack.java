package scopa.com;

import util.PlayerName;

public class MsgScopaNack extends MsgScopa {

	private static final long serialVersionUID = 2700273500578796894L;

	private String reason;

	public MsgScopaNack(PlayerName nextPlayer, String reason) {
		super(ScopaMsgType.nack, nextPlayer);
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

}
