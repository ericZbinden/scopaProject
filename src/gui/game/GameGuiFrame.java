package gui.game;

import game.GameType;
import gui.ChatMsgSender;
import gui.ChatPanel;
import gui.GamePanel;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.msg.MsgPlay;

public class GameGuiFrame extends JFrame implements GameGui {
	
	private JPanel gPanel = new JPanel();
	private JPanel scorePanel = new JPanel();
	private JPanel chatPanel = new JPanel();
	
	private GamePanel gamePanel;



	public GameGuiFrame(ChatMsgSender chatMsgSender, GameType gameType) {
		if(gameType != null){
			//gamePanel = gameType.getGamePanel();
		}
		
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		JSplitPane split = new JSplitPane();
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split.add(gPanel);
		
		JSplitPane split2 = new JSplitPane();
		split2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.add(split2);
		split2.add(scorePanel);
		split2.add(chatPanel);
		
		chatPanel.add(new ChatPanel(chatMsgSender));
		//TODO scorePanel.add(...)
		//if(gamePanel != null){
		//	gamePanel.add(gameType.getGamePanel());
		//}
			
		this.setPreferredSize(new Dimension(width,height));
		this.setResizable(false);
		this.pack();
		//this.setVisible(true); called by WaitingFrame
	}

	@Override
	public void setGameType(GameType gameType) {
		gPanel.removeAll();
		//gamePanel.add(gameType.getGamePanel()); TODO
		gPanel.invalidate();
		this.repaint();
	}

	@Override
	public void update(MsgPlay msg) {
		gamePanel.update(msg);
	}

	@Override
	public void startNack() {
		// TODO Auto-generated method stub
		
	}


}
