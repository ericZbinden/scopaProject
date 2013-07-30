package game;

import scopa.gui.ScopaGamePanel;
import scopa.gui.ScopaRulePanel;
import scopa.gui.ZilchRulePanel;
import scopa.logic.ScopaGame;
import gui.GamePanel;
import gui.RulePanel;


public enum GameType {
	SCOPA(new ScopaRulePanel(),new ScopaGame(),new ScopaGamePanel()),	
	ZILCH(new ZilchRulePanel(),null,null);
	
	private final RulePanel rulePanel;
	
	private final Playable game;
	
	private final GamePanel gamePanel;
	
	GameType(RulePanel rulePanel, Playable game, GamePanel gamePanel){
		this.rulePanel=rulePanel;
		this.game=game;
		this.gamePanel=gamePanel;
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
	
	public GamePanel getGamePanel(){
		return gamePanel.clone();
	}

}
