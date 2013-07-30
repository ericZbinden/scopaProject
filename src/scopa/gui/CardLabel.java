package scopa.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaColor;
import scopa.logic.ScopaValue;

public class CardLabel extends JPanel {

	private ScopaCard card;
	private Image img;

	public CardLabel(ScopaCard card) {
		this.card=card;
		//this.setBackground(Color.black);
		this.setSize(new Dimension(43,60));
		img = new ImageIcon(this.getImagePath()).getImage();
	}
	
	public ScopaCard getCard(){
		return card;
	}
	
	private String getImagePath(){
		return "resources/img/"+card.getColor()+"/"+ScopaValue.val(card)+".png";
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		Dimension d = this.getSize();
		g.drawImage(img, 0, 0, (int)d.getWidth(), (int)d.getHeight(), null);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		CardLabel card = new CardLabel(new ScopaCard(ScopaValue.seven, ScopaColor.gold));
		frame.add(card);
		frame.setSize(new Dimension(300,300));		
		//frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
}
