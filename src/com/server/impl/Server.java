package com.server.impl;

import game.GameType;
import game.Playable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.Logger;
import util.PlayerName;
import util.ReservedName;

import com.msg.MalformedMessageException;
import com.msg.Message;
import com.msg.MsgChat;
import com.msg.MsgConfig;
import com.msg.MsgConnect;
import com.msg.MsgDeco;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.msg.MsgReset;
import com.msg.MsgStartAck;
import com.msg.MsgStartNack;
import com.server.api.MessageFilter;
import com.server.api.ServerApi;
import com.server.api.ServerConnect;
import com.server.api.ServerState;
import com.server.exceptions.IllegalInitialConditionException;
import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

public class Server implements Runnable, ServerConnect, ServerApi {

	/** static reference to the class. */
	public static final PlayerName SERVER_NAME = new PlayerName(ReservedName.SERVER_NAME);
	private ServerState state;
	private MessageFilter filter;
	private static Server server = null;
	private static int listeningPort;
	private static String pwd;
	/** The server socket of this thread */
	private ServerSocket listener;
	/** The listener thread */
	private static Thread t;

	/** Ref to the gui that launched this server */
	private static ActionListener al;

	// ref to connected clients
	private int openSlot;
	private Map<PlayerName, ServerCSocket> clients;
	private Map<PlayerName, Config> confs;

	// ref to game. Here declared default game
	private MsgMasterRule rule = GameType.SCOPA.getRulePanel().getMsgRule(SERVER_NAME);
	private Playable game;

	/**
	 * @return a static reference.
	 */
	public static Server getInstance(int port, String pwd, ActionListener al) {
		// FIXME should not be static, this is not what is intended
		if (server == null) {
			server = new Server(port, pwd, al, new DefaultMessageFilter());
		}
		return server;
	}

	// private Server(int port, String password, ActionListener listener) {
	// this(port, password, listener, new EmptyMessageFilter());
	// }

	/**
	 * Default builder. Start the listening thread.
	 */
	private Server(int port, String password, ActionListener listener, MessageFilter filter) {
		state = ServerState.none;
		al = listener;
		listeningPort = port;
		pwd = password;
		this.filter = filter;
		this.openSlot = 1;
		t = new Thread(null, this, "Server_" + port);
		clients = new HashMap<>();
		confs = new HashMap<>();
		Config empty = new EmptyConf(0);
		confs.put(empty.getClientID(), empty);
		t.start();
	}

	/**
	 * start a Socket listening on the default TCP port. launch a new thread of PorkutRequest for each new connections request.
	 */
	@Override
	public void run() {
		Logger.log("Server Listener launched");
		try {

			listener = new ServerSocket(listeningPort);
			Socket srv;
			while (true) {
				srv = listener.accept();
				ServerCSocket req = new ServerCSocket(srv, this, filter);
				String client;
				if (req.getClientID() == null) {
					client = "Master";
				} else {
					client = req.getClientID().getName();
				}
				Thread socketListener = new Thread(null, req, "ServerSocket_" + client + "_P" + req.getSocket().getLocalPort());
				socketListener.start();
			}

		} catch (InterruptedIOException e) {
			Logger.debug("Server interrupted");
		} catch (Exception e) {
			if (!t.isInterrupted()) {
				al.actionPerformed(new ActionEvent(this, 12, e.getMessage()));
				Logger.error("Server crashed: " + e.getLocalizedMessage());
			}
		} finally {
			try {
				listener.close();
			} catch (Exception e) {
				// Die silently
			}
			if (!ServerState.closing.equals(state) && !ServerState.closed.equals(state)) {
				this.close();
			}
		}
	}

	/**
	 * Interrupt the server
	 */
	public synchronized void shutDownServer() {
		state = ServerState.closing; // FIXME do not close correctly the
										// listener
		close();
	}

	private void close() {
		if (ServerState.closing.equals(state)) {
			t.interrupt();
		}

		for (ServerCSocket scs : clients.values()) {
			scs.close(false);
		}
		clients.clear();
		confs.clear();
		openSlot = 0;
		listener = null;
		server = null;
		state = ServerState.closed;

	}

	/* ** ** ** UTIL METHODS ** ** ** */
	public synchronized boolean isRunning() {
		if (ServerState.closed.equals(state) || ServerState.closing.equals(state) || ServerState.none.equals(state)) {
			return false;
		}
		return true;
	}

	@Override
	public void saveRule(MsgMasterGame rule) {
		saveRule(rule.getGameType().getRulePanel().getMsgRule(SERVER_NAME));
	}

	@Override
	public synchronized void saveRule(MsgMasterRule rule) {
		this.rule = rule;
	}

	@Override
	public synchronized boolean areAllPlayersReady() {

		for (Config conf : confs.values()) {
			if (!conf.isReady()) {
				return false;
			}
		}

		return true;
	}

	private synchronized boolean acceptNewConnection() {
		return ServerState.none.equals(state) || ServerState.waiting.equals(state);
	}

	private boolean acceptPlayerName(PlayerName playerName) {
		if (ReservedName.isReserved(playerName)) {
			Logger.debug("PlayerName \"" + playerName + "\" is reserved");
			return false;
		}

		if (clients.containsKey(playerName)) {
			Logger.debug("PlayerName \"" + playerName + "\" is already taken");
			return false;
		}

		return true;
	}

	/* ** ** ** PROCESS METHODS ** ** ** */
	@Override
	public synchronized void startGame() throws IllegalInitialConditionException {
		if (!areAllPlayersReady()) {
			throw new IllegalInitialConditionException("All players are not ready!");
		}

		GameType gameToStart = rule.getGameType();
		game = gameToStart.getGame();

		// try to generate the game
		game.initGame(new ArrayList<Config>(confs.values()), rule);

		ServerCSocket current = null;
		try {
			// Send to all players the starting conf
			for (ServerCSocket player : clients.values()) {
				current = player;
				MsgStartAck msg = new MsgStartAck(gameToStart);
				player.sendToThisClient(msg);
			}
		} catch (IOException e) {
			current.close(true);
			// alert all players that the game did not start
			String reason = "Player " + current.getClientID() + " has quit";
			this.transferMsgToAll(new MsgStartNack(reason), new PlayerName(ReservedName.SERVER_NAME));

			throw new IllegalInitialConditionException(reason);
		}

		state = ServerState.playing;
		game.start(this);
	}

	@Override
	public synchronized void updateWR(PlayerName impactedID, Config state, ServerCSocket scs) {

		Config c = confs.get(impactedID);
		if (c != null) {

			if (state instanceof ClosedConf) {
				// Closing an open slot
				if (!(c instanceof EmptyConf)) {
					// closing an used slot (kick)
					confs.remove(impactedID);
					ServerCSocket kickedClient = clients.remove(impactedID);
					sendToThisClient(kickedClient, new MsgReset("You have been kicked"));
					kickedClient.close(false);
					Logger.debug("Open slot: " + openSlot);
				} else {
					// Closing an empty slot
					openSlot--;
					confs.remove(impactedID);
					Logger.debug("Open slot: " + openSlot);
				}
			} else if (state instanceof EmptyConf) {
				// A player haz been kicked (slot still open)
				confs.remove(impactedID);
				try {
					this.transfertMsgTo(impactedID, new MsgReset("You have been kicked"));
				} catch (Exception e) {
					Logger.error(e.getLocalizedMessage());
				}
				ServerCSocket kickedClient = clients.remove(impactedID);
				kickedClient.close(true);
				openSlot++;
				Logger.debug("Open slot: " + openSlot);

			} else {
				// Updating a player config
				c.setReady(state.isReady());
				c.setTeam(state.getTeam());
			}

		} else if (state instanceof EmptyConf) {
			// Open a new slot
			confs.put(impactedID, state);
			openSlot++;
			Logger.debug("Open slot: " + openSlot);
		} else if (state instanceof ClosedConf) {
			openSlot--;
			Logger.debug("Open slot: " + openSlot);
		} else {
			Logger.error("unknown client: " + impactedID + " with state: " + state);
		}
	}

	@Override
	public synchronized Message connect(MsgConnect msg, ServerCSocket scs) {

		if (!acceptNewConnection()) { // check if server accept new connection
			Logger.debug("Received msg but was ignored: " + msg.toString());
			return new MsgReset("Game already started");
		}

		if (openSlot <= 0) { // check if empty slot available
			Logger.debug("Server full, refused connection to " + msg.getSenderID());
			return new MsgReset("Server full");
		}

		String password = msg.getPwd();
		PlayerName player = msg.getSenderID();
		Logger.debug("State set to waiting");
		state = ServerState.waiting;

		if (!pwd.equals(password)) { // check pwd
			Logger.debug("Wrong password: \"" + password + "\"");
			return new MsgReset("Wrong password");
		}

		if (!acceptPlayerName(player)) {
			return new MsgReset("PlayerName is already taken, choose another");
		}

		// Connection accepted
		openSlot--;
		List<Config> cs = new ArrayList<Config>();
		Config clientConf = new Config(player, true);
		cs.add(clientConf);

		// replaced an empty slot with the new player
		Config conf = this.getOpenSlot();
		Logger.debug("Replacing slot " + conf.toString() + " by new player" + scs.getClientID());
		confs.remove(conf.getClientID());

		// Build other config for msgCo
		for (Entry<PlayerName, Config> c : confs.entrySet()) {
			Logger.debug(c.getKey() + " : " + c.getValue().toString());
			cs.add(c.getValue());
		}
		MsgConfig msgCo = new MsgConfig(player, cs, rule, conf.getClientID());

		// Add to ref this client
		clients.put(player, scs);
		confs.put(player, clientConf);
		Logger.debug("Open slot left: " + openSlot + "\n" + msgCo.toString());
		return msgCo;

	}

	@Override
	public void play(MsgPlay msg) {

		try {
			if (!game.getGameType().equals(msg.getGameType())) {
				throw new MalformedMessageException("Expected " + game.getGameType() + " game type but was: " + msg.getType());
			}
			game.receiveMsgPlay(msg);
		} catch (MalformedMessageException e) {
			Logger.error("Ignoring msg due to malformed exception.", e);
		}
	}

	@Override
	public synchronized void disconnect(ServerCSocket scs) {
		clients.remove(scs.getClientID());
		Logger.debug("Client " + scs.getClientID() + " quit");

		int id = -1;
		Config open;
		do {
			id++;
			open = new EmptyConf(id);

		} while (confs.get(open.getClientID()) != null);
		confs.remove(scs.getClientID());
		confs.put(open.getClientID(), open);
		openSlot++;
		Logger.debug("Open slot: " + openSlot);
		this.transferMsgToAll(new MsgDeco(scs.getClientID(), scs.getClientID(), id), scs.getClientID());
	}

	private Config getOpenSlot() {
		for (Config s : confs.values()) {
			if (s instanceof EmptyConf) {
				return s;
			}
		}
		Logger.debug("srv> No open slot left");
		return null;
	}

	@Override
	public ServerState getServerState() {
		return state;
	}

	/* ** ** ** SEND METHODS ** ** ** */
	@Override
	public void sendMsgTo(PlayerName client, MsgPlay msg) {
		this.transfertMsgTo(client, msg);
	}

	@Override
	public void sendMsgToAll(MsgPlay msg) {
		this.transferMsgToAll(msg, null);
	}

	@Override
	public void sendMsgToAllExcept(PlayerName client, MsgPlay msg) {
		this.transferMsgToAll(msg, client);
	}

	@Override
	public void writeIntoClientChat(PlayerName client, String txt) {
		this.transfertMsgTo(client, new MsgChat(SERVER_NAME, client, txt));

	}

	@Override
	public void writeIntoAllChat(String txt) {
		this.transferMsgToAll(new MsgChat(SERVER_NAME, null, txt), null);
	}

	@Override
	public void transfertMsgTo(PlayerName clientID, Message msg) {
		ServerCSocket sock = clients.get(clientID);
		if (sock != null) {
			sendToThisClient(sock, msg);
		} else {
			Logger.error("clientID " + clientID + " is unknown, failed to send msg");
			// TODO remonter l'erreur à l'UI
		}
	}

	@Override
	public void transferMsgToAll(Message msg, PlayerName senderID) {
		for (ServerCSocket sock : clients.values()) {
			if (!sock.getClientID().equals(senderID)) {
				sendToThisClient(sock, msg);
			}
		}
	}

	/**
	 * Try to send the message to the provided socket. If an error occurs, the socket is closed
	 * 
	 * @param socket
	 * @param msg
	 */
	private void sendToThisClient(ServerCSocket socket, Message msg) {
		try {
			socket.sendToThisClient(msg);
		} catch (IOException e) {
			Logger.error("srv> Failed to send msg to " + socket.getClientID() + ". Closing.");
			socket.close(true);
		}
	}
}
