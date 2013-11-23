package gui.api;

import game.GameType;
import util.PlayerName;

import com.msg.MsgChat;
import com.msg.MsgPlay;

public interface GameGui extends PlayMsgSender {

	public void update(MsgPlay msg);

	public void startNack();

	public void start(PlayerName client, GameType gameType, PlayMsgSender playSender);

	public void setVisibleToFalse();

	public void chat(MsgChat msg);

	public void writeIntoChat(PlayerName sender, String txt);

}
