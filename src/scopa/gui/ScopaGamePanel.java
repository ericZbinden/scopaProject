package scopa.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scopa.logic.EmptyHand;
import scopa.logic.ScopaCard;
import scopa.logic.ScopaDeck;
import scopa.logic.ScopaDeckCards;
import scopa.logic.ScopaGame;
import scopa.logic.ScopaHand;
import scopa.logic.ScopaTable;
import scopa.logic.ScopaTableCards;
import util.Logger;

import gui.GamePanel;

public class ScopaGamePanel extends GamePanel {

private ScopaTable table;
private ScopaDeck deck;
	
	private BorderPanel p1;
	private BorderPanel p2;
	private BorderPanel p3;
	private BorderPanel p4;
	
	public ScopaGamePanel(int players){
		//logic
		this.table= ScopaTableCards.newTable(); //FIXME logic is externalized into server, should not be there
		boolean ok;
		do{
			this.deck = ScopaDeckCards.newDeck();	
			deck.shuffle();
			ok = table.putInitial(deck.drawInitialCards());
		} while (!ok);
		
		//gui
		this.build(players);

	}
	
	private void build(int players){
		
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(800, 800));
		this.setMinimumSize(this.getPreferredSize());
		this.setLayout(new BorderLayout());
		
		switch(players){ //FIXME ça va pas
		case 4:
			
		case 3:
			
		case 2:
			break;
		default:
			throw new IllegalArgumentException("Number of players should be 2,3 or 4 but was: "+players);
		}

		
		this.p1= new BorderPanel(new ScopaHand("Bob",1), Zone.S); //FIXME fix team
		this.p2 = new BorderPanel(new EmptyHand(),Zone.W);
		this.p3 = new BorderPanel(new EmptyHand(),Zone.E);
		this.p4 = new BorderPanel(new ScopaHand("PLAYER",2), Zone.N);
		
		this.add(p1,BorderLayout.NORTH); 
		this.add(p2,BorderLayout.WEST); 
		this.add(p3,BorderLayout.EAST); 
		this.add(p4,BorderLayout.SOUTH); 
		this.add(this.buildTable(), BorderLayout.CENTER);
	
		
	}

	
	private JPanel buildTable(){
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 400));
		this.setMinimumSize(this.getPreferredSize());
		this.setMaximumSize(this.getPreferredSize());
		//TODO
		return panel;
	}
	
	public void giveNewHand(List<ScopaCard> playerCards){
		p1.newHand(playerCards);
		p2.newHand(null);
		p3.newHand(null);
		p4.newHand(null);
	}
	
	public void SouthPlay(ScopaCard playedCard){
		p1.playCard(playedCard);
	}
	
	public void NorthPlay(){
		p4.playCard(null);
	}
	
	public void EastPlay(){
		p3.playCard(null);
	}
	
	public void WestPlay(){
		p2.playCard(null);
	}
	
	

	
	private class BorderPanel extends JPanel {
		
		private Zone z; //region du board
		private int cards = 0;
		public ScopaHand hand; //the player hand
		private boolean otherPlayer;
		
		private JLabel guiCards; //other player cards image
		
		public BorderPanel(boolean otherPlayer, Zone z){
			this(otherPlayer,null,z);
		}
		
		public BorderPanel(ScopaHand hand, Zone z){
			this(true,hand,z);
		}
		
		private BorderPanel(boolean otherPlayer, ScopaHand hand, Zone z){
			this.otherPlayer=otherPlayer;
			this.hand = hand;
			this.z = z;

			switch(z){
			case N:
				this.setBackground(Color.blue);
				this.setPreferredSize(new Dimension(400, 200));
			case S:
				this.setBackground(Color.GREEN);
				this.setPreferredSize(new Dimension(400, 200));
				break;
			case E:
				this.setBackground(Color.RED);
				this.setPreferredSize(new Dimension(200, 400));
			case W:
				this.setBackground(Color.cyan);
				this.setPreferredSize(new Dimension(200, 400));
				break;
			}
			this.setMinimumSize(this.getPreferredSize());
			this.setMaximumSize(this.getPreferredSize());
			
			JLabel playerName = new JLabel("    "+hand.getPlayer());
			playerName.setPreferredSize(new Dimension(200,50));
			this.guiCards= new JLabel();
			guiCards.setPreferredSize(new Dimension(157,97));
			
			this.add(playerName);
			this.add(guiCards);
		}
		
		public void newHand(List<ScopaCard> newCards){
			if(hand != null){
				hand.newHand(newCards);
			} else if (otherPlayer){
				cards = 3;
			} else {
				return;
			}
			this.updateCardDisplay();
			this.invalidate();
			this.repaint();
		}	
		
		public void playCard(ScopaCard playedCard){
			if(hand != null){
				boolean ok = hand.playCard(playedCard);
				if(!ok)
					Logger.error("Unable to play card "+playedCard.toString());
			} else if (otherPlayer){
				cards--;
			} else {
				return;
			}
			
			this.updateCardDisplay();
			this.invalidate();
			this.repaint();			
		}
		
		private void updateCardDisplay(){
			if (hand!=null){
				
			} else if (cards > 0){		
				guiCards.setIcon(new ImageIcon("resources/img/gui/carte"+cards+".png"));
			} else {
				guiCards.setIcon(null);
			}
			guiCards.invalidate();
		}
	}
	
	private enum Zone {
		N,S,E,W
	}
	
	
	public static void main(String[] args){
		//test main
		JFrame frame = new JFrame();
		ScopaGamePanel sgp = new ScopaGamePanel(2);
		frame.add(sgp);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

}
