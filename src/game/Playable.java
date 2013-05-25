package game;

import java.util.List;

import com.msg.MsgMasterRule;
import com.server.wait.Config;
import com.server.wait.GameStartCondition;

public interface Playable {
	

	public GameType getGameType();
	
	public GameStartCondition checkInitialConfig(List<Config> configs, MsgMasterRule rules);
	
	public void initGame(List<Config> configs, MsgMasterRule rules);
}
