package scopa.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import com.msg.MalformedMessageException;
import com.msg.MsgCaster;
import com.msg.MsgPlay;

import scopa.com.MsgBaseConf;
import scopa.com.MsgScopa;
import scopa.com.MsgScopaHand;
import scopa.com.MsgScopaPlay;
import scopa.logic.EmptyHand;
import scopa.logic.OffuscatedHand;
import scopa.logic.ScopaCard;
import scopa.logic.ScopaFactory;
import scopa.logic.ScopaGame;
import scopa.logic.ScopaHand;
import scopa.logic.ScopaTable;
import util.Logger;
import util.PlayerName;

import game.GameType;
import gui.game.GameGuiFrame;
import gui.game.GamePanel;

public class ScopaGamePanel extends GamePanel {

	private ScopaTable table;
	//private ScopaDeck deck;
	//private ScopaHand hand;
	
	private PlayerName nextPlayer;
	
	private BorderPanel south;
	private BorderPanel west;
	private BorderPanel east;
	private BorderPanel north;
	private TablePanel center;
	
	public ScopaGamePanel(){
		//logic
		this.table= ScopaFactory.getNewScopaTable();
		nextPlayer = ScopaGame.SRV_NAME;
		
		//gui
		this.build();
	}
	
	private void build(){
		
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(800, 800));
		this.setMinimumSize(this.getPreferredSize());
		this.setLayout(new BorderLayout());
		
		this.south= new PlayerBorderPanel(null,this);
		this.west = new BorderPanel(BorderLayout.WEST);
		this.east = new BorderPanel(BorderLayout.EAST);
		this.north = new BorderPanel(BorderLayout.NORTH);
		this.center = new TablePanel();
		
		this.add(south,BorderLayout.SOUTH); 
		this.add(west,BorderLayout.WEST); 
		this.add(east,BorderLayout.EAST); 
		this.add(north,BorderLayout.NORTH); 
		this.add(center, BorderLayout.CENTER);	
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
	
	public void dudePlay(PlayerName name){
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

	
	public static void main(String[] args){
		//test main
		GameGuiFrame frame = new GameGuiFrame(null);
		//ScopaGamePanel sgp = new ScopaGamePanel();
		//frame.add(sgp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.start(new PlayerName("Coubii"), GameType.SCOPA, null);
	}

	@Override
	public void update(MsgPlay msg) {
		
		try{
			
			MsgScopa msgScopa = MsgCaster.castMsg(MsgScopa.class, msg);
			nextPlayer = msgScopa.nextPlayerToPlayIs();
			switch(msgScopa.getScopaType()){
			case baseConf:
				MsgBaseConf msgConf = MsgCaster.castMsg(MsgBaseConf.class, msgScopa);
				PlayerName player = msgConf.getPlayerEast();
				if(player.equals("")){
					east.setHand(new EmptyHand());
				} else {
					east.setHand(new OffuscatedHand(player,0));
				}
				player = msgConf.getPlayerWest();
				if(player.equals("")){
					west.setHand(new EmptyHand());
				} else {
					west.setHand(new OffuscatedHand(player,0));
				}
				player = msgConf.getPlayerNorth();
				if(player.equals("")){
					north.setHand(new EmptyHand());
				} else {
					north.setHand(new OffuscatedHand(player,0));
				}
				ScopaHand playerHand = new ScopaHand(this.getLocalClient(),0);
				playerHand.newHand(msgConf.getHand());
				south.setHand(playerHand); 
				table.putInitial(msgConf.getTable());
				break;
			case play:
				MsgScopaPlay msgPlay = MsgCaster.castMsg(MsgScopaPlay.class, msgScopa);
				ScopaCard played = msgPlay.getPlayed(); //TODO display who played and the replay thing
				List<ScopaCard> taken = msgPlay.getTaken();
				List<ScopaCard> check = table.putCard(played, taken);
				if(check == null){
					//TODO cheeter or unconsistent state
				}			
				this.dudePlay(msgPlay.getSenderID());
				break;
			case hand:
				MsgScopaHand msgHand = MsgCaster.castMsg(MsgScopaHand.class, msgScopa);
				south.newHand(msgHand.getCards());
				break;
			case ack:
				//MsgScopaAck msgAck = MsgCaster.castMsg(MsgScopaAck.class, msgScopa);
				//Nothing todo
				break;
			default:
				Logger.debug("Unknown scopa type: "+msgScopa.getScopaType()+", ignoring it.");
			}
			this.revalidate();
			this.repaint();
		} catch (MalformedMessageException e){
			Logger.debug("Malformed msg of type: "+msg.getGameType().getGameType()+". Ignoring it.");
		}
	}
	
	public void sendMsgScopaPlay(ScopaCard played, List<ScopaCard> taken){
		MsgScopaPlay msg = new MsgScopaPlay(this.getLocalClient(),played,taken,null);
		this.sendMsgPlay(msg);
	}
	
	public void alertPlayerWrongPlay(){
		this.showWarningToPlayer("The move you tried to do is not a valid move");
	}


	@Override
	public GamePanel clone() {
		ScopaGamePanel sgp = new ScopaGamePanel();
		sgp.east = new BorderPanel(east.hand,BorderLayout.EAST);
		sgp.west = new BorderPanel(west.hand,BorderLayout.WEST);
		sgp.south = new BorderPanel(south.hand,BorderLayout.SOUTH);
		sgp.north = new BorderPanel(north.hand,BorderLayout.NORTH);
		sgp.table = ScopaFactory.getNewScopaTable();
		sgp.table.putCards(table.cardsOnTable());
		sgp.nextPlayer = new PlayerName(nextPlayer.getName());
		//center ?
		return sgp;
	}

}
