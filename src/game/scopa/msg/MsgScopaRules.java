package game.scopa.msg;

import game.GameType;

import com.msg.MsgMasterRule;

public class MsgScopaRules extends MsgMasterRule {

	private static final long serialVersionUID = 1768682993138644502L;
	private boolean reverse;
	private boolean scopa;
	private boolean napoli;
	
	public MsgScopaRules(boolean reverse,boolean napoli, boolean scopa, String senderID) {
		super(GameType.SCOPA,senderID);
		this.reverse=reverse;
		this.scopa = scopa;
		this.napoli = napoli;
	}
	
	public boolean getReverse(){
		return reverse;
	}
	
	public boolean getNapoli(){
		return napoli;
	}
	
	public boolean getScopa(){
		return scopa;
	}

}
