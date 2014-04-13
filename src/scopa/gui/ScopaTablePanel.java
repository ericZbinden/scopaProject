package scopa.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import scopa.logic.ScopaFactory;
import scopa.logic.ScopaTable;
import scopa.logic.card.ScopaCard;
import util.Logger;

public class ScopaTablePanel extends JPanel implements MouseListener, ScopaTable {

	private final GridLayout defaultLayout;
	private boolean biggerThan11;
	private int emptyToAdd;

	private Map<ScopaCard, CardLabel> cards;

	private ScopaTable table;

	public ScopaTablePanel() {
		defaultLayout = new GridLayout(2, 6);
		this.setLayout(defaultLayout);
		cards = new HashMap<>();
		biggerThan11 = false;
		emptyToAdd = 12;
		fillEmptyPanels();

		table = ScopaFactory.getNewScopaTable();

		this.setBackground(ScopaConstant.backgroundColor);
		this.setPreferredSize(new Dimension(400, 400));
	}

	private void fillEmptyPanels() {
		if (emptyToAdd < 0) {
			removeEmptyPanel(0 - emptyToAdd);
		} else if (emptyToAdd > 0) {
			do {
				CardLabel ecl = new CardLabel();
				// ecl.addMouseListener(this);
				this.add(ecl);
				emptyToAdd--;
			} while (emptyToAdd != 0);
		}
	}

	/* ------------- GUI METHODS ----------- */

	private void removeCardFromGui(ScopaCard card) {
		CardLabel cl = cards.remove(card);
		if (cl != null) {
			if (biggerThan11 && cards.size() < 12) {
				this.setLayout(defaultLayout);
			}
			emptyToAdd++;
			this.remove(cl);
		} else {
			Logger.debug("Try to remove card " + card.toString() + " but was not present in the table");
		}
	}

	private void removeCardsFromGui(List<ScopaCard> cards) {
		for (ScopaCard card : cards) {
			removeCardFromGui(card);
		}
		fillEmptyPanels();
	}

	private void removeAllCardsFromGui() {
		this.removeCardsFromGui(new ArrayList<ScopaCard>(cards.keySet()));
	}

	private void removeEmptyPanel(int nb) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component compo = this.getComponent(i);
			if (compo instanceof CardLabel) {
				CardLabel cardLabel = (CardLabel) compo;
				if (cardLabel.isEmpty()) {
					this.remove(compo);
					i--;
					nb--;
					emptyToAdd++;
					if (nb == 0) {
						assert emptyToAdd == 0;
						return;
					}
				}
			}
		}
		Logger.error("Unable to remove enough emptyPanel. Left: " + nb);
	}

	private void addCardToGui(ScopaCard card, boolean removeBlank) {
		CardLabel cLabel = new CardLabel(card);
		cLabel.addMouseListener(this);
		cards.put(card, cLabel);
		int size = cards.size();
		if (size > 11) {
			this.setLayout(new GridLayout(3, 6));
			biggerThan11 = true;
		}

		this.addBeforeEmptyPanel(cLabel);
		emptyToAdd--;

		if (removeBlank) {
			removeEmptyPanel(1);
		}
	}

	private void addBeforeEmptyPanel(CardLabel clabel) {
		Component[] compo = this.getComponents();
		for (int i = 0; i < compo.length; i++) {
			CardLabel card = (CardLabel) compo[i];
			if (card.isEmpty()) {
				this.add(clabel, i);
				return;
			}
		}
	}

	private void addCardsToGui(List<ScopaCard> cards) {
		for (ScopaCard card : cards) {
			addCardToGui(card, false);
		}
		removeEmptyPanel(cards.size());
	}

	public List<ScopaCard> getSelectedCards() {
		List<ScopaCard> selected = new ArrayList<>(cards.size());
		for (CardLabel cl : cards.values()) {
			if (cl.isSelected()) {
				selected.add(cl.getCard());
			}
		}
		return selected;
	}

	private void addAndRemoveCardsFromGui(ScopaCard playedCard, List<ScopaCard> takenCards) {
		if (takenCards == null) {
			return;
		} else if (takenCards.isEmpty()) {
			this.addCardToGui(playedCard, true);
		} else {
			for (ScopaCard card : takenCards) {
				if (!card.equals(playedCard)) {
					this.removeCardFromGui(card);
					fillEmptyPanels();
				}
			}
		}
	}

	/*
	 * ---------- Interface methods. From where will come the updates --------
	 */

	@Override
	public List<ScopaCard> putCard(ScopaCard card) {
		return putCard(card, new ArrayList<ScopaCard>(0));
	}

	@Override
	public List<ScopaCard> putCard(ScopaCard card, List<ScopaCard> cards) {
		List<ScopaCard> returned = table.putCard(card, cards);
		this.addAndRemoveCardsFromGui(card, returned);
		this.revalidate();
		return returned;
	}

	@Override
	public void putCards(List<ScopaCard> cards) {
		table.putCards(cards);
		this.addCardsToGui(cards);
		this.revalidate();
	}

	@Override
	public List<ScopaCard> takeAll() {
		List<ScopaCard> returnMe = table.takeAll();
		this.removeAllCardsFromGui();
		this.revalidate();
		return returnMe;
	}

	@Override
	public boolean putInitial(List<ScopaCard> cards) {
		boolean accepted = table.putInitial(cards);
		if (accepted) {
			this.addCardsToGui(cards);
			//this.revalidate();
		}
		return accepted;
	}

	/* ------------- OTHERS ----------- */

	@Override
	public List<List<ScopaCard>> allPossibleTakeWith(ScopaCard card) {
		return table.allPossibleTakeWith(card);
	}

	@Override
	public List<ScopaCard> cardsOnTable() {
		return table.cardsOnTable();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Component compo = arg0.getComponent();

		if (compo instanceof CardLabel) {
			CardLabel label = (CardLabel) compo;

			label.setSelected(!label.isSelected());
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
		// Nothing
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Nothing
	}

	@Override
	public boolean isEmpty() {
		return table.isEmpty();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Dimension size = this.getSize();
		g.drawRect(1, 1, size.width - 2, size.height - 2);
	}

}
