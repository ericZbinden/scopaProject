package scopa.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import scopa.logic.card.ScopaCard;

public class CardsLabel extends JPanel {

	private static final int maxCardWidth = 80;
	private static final int maxCardHeight = 100;

	private List<ScopaCard> cards;

	private List<Image> images;

	public CardsLabel() {
		cards = new ArrayList<>();
		images = new ArrayList<>();

		this.setPreferredSize(new Dimension(maxCardWidth * cards.size(), maxCardHeight));
		this.setOpaque(false);
	}

	public <T extends ScopaCard> CardsLabel(List<T> cards) {
		this();
		this.setCards(cards);
	}

	public <T extends ScopaCard> void setCards(List<T> cards) {
		int preivousCardCount = this.cards.size();
		int newCardCount = cards.size();
		this.cards = (List<ScopaCard>) cards;
		this.setPreferredSize(new Dimension(maxCardWidth * newCardCount, maxCardHeight));
		images.clear();

		for (ScopaCard card : this.cards) {
			Image img = new ImageIcon(card.getImgPath()).getImage();
			images.add(img);
		}

		if (preivousCardCount == newCardCount) {
			//this.repaint();
		} else {
			this.revalidate();
		}
	}

	public List<ScopaCard> getCards() {
		return cards;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Dimension d = this.getSize();
		if (!isEmpty()) {
			int nbOfCards = cards.size();
			int removal = 0;
			if ((nbOfCards * maxCardWidth) > d.getWidth() && nbOfCards != 1) {
				removal = (int) (((nbOfCards * maxCardWidth) - d.getWidth()) / (nbOfCards - 1));
				if (removal > (nbOfCards - 1) * maxCardWidth) {
					removal = (nbOfCards - 1) * maxCardWidth;
				}
			}

			int height = maxCardHeight;
			if (d.getHeight() < maxCardHeight) {
				height = (int) d.getHeight();
			}

			int currentX = 0;
			int i = 0;
			for (Image img : images) {
				g.drawImage(img, currentX - (i * removal), 0, maxCardWidth, height, null);
				currentX += maxCardWidth;
				i++;
			}

		}
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	//Test purpose
	//	public static void main(String[] args) {
	//		CardsLabel l = new CardsLabel();
	//
	//		l.setCards(Arrays.asList(ScopaTestUtil.aceOfGold, ScopaTestUtil.kingOfGold, ScopaTestUtil.sevenOfSword));
	//
	//		JFrame frame = new JFrame();
	//		frame.add(l);
	//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	//		frame.pack();
	//		frame.setVisible(true);
	//
	//	}

}
