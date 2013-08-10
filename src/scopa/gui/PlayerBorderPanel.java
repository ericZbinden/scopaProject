package scopa.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaHand;

public class PlayerBorderPanel extends BorderPanel implements MouseListener {
	
	private JPanel cards;	 //player cards
	private ScopaGamePanel parent;


	public PlayerBorderPanel(ScopaHand hand, ScopaGamePanel parent) {
		super(hand, BorderLayout.SOUTH);
		this.parent=parent;
		
		cards = new JPanel();
		cards.setLayout(new GridLayout(1,3));			

	}
	
	protected void updateCardDisplay(){
		this.revalidate();
		this.repaint();
	}
	
	public void newHand(List<ScopaCard> newCards){
		hand.newHand(newCards);
		cards.removeAll();
		for(ScopaCard card : hand.getHand()){
			CardLabel clabel = new CardLabel(card);
			clabel.addMouseListener(this);
			clabel.setTransferHandler(new ScopaCardTransfertHandler(parent));
			cards.add(clabel);
		}
		cards.revalidate();
		cards.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Object src = arg0.getSource();
		if(src instanceof CardLabel){
			CardLabel source = (CardLabel) src;
			source.getTransferHandler().exportAsDrag(source, arg0, TransferHandler.MOVE);
		} 		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}

}
