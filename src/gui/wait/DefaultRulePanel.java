package gui.wait;

import javax.swing.JLabel;

import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgMasterRule;

import gui.RulePanel;

public class DefaultRulePanel extends RulePanel {

	private static final long serialVersionUID = -4934851131965627000L;

	public DefaultRulePanel() {
		JLabel title = new JLabel("<html><h1>Scopa Project.</h1>Please select a game.</html>");
		this.add(title);
	}

	@Override
	public void editRules(MsgMasterRule ruleMsg)
			throws MalformedMessageException {

	}

	@Override
	public RulePanel clone() {
		return new DefaultRulePanel();
	}

	@Override
	public MsgMasterRule getMsgRule(PlayerName senderID) {
		return null;
	}

	@Override
	public void enableAction(boolean enable) {		
	}

}
