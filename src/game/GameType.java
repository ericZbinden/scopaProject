package game;

import scopa.gui.ScopaRulePanel;
import scopa.gui.ZilchRulePanel;
import scopa.logic.ScopaGame;
import gui.RulePanel;


public enum GameType {
	SCOPA(new ScopaRulePanel(),new ScopaGame()),	
	ZILCH(new ZilchRulePanel(),null);
	
	private final RulePanel rulePanel;
	
	private final Playable game;
	
	GameType(RulePanel rulePanel, Playable game){
		this.rulePanel=rulePanel;
		this.game=game;
	}
	
	public RulePanel getRulePanel(){
		return rulePanel.clone();
	}
	
	public Playable getGame(){
		return game;
	}
	
	public String getGameType(){
		return toString();
	}

}
