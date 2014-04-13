package scopa.gui;

import game.GameType;
import gui.api.RulePanel;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import scopa.com.MsgScopaRules;
import scopa.logic.ScopaRules;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgCaster;
import com.msg.MsgMasterRule;

public class ScopaRulePanel extends RulePanel implements ActionListener {

	protected JCheckBox scopa = new JCheckBox("Scopa");
	private JCheckBox reverse = new JCheckBox("Reverse");
	private JCheckBox napoli = new JCheckBox("Napoli");

	public ScopaRulePanel() {
		//size = 290,300
		this.setLayout(new GridLayout(3, 1));
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

	private ScopaRulePanel(boolean canEdit) {
		this();
		this.setEdit(canEdit);
	}

	@Override
	public void setEdit(boolean canEdit) {
		super.setEdit(canEdit);

		scopa.setEnabled(canEdit);
		reverse.setEnabled(canEdit);
		napoli.setEnabled(canEdit);
	}

	@Override
	public void editRules(MsgMasterRule ruleMsg) throws MalformedMessageException {
		if (GameType.SCOPA.equals(ruleMsg.getGameType())) {

			MsgScopaRules scopaMsg = MsgCaster.castMsg(MsgScopaRules.class, ruleMsg);
			ScopaRules rules = scopaMsg.getRules();

			scopa.setSelected(rules.isRuleScopaEnable());
			reverse.setSelected(rules.isRuleReverseEnable());
			napoli.setSelected(rules.isRuleNapoliEnable());

			this.invalidate();

		} else {
			throw new MalformedMessageException("Expected " + GameType.SCOPA + " game type but was: " + ruleMsg.getGameType());
		}
	}

	@Override
	public RulePanel clone() {
		ScopaRulePanel clone = new ScopaRulePanel(canEdit);
		clone.napoli.setSelected(napoli.isSelected());
		clone.scopa.setSelected(scopa.isSelected());
		clone.reverse.setSelected(reverse.isSelected());
		clone.setEdit(scopa.isEnabled());
		return clone;
	}

	@Override
	public MsgMasterRule getMsgRule(PlayerName senderID) {
		ScopaRules rules = new ScopaRules();
		rules.setRuleNapoli(napoli.isSelected());
		rules.setRuleScopa(scopa.isSelected());
		rules.setRuleReverse(reverse.isSelected());

		return new MsgScopaRules(rules, senderID);
	}

}
