package gui.game;

import com.msg.MsgPlay;

import game.GameType;

public interface GameGui {
	
	public void setGameType(GameType gameType);
	
	public void update(MsgPlay msg);
	
	public void startNack();
	
	public void setVisible(boolean visible);

}
