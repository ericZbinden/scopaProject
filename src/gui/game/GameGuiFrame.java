package gui.game;

import game.GameType;
import gui.api.ChatMsgSender;
import gui.api.GameGui;
import gui.api.PlayMsgSender;
import gui.util.ChatPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import util.Logger;
import util.PlayerName;

import com.msg.MsgChat;
import com.msg.MsgPlay;

public class GameGuiFrame extends JFrame implements GameGui {

	private final String TITLE = "ScopaProject";

	private PlayMsgSender playSender;
	private PlayerName client;

	private JPanel gPanel = new JPanel();
	private JPanel scorePanel = new JPanel();
	private ChatPanel chatPanel;

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

		// TODO scorePanel.add(...)
		// if(gamePanel != null){
		// gamePanel.add(gameType.getGamePanel());
		// }

		this.add(split);
		this.setPreferredSize(new Dimension(width, height));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// this.setResizable(false);
		this.setTitle(TITLE);
		// this.setVisible(true); called by WaitingFrame
	}

	@Override
	public void start(PlayerName client, GameType gameType, PlayMsgSender playSender) {
		this.client = client;
		this.playSender = playSender;
		this.setTitle(TITLE + ": " + gameType.toString());
		gamePanel = gameType.getGamePanel();
		gamePanel.setGameGui(this);
		gPanel.removeAll();
		gPanel.add(gamePanel, BorderLayout.CENTER);
		gPanel.revalidate();
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void update(MsgPlay msg) {
		try {
			// Protect from anything bad that could happen
			gamePanel.update(msg);
			gamePanel.revalidate();
			// gamePanel.repaint();
			// this.pack();
			this.revalidate();
		} catch (Exception e) {
			Logger.error(e.getClass().toString() + ": " + e.getMessage() + "\nCaused by msg: " + msg.toString());
			e.printStackTrace();
			// TODO remonter l'info à l'UI
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
		this.writeIntoChat(msg.getSenderID(), msg.getText());
	}

	@Override
	public PlayerName getLocalClient() {
		return client;
	}

	@Override
	public void setVisibleToFalse() {
		this.setVisible(false);
	}

	@Override
	public void writeIntoChat(PlayerName sender, String txt) {
		chatPanel.writeIntoChat(sender, txt);
	}

	@Override
	public void dispose() {

		// TODO if game is finished, do not ask confirmation and exit
		int choice = JOptionPane.showConfirmDialog(this, "Game is still on, do you want to exit ?");
		switch (choice) {
		case JOptionPane.YES_OPTION:
			disconnect();
			return;
		case JOptionPane.NO_OPTION:
		case JOptionPane.CANCEL_OPTION:
		default:
			return;
		}
	}

	@Override
	public void disconnect() {
		playSender.disconnect();
		super.dispose();

	}

}
