package scopa.com;

import game.GameType;
import scopa.logic.ScopaRules;
import util.PlayerName;

import com.msg.MsgMasterRule;

public class MsgScopaRules extends MsgMasterRule {

	private static final long serialVersionUID = 1768682993138644502L;
	private boolean reverse;
	private boolean scopa;
	private boolean napoli;
	private boolean team;

	public MsgScopaRules(boolean reverse, boolean napoli, boolean scopa, boolean team, PlayerName senderID) {
		super(GameType.SCOPA, senderID);
		this.reverse = reverse;
		this.scopa = scopa;
		this.napoli = napoli;
		this.team = team;
	}

	public MsgScopaRules(ScopaRules rules, PlayerName senderID) {
		this(rules.isRuleReverseEnable(), rules.isRuleNapoliEnable(), rules.isRuleScopaEnable(), rules.isRuleTeamEnable(), senderID);
	}

	public ScopaRules getRules() {
		ScopaRules rules = new ScopaRules();
		rules.setRuleNapoli(napoli);
		rules.setRuleScopa(scopa);
		rules.setRuleReverse(reverse);
		rules.setRuleTeam(team);
		return rules;
	}

}
