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
	
	private ScopaCard card;
	private Image img;	
	private int borderSize = 5;
	private boolean isSelected;

	public CardLabel(ScopaCard card) {
		this.card=card;
		isSelected = false;
		this.setSize(new Dimension(43,60));
		if(!isEmpty())
			img = new ImageIcon(this.getImagePath()).getImage();
		
		this.setOpaque(false);
		//this.setTransferHandler(new ScopaCardTransfertHandler());	
	}
	
	public void setSelected(boolean selected){
		this.isSelected = selected;
		this.revalidate();
		this.repaint();
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public void setCard(ScopaCard card){
		this.card=card;
		if(!isEmpty())
			img = new ImageIcon(this.getImagePath()).getImage();
		else
			img = null;
		
		this.revalidate();
		this.repaint();
	}
	
	public ScopaCard getCard(){
		return card;
	}
	
	public Image getImage(){
		return img;
	}
	
	public int getBoderSize(){
		return borderSize;
	}
	
	public void setBorderSize(int size){
		this.borderSize=size;
		this.revalidate();
		this.repaint();
	}
	
	private String getImagePath(){
		if(isEmpty()){
			return "null";
		} else {
			return "resources/img/"+card.getColor()+"/"+ScopaValue.val(card)+".png";
		}
	}
	
	public boolean isEmpty(){
		if(card==null)
			return true;
		else 
			return false;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		
		Dimension d = this.getSize();
		if(!isEmpty()){
			g.drawImage(img, borderSize, borderSize, (int)d.getWidth()-2*borderSize, (int)d.getHeight()-2*borderSize, null);
		} else {
			g.setColor(Color.black);
			g.drawRect(borderSize, borderSize, (int)d.getWidth()-2*borderSize, (int)d.getHeight()-2*borderSize);			
		}	
		
		if(isSelected){
			g.setColor(Color.yellow);
			g.drawRect(borderSize-1, borderSize-1, (int)d.getWidth()-2*borderSize+1, (int)d.getHeight()-2*borderSize+1);
		}
	}
	
	@Override
	public boolean equals(Object that){
		if (that != null && (that instanceof CardLabel)){
			//Logger.debug("Testing equality between CardLabel: "+this.toString()+" and: "+that.toString());
			CardLabel thatCard = (CardLabel) that;
			
			if(this.isEmpty() && thatCard.isEmpty())
				return true;
			
			else if (!this.isEmpty() && !thatCard.isEmpty())
				return this.getCard().equals(thatCard.getCard());
		}
		return false;
	}
	
	@Override
	public String toString(){
		if(!isEmpty()){
			return card.toString();
		} else {
			return "EmptyCard";
		}
	}
	
//	/**
//	 * Display test
//	 * @param args
//	 */
//	public static void main(String[] args){
//		JFrame frame = new JFrame();
//		CardLabel card = new CardLabel(new ScopaCard(ScopaValue.seven, ScopaColor.gold));
//		frame.add(card);
//		frame.setSize(new Dimension(300,300));		
//		//frame.pack();
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setVisible(true);
//	}
}
