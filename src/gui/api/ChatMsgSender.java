package gui.api;

import util.PlayerName;

public interface ChatMsgSender {
	
	public void sendChatMsg(String msg);
	
	public PlayerName getLocalClient();

}
