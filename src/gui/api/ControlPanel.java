package gui.api;

import game.GameType;
import util.PlayerName;

import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.msg.MsgReco;
import com.server.api.ServerState;
import com.server.wait.Config;

public interface ControlPanel {

	public PlayerName getClientID();

	/** The down connection from the server is down */
	public void serverDown();

	/** Called when the ui frame should be closed and disposed */
	public void dispose();

	public ServerState getServerState();

	public boolean isPlaying();

	public boolean isWaiting();

	public boolean isStarting();

	public void reset(String reason);

	public void reco(MsgReco msg);

	public void wrSlot(PlayerName impactedID, Config newConf);

	public void masterGame(GameType gameType);

	public void masterRule(MsgMasterRule msg);

	public void start();

	public void startNack(String reason);

	public void startAck(GameType gameType);

	public void play(MsgPlay msg);

	public void newPlayer(PlayerName openId, PlayerName newPlayerId);

	public void disconnect(PlayerName decoPlayerId, int emptyId);

	public void chat(PlayerName sender, String text);

	public void chatFromServer(String text);

	public void chatDuringPlay(PlayerName sender, String txt);

}
