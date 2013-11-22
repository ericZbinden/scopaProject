package gui.api;

import util.PlayerName;

import com.msg.MsgChat;
import com.msg.MsgPlay;

import game.GameType;

public interface GameGui extends PlayMsgSender {
		
	public void update(MsgPlay msg);
	
	public void startNack();
	
	public void start(PlayerName client, GameType gameType, PlayMsgSender playSender);
		
	public void setVisibleToFalse();
	
	public void chat(MsgChat msg);

}
