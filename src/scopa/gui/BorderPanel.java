package scopa.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scopa.logic.EmptyHand;
import scopa.logic.OffuscatedHand;
import scopa.logic.ScopaCard;
import scopa.logic.ScopaHand;
import util.Logger;
import util.PlayerName;

public class BorderPanel extends JPanel {
	
	protected String borderLayoutProperty; //region du board
	protected ScopaHand hand; //the player hand
	
	protected JLabel playerName;
	private JLabel guiCards; //other player cards image
	
	public BorderPanel(String borderLayoutProperty){
		this((ScopaHand)new EmptyHand(),borderLayoutProperty);
	}
	
	public BorderPanel(PlayerName otherPlayer, int team, String borderLayoutProperty){
		this((ScopaHand)new OffuscatedHand(otherPlayer,team), borderLayoutProperty);
	}
	
	public BorderPanel(ScopaHand hand, String borderLayoutProperty){
		this.hand = hand;
		this.borderLayoutProperty = borderLayoutProperty;

		switch(borderLayoutProperty){
		case BorderLayout.NORTH:
			this.setBackground(Color.blue);
			this.setPreferredSize(new Dimension(400, 200)); //TODO handle resizable dimension
			break;
		case BorderLayout.SOUTH:
			this.setBackground(Color.GREEN);
			this.setPreferredSize(new Dimension(400, 200));
			break;
		case BorderLayout.EAST:
			this.setBackground(Color.RED);
			this.setPreferredSize(new Dimension(200, 400));
			break;
		case BorderLayout.WEST:
			this.setBackground(Color.cyan);
			this.setPreferredSize(new Dimension(200, 400));
			break;
		default:
			throw new IllegalArgumentException("BorderLayoutProperty should be BorderLayout.[NORTH/EAST/SOUTH/WEST], but was: "+borderLayoutProperty);
		}
		this.setMinimumSize(this.getPreferredSize());
		this.setMaximumSize(this.getPreferredSize());
		
		Box panel = new Box(BoxLayout.X_AXIS);
		
		playerName = new JLabel();
		//playerName.setPreferredSize(new Dimension(200,50));
		panel.add(playerName);

		if(!borderLayoutProperty.equals(BorderLayout.SOUTH)){
			this.guiCards= new JLabel();
			guiCards.setPreferredSize(new Dimension(157,97));
			panel.add(guiCards);
		}		
		
		this.add(panel);
	}
	
	public void setHand(ScopaHand hand){
		this.hand=hand;
		playerName = new JLabel("    "+hand.getPlayer());
		playerName.revalidate();
		updateCardDisplay();
	}
	
	public PlayerName getPlayerName(){
		return hand.getPlayer();
	}
	
	public void newHand(List<ScopaCard> newCards){
		
		hand.newHand(newCards);
		this.updateCardDisplay();
	}	
	
	public boolean playCard(ScopaCard playedCard){
		boolean ok = hand.playCard(playedCard);
		if(!ok){
			Logger.error("Unable to play card "+playedCard.toString());
			return ok;
		}
		
		this.updateCardDisplay();
		return ok;
	}
	
	protected void updateCardDisplay(){
		
		if (hand instanceof EmptyHand){
			guiCards.removeAll();
		} else if (hand instanceof OffuscatedHand){
			guiCards.setIcon(new ImageIcon("resources/img/gui/carte"+hand.getNumberCardInHand()+".png"));
			guiCards.revalidate();
			this.repaint();			
		} 
	}
	
}