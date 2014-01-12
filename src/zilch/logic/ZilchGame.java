package zilch.logic;

import game.GameType;
import game.Playable;

import java.util.List;

import com.msg.MalformedMessageException;
import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.server.api.ServerApi;
import com.server.exceptions.IllegalInitialConditionException;
import com.server.wait.Config;

public class ZilchGame implements Playable {

	public ZilchGame() {
	}

	@Override
	public GameType getGameType() {
		return GameType.ZILCH;
	}

	@Override
	public void checkInitialConfig(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {
		throw new IllegalInitialConditionException("Not implemented");

	}

	@Override
	public void initGame(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {
		throw new IllegalInitialConditionException("Not implemented");

	}

	@Override
	public void start(ServerApi api) {

	}

	@Override
	public void receiveMsgPlay(MsgPlay msg) throws MalformedMessageException {

	}

}
