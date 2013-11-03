package scopa.gui;

import javax.swing.JLabel;

import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgMasterRule;

import gui.RulePanel;

public class ZilchRulePanel extends RulePanel {

	
	
	public ZilchRulePanel() {
		this.add(new JLabel("Zilch"));
	}

	@Override
	public void editRules(MsgMasterRule ruleMsg)
			throws MalformedMessageException {

	}

	@Override
	public RulePanel clone() {
		return new ZilchRulePanel();
	}

	@Override
	public MsgMasterRule getMsgRule(PlayerName senderID) {
		return null;
	}

}
