package gui.game;

import game.GameType;
import gui.api.ChatMsgSender;
import gui.api.GameGui;
import gui.api.PlayMsgSender;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import util.Logger;
import util.PlayerName;

import com.msg.MsgPlay;

public class GameGuiFrame extends JFrame implements GameGui, PlayMsgSender {

	private final String TITLE = "ScopaProject";

	private PlayMsgSender playSender;
	private ChatMsgSender chatMsgSender;
	private PlayerName client;

	private final List<JFrame> registeredFrames; //TODO externalize it in a service

	private GamePanel gamePanel;

	public GameGuiFrame(ChatMsgSender chatMsgSender) {
		this.chatMsgSender = chatMsgSender;
		registeredFrames = new ArrayList<>();

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();

		this.setLayout(new BorderLayout());
		//this.add(new EmptyPanel(), BorderLayout.CENTER);

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

		//this.removeAll();
		this.add(gamePanel, BorderLayout.CENTER);

		this.invalidate();
		this.repaint();
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void play(MsgPlay msg) {
		try {
			// Protect from anything bad that could happen
			gamePanel.update(msg);
			//	this.revalidate();
		} catch (Exception e) {
			Logger.error("Exception caused by msg: " + msg.toString(), e);
			JOptionPane.showMessageDialog(this, e.getMessage() + " caused by msg:\n\t" + msg.toString());
		}
	}

	@Override
	public void startNack() {
		this.playSender = null;
		this.gamePanel = null;

		this.revalidate();
		this.setVisibleToFalse();
	}

	@Override
	public void sendMsgPlay(MsgPlay msg) {
		playSender.sendMsgPlay(msg);
	}

	@Override
	public void chat(PlayerName sender, String txt) {
		gamePanel.writeIntoChat(sender, txt);
	}

	@Override
	public void sendChatMsg(String txt) {
		this.chatMsgSender.sendChatMsg(txt);
	}

	@Override
	public ChatMsgSender getChatMsgSender() {
		return chatMsgSender;
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
	public void dispose() {

		// TODO if game is finished, do not ask confirmation and exit
		int choice = showConfirmDialog("Game is still on, do you want to exit ?");
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

		for (JFrame frame : registeredFrames) {
			frame.dispose();
		}
		super.dispose();

	}

	@Override
	public int showConfirmDialog(String messageToClient) {
		return JOptionPane.showConfirmDialog(this, messageToClient);
	}

	@Override
	public void showMessageDialog(String messageToClient) {
		JOptionPane.showMessageDialog(this, messageToClient);
	}

	@Override
	public void registerFrame(JFrame frameToRegister) {
		registeredFrames.add(frameToRegister);
	}

	@Override
	public void removeFrame(JFrame frameToUnregister) {
		registeredFrames.remove(frameToUnregister);

	}

	@Override
	public Dimension getSize() {
		return super.getSize();
	}

}
