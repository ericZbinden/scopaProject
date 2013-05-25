package game;

import game.scopa.gui.ScopaRulePanel;
import game.scopa.gui.ZilchRulePanel;
import gui.RulePanel;


public enum GameType {
	SCOPA(new ScopaRulePanel()),	
	ZILCH(new ZilchRulePanel());
	
	private final RulePanel rulePanel;
	
	GameType(RulePanel rulePanel){
		this.rulePanel=rulePanel;
	}
	
	public RulePanel getRulePanel(){
		return rulePanel.clone();
	}

}
