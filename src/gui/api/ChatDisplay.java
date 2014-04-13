package gui.api;

import util.PlayerName;

public interface ChatDisplay {

	public void writeIntoChat(PlayerName writer, String text);

	public void writeIntoChatFromServer(String text);

	public ChatMsgSender getChatMsgSender();

	public void setChatMsgSender(ChatMsgSender chatMsgSender);

}
