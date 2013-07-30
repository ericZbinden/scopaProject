package scopa.com;

public class MsgScopaAck extends MsgScopa {

	public MsgScopaAck(String nextPlayer) {
		super(ScopaMsgType.ack, nextPlayer);
	}
}
