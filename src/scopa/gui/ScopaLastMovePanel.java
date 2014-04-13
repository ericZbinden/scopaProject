package scopa.gui;

import gui.util.EmptyPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scopa.logic.ScopaDeck;
import scopa.logic.ScopaFactory;
import scopa.logic.card.OffuscatedScopaCard;
import scopa.logic.card.ScopaCard;
import util.PlayerName;

public class ScopaLastMovePanel extends JPanel {

	private static final Dimension minimum = new Dimension(250, 250);

	private CardLabel lastPlayed;
	private CardsLabel lastTaken;
	private JLabel nextPlayerLabel;

	private PlayerName nextPlayer;

	public ScopaLastMovePanel() {

		lastPlayed = new CardLabel(new OffuscatedScopaCard());
		lastTaken = new CardsLabel(Arrays.asList(new OffuscatedScopaCard(), new OffuscatedScopaCard()));

		Box layout = new Box(BoxLayout.Y_AXIS);
		layout.setOpaque(false);

		//Played
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setOpaque(false);
		JLabel labelPlayed = new JLabel("Played: ");
		labelPlayed.setVerticalAlignment(JLabel.CENTER);
		top.add(labelPlayed, BorderLayout.WEST);
		JPanel centerPlayed = new EmptyPanel();
		lastPlayed.setAlignmentX(CENTER_ALIGNMENT);
		lastPlayed.setAlignmentY(CENTER_ALIGNMENT);
		centerPlayed.add(lastPlayed);
		top.add(new EmptyPanel(), BorderLayout.EAST);
		top.add(centerPlayed, BorderLayout.CENTER);
		layout.add(top);

		//Taken
		JPanel down = new JPanel();
		down.setOpaque(false);
		down.setLayout(new BorderLayout());
		JLabel labelTaken = new JLabel("Taken: ");
		labelTaken.setVerticalAlignment(JLabel.CENTER);
		down.add(labelTaken, BorderLayout.WEST);
		JPanel centerTaken = new EmptyPanel();
		lastTaken.setAlignmentX(CENTER_ALIGNMENT);
		centerTaken.add(lastTaken);
		down.add(new EmptyPanel(), BorderLayout.EAST);
		down.add(centerTaken, BorderLayout.CENTER);
		layout.add(down);

		//Next player
		nextPlayer = ScopaConstant.SCOPA_SRV_NAME;
		nextPlayerLabel = new JLabel();
		nextPlayerLabel.setHorizontalAlignment(JLabel.CENTER);
		this.updateNextPlayer(nextPlayer);

		//ScopaLastMovePanel itself
		this.setLayout(new BorderLayout());
		this.add(layout, BorderLayout.CENTER);
		this.add(nextPlayerLabel, BorderLayout.SOUTH);
		this.setBackground(ScopaConstant.backgroundColor);
		this.setMinimumSize(minimum);
	}

	public void updateLastMove(ScopaCard lastPlayed, List<ScopaCard> lastTaken) {
		this.lastPlayed.setCard(lastPlayed);
		this.lastTaken.setCards(lastTaken);
		this.repaint();
	}

	public void updateNextPlayer(PlayerName nextPlayer) {
		this.nextPlayer = nextPlayer;
		nextPlayerLabel.setText("Next player is: " + this.nextPlayer.getName());
		nextPlayerLabel.repaint();
	}

	public CardLabel getLastPlayed() {
		return lastPlayed;
	}

	public CardsLabel getLastTaken() {
		return lastTaken;
	}

	public PlayerName getNextPlayer() {
		return nextPlayer;
	}

	// Test purpose	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final ScopaLastMovePanel panel = new ScopaLastMovePanel();
		JButton button = new JButton("Update");
		ActionListener al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ScopaDeck deck = ScopaFactory.getNewScopaDeck();
				deck.shuffle();
				List<ScopaCard> taken = deck.draw3Cards();
				ScopaCard played = deck.drawCard();
				panel.updateLastMove(played, taken);
				//panel.updateNextPlayer(new PlayerName("Bli" + new Random().nextInt()));
			}

		};
		button.addActionListener(al);
		JPanel temp = new JPanel();
		temp.setLayout(new BorderLayout());
		temp.add(panel);
		temp.add(button, BorderLayout.SOUTH);
		frame.add(temp);
		frame.setSize(minimum);
		frame.setVisible(true);
	}

}
