package scopa.gui;

import gui.util.EmptyPanel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import scopa.logic.ScopaRules;
import scopa.logic.ScopaSetResult;
import util.PlayerName;

public class ScopaScoreFrame extends JFrame implements ActionListener {

	private static final String POINT_STR = "point_";
	private static final String TOTAL_STR = "total_";
	private static final String CARDS_STR = "cards_";
	private static final String GOLD_STR = "gold_";
	private static final String SEVEN_STR = "7_";
	private static final String SEVEN_GOLD_STR = "7gold_";
	private static final String SCOPA_STR = "scopa_";
	private static final String NAPOLI_STR = "napoli_";

	int numberOfTeam;
	ScopaRules rules;

	Map<PlayerName, ScopaSetResult> results;

	Map<String, JLabel> labels;

	public ScopaScoreFrame(ScopaRules rules, int numberOfTeam) {
		this.rules = rules;
		this.numberOfTeam = numberOfTeam;
		labels = new HashMap<>();
		results = new HashMap<>();

		JPanel up = new JPanel();
		JPanel down = new JPanel();

		//UP
		int raw = 4;
		up.add(new EmptyPanel());
		for (int i = 1; i <= numberOfTeam; i++) {
			JLabel t = new JLabel("Team " + i + "    ");
			up.add(t);
		}
		up.add(new JLabel("Cards : "));
		for (int i = 1; i <= numberOfTeam; i++) {
			JLabel t = new JLabel("0");
			labels.put(CARDS_STR + i, t);
			up.add(t);
		}
		up.add(new JLabel("Gold    : "));
		for (int i = 1; i <= numberOfTeam; i++) {
			JLabel t = new JLabel("0");
			labels.put(GOLD_STR + i, t);
			up.add(t);
		}
		up.add(new JLabel("Seven : "));
		for (int i = 1; i <= numberOfTeam; i++) {
			JLabel t = new JLabel("0");
			labels.put(SEVEN_STR + i, t);
			up.add(t);
		}
		up.add(new JLabel("7 Gold : "));
		for (int i = 1; i <= numberOfTeam; i++) {
			JLabel t = new JLabel("0");
			labels.put(SEVEN_GOLD_STR + i, t);
			up.add(t);
		}
		if (rules.isRuleScopaEnable()) {
			raw++;
			up.add(new JLabel("Scopa : "));
			for (int i = 1; i <= numberOfTeam; i++) {
				JLabel t = new JLabel("0");
				labels.put(SCOPA_STR + i, t);
				up.add(t);
			}
		}
		if (rules.isRuleNapoliEnable()) {
			raw++;
			up.add(new JLabel("Napoli : "));
			for (int i = 1; i <= numberOfTeam; i++) {
				JLabel t = new JLabel("0");
				labels.put(NAPOLI_STR + i, t);
				up.add(t);
			}
		}
		up.setLayout(new GridLayout(raw + 1, numberOfTeam + 1));

		//DOWN
		down.setLayout(new GridLayout(2, numberOfTeam + 1));
		down.add(new JLabel("Points:"));
		for (int i = 1; i <= numberOfTeam; i++) {
			JLabel p = new JLabel("0");
			labels.put(POINT_STR + i, p);
			down.add(p);
		}
		down.add(new JLabel("Total   :"));
		for (int i = 1; i <= numberOfTeam; i++) {
			JLabel t = new JLabel("0");
			labels.put(TOTAL_STR + i, t);
			down.add(t);
		}

		//BUTTON
		JButton OKButton = new JButton("ok");
		OKButton.addActionListener(this);

		//FRAME
		Box total = new Box(BoxLayout.Y_AXIS);
		total.add(up);
		total.add(new JSeparator(JSeparator.HORIZONTAL));
		total.add(down);
		total.add(OKButton);
		this.add(total);

		//LAYOUT
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setVisible(false);
		this.pack();
	}

	public void updateScore(Map<PlayerName, ScopaSetResult> results, boolean display) {
		this.results = results;

		for (ScopaSetResult result : this.results.values()) {
			int team = result.getTeam();

			labels.get(CARDS_STR + team).setText(String.valueOf(result.getCards()));
			labels.get(GOLD_STR + team).setText(String.valueOf(result.getGold()));
			labels.get(SEVEN_STR + team).setText(String.valueOf(result.getSeven()));
			labels.get(SEVEN_GOLD_STR + team).setText(String.valueOf(result.getSevenOfGold()));
			if (rules.isRuleScopaEnable()) {
				labels.get(SCOPA_STR + team).setText(String.valueOf(result.getScopa()));
			}
			if (rules.isRuleNapoliEnable()) {
				labels.get(NAPOLI_STR + team).setText(String.valueOf(result.getNapoli()));
			}
			labels.get(POINT_STR + team).setText(String.valueOf(result.getPoints()));
			labels.get(TOTAL_STR + team).setText(String.valueOf(result.getTotal()));
		}

		this.revalidate();
		this.setVisible(display);
	}

	public Map<PlayerName, ScopaSetResult> getResults() {
		return results;
	}

	public ScopaRules getRules() {
		return rules;
	}

	public void setRules(ScopaRules rules) {
		this.rules = rules;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.setVisible(false);
		//dispose ?

		//		Test purpose
		//		ScopaSetResult temp1 = new ScopaSetResult(new PlayerName("Coubii"), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		//		ScopaSetResult temp2 = new ScopaSetResult(new PlayerName("Bob"), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
		//
		//		Map<PlayerName, ScopaSetResult> newStuff = new HashMap<>();
		//		newStuff.put(temp1.getPlayer(), temp1);
		//		newStuff.put(temp2.getPlayer(), temp2);
		//
		//		this.updateScore(newStuff);

	}

	public static void main(String[] args) {
		ScopaRules rules = new ScopaRules();
		rules.setRuleScopa(true);
		rules.setRuleNapoli(true);
		new ScopaScoreFrame(rules, 2);
	}

}
