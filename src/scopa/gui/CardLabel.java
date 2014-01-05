package scopa.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaValue;

public class CardLabel extends JPanel {

	private static final long serialVersionUID = 5902223281341496902L;

	private static final String offuscatedCardImgPath = "resources/img/gui/carte1.png";

	private ScopaCard card;
	private Image img;
	private int borderSize = 5;
	private boolean isSelected;
	private boolean isOffuscated;

	public CardLabel() {
		this.isSelected = false;
		this.isOffuscated = false;
		this.setSize(new Dimension(43, 60));

		this.img = new ImageIcon(this.getImagePath()).getImage();
		this.setOpaque(false);
	}

	public CardLabel(ScopaCard card) {
		this();
		this.card = card;

		if (card != null) {
			this.isOffuscated = card.isOffuscated();
			if (!this.isOffuscated) {
				this.setToolTipText(card.toString());
			}
		}

		this.img = new ImageIcon(this.getImagePath()).getImage();
		// this.setTransferHandler(new ScopaCardTransfertHandler());
	}

	public void setSelected(boolean selected) {
		this.isSelected = selected;
		this.revalidate();
		this.repaint();
	}

	public boolean isSelected() {
		return isSelected;
	}

	public boolean isOffuscated() {
		return isOffuscated;
	}

	public void setCard(ScopaCard card) {
		this.card = card;

		if (card != null) {
			this.isOffuscated = card.isOffuscated();
			if (!isOffuscated) {
				this.setToolTipText(card.toString());
			}
		} else {
			this.isOffuscated = false;
			this.setToolTipText(null);
		}

		img = new ImageIcon(this.getImagePath()).getImage();

		this.invalidate();
		// this.revalidate();
		// this.repaint();
	}

	public ScopaCard getCard() {
		return card;
	}

	public Image getImage() {
		return img;
	}

	public int getBoderSize() {
		return borderSize;
	}

	public void setBorderSize(int size) {
		this.borderSize = size;
		this.revalidate();
		this.repaint();
	}

	private String getImagePath() {
		if (isEmpty())
			return "null";

		else if (isOffuscated)
			return offuscatedCardImgPath;

		else
			return "resources/img/" + card.getColor() + "/" + ScopaValue.val(card) + ".png";
	}

	public boolean isEmpty() {
		return card == null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Dimension d = this.getSize();
		if (!isEmpty()) {
			g.drawImage(img, borderSize, borderSize, (int) d.getWidth() - 2 * borderSize, (int) d.getHeight() - 2 * borderSize, null);
		} else {
			g.setColor(Color.black);
			g.drawRect(borderSize, borderSize, (int) d.getWidth() - 2 * borderSize, (int) d.getHeight() - 2 * borderSize);
		}

		if (isSelected) {
			// TODO set the selected rectangle bigger depending on the size
			g.setColor(Color.yellow);
			g.drawRect(borderSize - 1, borderSize - 1, (int) d.getWidth() - 2 * borderSize + 1, (int) d.getHeight() - 2 * borderSize + 1);
		}
	}

	@Override
	public boolean equals(Object that) {
		if (that != null && (that instanceof CardLabel)) {
			// Logger.debug("Testing equality between CardLabel: "+this.toString()+" and: "+that.toString());
			CardLabel thatCard = (CardLabel) that;

			if (this.isEmpty() && thatCard.isEmpty())
				return true;

			else if (!this.isEmpty() && !thatCard.isEmpty())
				return this.getCard().equals(thatCard.getCard());
		}
		return false;
	}

	@Override
	public String toString() {
		if (!isEmpty())
			return card.toString();
		else
			return "EmptyCard";
	}

	// /**
	// * Display test
	// * @param args
	// */
	// public static void main(String[] args){
	// JFrame frame = new JFrame();
	// CardLabel card = new CardLabel(new ScopaCard(ScopaValue.seven,
	// ScopaColor.gold));
	// frame.add(card);
	// frame.setSize(new Dimension(300,300));
	// //frame.pack();
	// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	// frame.setVisible(true);
	// }
}
