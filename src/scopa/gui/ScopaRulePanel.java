package scopa.gui;

import game.GameType;
import gui.RulePanel;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import scopa.com.MsgScopaRules;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgMasterRule;

public class ScopaRulePanel extends RulePanel implements ActionListener {
	
	protected JCheckBox scopa = new JCheckBox("Scopa");
	private JCheckBox reverse = new JCheckBox("Reverse");
	private JCheckBox napoli = new JCheckBox("Napoli");
	
	public ScopaRulePanel() {
		//size = 290,300
		this.setLayout(new GridLayout(3,1));
		this.add(scopa);
		this.add(reverse);
		this.add(napoli);
		
		scopa.setToolTipText("Marque un point lors d'une scopa.");
		reverse.setToolTipText("Le but de la partie est de faire le moins de points possible.");
		napoli.setToolTipText("<html>A la fin de la partie, avoir l'as, le deux et le trois d'or rapporte un point.<br/> Ainsi qu'ûn point supplémentaire pout chaque or consécutif.</html>");
		scopa.addActionListener(this);
		reverse.addActionListener(this);
		napoli.addActionListener(this);
	}
	
	private ScopaRulePanel(boolean canEdit){
		this();
		this.setEdit(canEdit);
	}
	
	public void setEdit(boolean canEdit){
		super.setEdit(canEdit);
		
		scopa.setEnabled(canEdit);
		reverse.setEnabled(canEdit);
		napoli.setEnabled(canEdit);	
	}
	
	public void editRules(MsgMasterRule ruleMsg) throws MalformedMessageException{
		if(ruleMsg.getGameType().equals(GameType.SCOPA) && ruleMsg instanceof MsgScopaRules){
			
			MsgScopaRules scopaMsg = (MsgScopaRules) ruleMsg;
			
			scopa.setSelected(scopaMsg.getScopa());
			reverse.setSelected(scopaMsg.getReverse());
			napoli.setSelected(scopaMsg.getNapoli());
			
			this.invalidate();
			
		} else
			throw new MalformedMessageException();
	}

	@Override
	public RulePanel clone() {	
		return new ScopaRulePanel(canEdit);
	}

	@Override
	public MsgMasterRule getMsgRule(PlayerName senderID) {
		return new MsgScopaRules(reverse.isSelected(), napoli.isSelected(), scopa.isSelected(), senderID);
	}

	

	
	
	

}
