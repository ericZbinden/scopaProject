package zilch.gui;

import gui.api.ChatMsgSender;
import gui.game.GamePanel;
import util.PlayerName;

import com.msg.MsgPlay;

public class ZilchGamePanel extends GamePanel {

	public ZilchGamePanel() {
	}

	@Override
	public void update(MsgPlay msg) {
		// nothing

	}

	@Override
	public GamePanel clone() {
		return new ZilchGamePanel();
	}

	@Override
	public void writeIntoChat(PlayerName writer, String text) {
		// nothing

	}

	@Override
	public void writeIntoChatFromServer(String text) {
		// nothing

	}

	@Override
	public ChatMsgSender getChatMsgSender() {
		return null;
	}

	@Override
	public void setChatMsgSender(ChatMsgSender chatMsgSender) {
		// nothing

	}

}
