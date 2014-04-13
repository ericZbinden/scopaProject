package gui.api;

import game.GameType;

import java.awt.Dimension;

import javax.swing.JFrame;

import util.PlayerName;

import com.msg.MsgPlay;

public interface GameGui extends ChatMsgSender {

	public void play(MsgPlay msg);

	public void startNack();

	public void start(PlayerName client, GameType gameType, PlayMsgSender playSender);

	public ChatMsgSender getChatMsgSender();

	public void setVisibleToFalse();

	public void chat(PlayerName sender, String txt);

	public void sendMsgPlay(MsgPlay msg);

	public int showConfirmDialog(String messageToClient);

	public void showMessageDialog(String messageToClient);

	public void registerFrame(JFrame frameToRegister);

	public void removeFrame(JFrame frameToUnregister);

	public Dimension getSize();

}
