package scopa.logic;

import java.util.HashMap;
import java.util.Map;

public class ScopaRules {

	public enum Rule {
		scopa, napoli, reverse, team

	}

	private Map<Rule, Boolean> rules;

	public ScopaRules() {
		rules = new HashMap<>(4);
	}

	public ScopaRules(boolean scopa, boolean napoli, boolean reverse, boolean team) {
		this();

		rules.put(Rule.napoli, napoli);
		rules.put(Rule.scopa, scopa);
		rules.put(Rule.reverse, reverse);
		rules.put(Rule.team, team);
	}

	public boolean isRuleScopaEnable() {
		return isRuleEnable(Rule.scopa);
	}

	public boolean isRuleNapoliEnable() {
		return isRuleEnable(Rule.napoli);
	}

	public boolean isRuleReverseEnable() {
		return isRuleEnable(Rule.reverse);
	}

	public boolean isRuleTeamEnable() {
		return isRuleEnable(Rule.team);
	}

	public void setRuleScopa(boolean enable) {
		setRule(Rule.scopa, enable);
	}

	public void setRuleNapoli(boolean enable) {
		setRule(Rule.napoli, enable);
	}

	public void setRuleReverse(boolean enable) {
		setRule(Rule.reverse, enable);
	}

	public void setRuleTeam(boolean enable) {
		setRule(Rule.team, enable);
	}

	private void setRule(Rule rule, boolean enable) {
		rules.put(rule, enable);
	}

	private boolean isRuleEnable(Rule rule) {
		Boolean enable = rules.get(rule);
		if (enable == null) {
			return false;
		} else {
			return enable;
		}
	}

}
