package game;

import java.util.List;

import com.msg.MalformedMessageException;
import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.server.api.ServerApi;
import com.server.exceptions.IllegalInitialConditionException;
import com.server.wait.Config;

public interface Playable extends Cloneable{
	

	public GameType getGameType();
		
	/**
	 * Check the initial config
	 * @param configs
	 * @param rules
	 * @return true if the config are valid
	 */
	public void checkInitialConfig(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException;
	
	/**
	 * Initiate a new game if the condition are valid
	 * @param configs
	 * @param rules
	 * @throws IllegalInitialConditionException throw this exception when the
	 * initial condition are illegal
	 */
	public void initGame(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException;
	
	/**
	 * Start the game
	 */
	public void start(ServerApi api);
	
	/**
	 * Play a move
	 * @throws MalformedMessageException when the game expected another msg type
	 */
	public void receiveMsgPlay(MsgPlay msg) throws MalformedMessageException;
}
