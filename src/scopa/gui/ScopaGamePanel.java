package scopa.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.msg.MsgPlay;

import scopa.com.MsgBaseConf;
import scopa.com.MsgScopa;
import scopa.com.MsgScopaAck;
import scopa.com.MsgScopaHand;
import scopa.com.MsgScopaPlay;
import scopa.logic.EmptyHand;
import scopa.logic.OffuscatedHand;
import scopa.logic.ScopaCard;
import scopa.logic.ScopaDeck;
import scopa.logic.ScopaDeckImpl;
import scopa.logic.ScopaFactory;
import scopa.logic.ScopaGame;
import scopa.logic.ScopaHand;
import scopa.logic.ScopaTable;
import scopa.logic.ScopaTableImpl;
import util.Logger;

import game.GameType;
import gui.GamePanel;

public class ScopaGamePanel extends GamePanel {

	private ScopaTable table;
	//private ScopaDeck deck;
	//private ScopaHand hand;
	
	private String nextPlayer;
	
	private BorderPanel south;
	private BorderPanel west;
	private BorderPanel east;
	private BorderPanel north;
	private JPanel center;
	
	public ScopaGamePanel(){
		//logic
		this.table= ScopaFactory.getNewScopaTable();
		nextPlayer = ScopaGame.SRV;
		
		//gui
		this.build();
	}
	
	private void build(){
		
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(800, 800));
		this.setMinimumSize(this.getPreferredSize());
		this.setLayout(new BorderLayout());
		
		this.south= new BorderPanel(BorderLayout.SOUTH);
		this.west = new BorderPanel(BorderLayout.WEST);
		this.east = new BorderPanel(BorderLayout.EAST);
		this.north = new BorderPanel(BorderLayout.NORTH);
		
		this.add(south,BorderLayout.NORTH); 
		this.add(west,BorderLayout.WEST); 
		this.add(east,BorderLayout.EAST); 
		this.add(north,BorderLayout.SOUTH); 
		this.add(this.buildTable(), BorderLayout.CENTER);	
	}

	
	private JPanel buildTable(){
		
		center = new JPanel();
		center.setPreferredSize(new Dimension(400, 400));
		center.setMinimumSize(this.getPreferredSize());
		center.setMaximumSize(this.getPreferredSize());
		//TODO
		return center;
	}
	
	public void giveNewHand(List<ScopaCard> playerCards){
		south.newHand(playerCards);
		west.newHand(null);
		east.newHand(null);
		north.newHand(null);
	}
	
	public void southPlay(ScopaCard playedCard){
		south.playCard(playedCard);
	}
	
	public void northPlay(){
		north.playCard(null);
	}
	
	public void eastPlay(){
		east.playCard(null);
	}
	
	public void westPlay(){
		west.playCard(null);
	}
	
	public void dudePlay(String name){
		if (west.getPlayerName().equals(name)){
			westPlay();
		} else if (east.getPlayerName().equals(name)){
			eastPlay();
		} else if (north.getPlayerName().equals(name)){
			northPlay();
		} else {
			Logger.error("Unknown player "+name+". Move ignored");
		}
	}

	
//	public static void main(String[] args){
//		//test main
//		JFrame frame = new JFrame();
//		ScopaGamePanel sgp = new ScopaGamePanel();
//		frame.add(sgp);
//		frame.pack();
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setVisible(true);
//	}

	@Override
	public void update(MsgPlay msg) {
		
		if (msg.getGameType().equals(GameType.SCOPA)){
			MsgScopa msgScopa = (MsgScopa) msg;
			nextPlayer = msgScopa.nextPlayerToPlayIs();
			switch(msgScopa.getScopaType()){
			case baseConf:
				MsgBaseConf msgConf = (MsgBaseConf) msgScopa;
				String player = msgConf.getPlayerEast();
				if(player.equals("")){
					east.setHand(new EmptyHand());
				} else {
					east.setHand(new OffuscatedHand(player,0)); //FIXME setup team
				}
				player = msgConf.getPlayerWest();
				if(player.equals("")){
					west.setHand(new EmptyHand());
				} else {
					west.setHand(new OffuscatedHand(player,0)); //FIXME setup team
				}
				player = msgConf.getPlayerNorth();
				if(player.equals("")){
					north.setHand(new EmptyHand());
				} else {
					north.setHand(new OffuscatedHand(player,0)); //FIXME setup team
				}
				ScopaHand playerHand = new ScopaHand("",0); //FIXME playerName and team
				playerHand.newHand(msgConf.getHand());
				south.setHand(playerHand); 
				table.putInitial(msgConf.getTable());
				break;
			case play:
				MsgScopaPlay msgPlay = (MsgScopaPlay) msgScopa;
				ScopaCard played = msgPlay.getPlayed(); //TODO display who played and the replay thing
				List<ScopaCard> taken = msgPlay.getTaken();
				if(taken.isEmpty()){
					table.putCard(played);
				} else {
					table.putCard(played, taken);
				}
				this.dudePlay(msgPlay.getSenderID());
				break;
			case hand:
				MsgScopaHand msgHand = (MsgScopaHand) msgScopa;
				south.newHand(msgHand.getCards());
				break;
			case ack:
				//MsgScopaAck msgAck = (MsgScopaAck) msgScopa;
				break;
			default:
				Logger.debug("Unknown scopa type: "+msgScopa.getScopaType()+", ignoring it.");
			}
			//TODO repaint
		} else {
			Logger.debug("Malformed msg of type: "+msg.getGameType().getGameType()+". Ignoring it.");
		}		
	}


	@Override
	public GamePanel clone() {
		ScopaGamePanel sgp = new ScopaGamePanel();
		sgp.east = new BorderPanel(east.hand,BorderLayout.EAST);
		sgp.west = new BorderPanel(west.hand,BorderLayout.WEST);
		sgp.south = new BorderPanel(south.hand,BorderLayout.SOUTH);
		sgp.north = new BorderPanel(north.hand,BorderLayout.NORTH);
		sgp.table = ScopaFactory.getNewScopaTable();
		sgp.table.putInitial(table.cardsOnTable()); //FIXME force cards
		sgp.nextPlayer = new String(nextPlayer);
		//center ?
		return sgp;
	}

}
