package gui.game;

import game.GameType;
import gui.ChatMsgSender;
import gui.ChatPanel;
import gui.PlayMsgSender;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgChat;
import com.msg.MsgPlay;

public class GameGuiFrame extends JFrame implements GameGui {
	
	private PlayMsgSender playSender;
	private PlayerName client;
	
	private JPanel gPanel = new JPanel();
	private JPanel scorePanel = new JPanel();
	private ChatPanel chatPanel ;
	
	private GamePanel gamePanel;



	public GameGuiFrame(ChatMsgSender chatMsgSender) {
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		chatPanel = new ChatPanel(chatMsgSender);

		JSplitPane split = new JSplitPane();
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split.add(gPanel, JSplitPane.LEFT);
		gPanel.setLayout(new BorderLayout());
		
		JSplitPane split2 = new JSplitPane();
		split2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.add(split2, JSplitPane.RIGHT);
		split2.add(scorePanel, JSplitPane.TOP);
		split2.add(chatPanel, JSplitPane.BOTTOM);
		
		//TODO scorePanel.add(...)
		//if(gamePanel != null){
		//	gamePanel.add(gameType.getGamePanel());
		//}
			
		this.add(split);
		this.setPreferredSize(new Dimension(width,height));
		//this.setResizable(false);
		this.pack();
		//this.setVisible(true); called by WaitingFrame
	}

	@Override
	public void start(PlayerName client, GameType gameType, PlayMsgSender playSender) {
		this.client = client;
		this.playSender = playSender;
		gamePanel = gameType.getGamePanel();
		gamePanel.setGameGui(this);
		gPanel.removeAll();
		gPanel.add(gamePanel, BorderLayout.CENTER);
		gPanel.revalidate();
		this.setVisible(true);
	}

	@Override
	public void update(MsgPlay msg) {
		try{
			//Protect from anything bad that could happen
			gamePanel.update(msg);
		} catch (Exception e){
			Logger.error(e.getClass().toString()+": "+e.getMessage()+"\nCaused by msg: "+msg.toString());
		}
	}

	@Override
	public void startNack() {
		this.playSender = null;
		this.gamePanel = null;
		gPanel.removeAll();
		this.setVisibleToFalse();	
	}

	@Override
	public void sendMsgPlay(MsgPlay msg) {
		playSender.sendMsgPlay(msg);	
	}
	
	@Override
	public void chat(MsgChat msg) {
		chatPanel.writeIntoChat(msg.getSenderID(), msg.getText());
	}

	@Override
	public PlayerName getLocalClient() {
		return client;
	}

	@Override
	public void setVisibleToFalse() {
		this.setVisible(false);		
	}



}
