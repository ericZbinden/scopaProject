package scopa.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaHand;
import util.Logger;

public class PlayerBorderPanel extends BorderPanel implements MouseListener {

	private JPanel cardsLayout; // player cards

	private Map<ScopaCard, CardLabel> cards; // inner card ref
	private ScopaGamePanel parent; // FIXME should be an interface

	public PlayerBorderPanel(ScopaHand hand, ScopaGamePanel parent) {
		super(hand, BorderLayout.SOUTH);
		this.parent = parent;

		this.cards = new HashMap<>(3);

		this.removeAll();
		cardsLayout = new JPanel();
		Box panel = new Box(BoxLayout.Y_AXIS);
		panel.add(new JLabel(hand.getPlayerName().getName()));
		cardsLayout.setLayout(new GridLayout(1, 3));
		panel.add(cardsLayout);
		this.add(panel, BorderLayout.CENTER);

		this.displayInGuiNewHand();
	}

	@Override
	protected void displayInGuiNewHand() {

		for (ScopaCard card : hand.getHand()) {
			CardLabel clabel = new CardLabel(card);
			cards.put(card, clabel);
			clabel.addMouseListener(this);
			clabel.setTransferHandler(new ScopaCardTransfertHandler(parent));
			cardsLayout.add(clabel);
		}

		cardsLayout.invalidate();
		this.revalidate();
	}

	@Override
	public boolean playCard(ScopaCard playedCard) {
		boolean superAnswer = super.playCard(playedCard);
		if (!superAnswer) {
			Logger.error("You should not have been able to play this card");
		}

		CardLabel clabel = cards.remove(playedCard);
		cardsLayout.remove(clabel);
		cardsLayout.invalidate();

		return superAnswer;
	}

	@Override
	public void newHand(List<ScopaCard> newCards) {

		hand.newHand(newCards);
		displayInGuiNewHand();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Object src = arg0.getSource();
		if (src instanceof CardLabel) {
			CardLabel source = (CardLabel) src;

			List<ScopaCard> selectedOnTable = parent.getSelectedCardsOnTable();

			if (parent.play(source.getCard(), selectedOnTable)) {
				playCard(source.getCard());
			} else {
				parent.showWarningToPlayer("Invalid move");
			}

		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Nothing
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO drag & drop
		// Object src = arg0.getSource();
		// if (src instanceof CardLabel) {
		// CardLabel source = (CardLabel) src;
		// source.getTransferHandler().exportAsDrag(source, arg0,
		// TransferHandler.MOVE);
		// }
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Nothing
	}

}
