package gui.game;

import com.msg.MsgPlay;

import game.GameType;
import gui.PlayMsgSender;

public interface GameGui extends PlayMsgSender {
		
	public void update(MsgPlay msg);
	
	public void startNack();
	
	public void start(String client, GameType gameType, PlayMsgSender playSender);
		
	public void setVisibleToFalse();

}
