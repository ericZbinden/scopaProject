package com.server;

import game.GameType;
import game.Playable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

import scopa.com.MsgBaseConf;
import util.Logger;
import util.PlayerName;
import util.ReservedName;


public class Server implements Runnable, ServerConnect, ServerApi {

	/** static reference to the class. */
	public static final PlayerName SERVER_NAME = new PlayerName(ReservedName.SERVER_NAME);
	private ServerState state = ServerState.none;	
	private static Server server = null;
	private static int listeningPort;	
	private static String pwd;
	/** The server socket of this thread */
	private ServerSocket listener;
	/**The listener thread */
	private static Thread t;
	
	/** Ref to the gui that launched this server */
	private static ActionListener al;

	// ref to connected clients 
	private int openSlot;
	private Map<PlayerName,ServerCSocket> clients;
	private Map<PlayerName,Config> confs;
	
	// ref to game 
	private MsgMasterGame rule = new MsgMasterGame(GameType.SCOPA,null); //default game
	private Playable game;

	
	/**
	 * return the static reference.
	 * @return instance of PorkutServer
	 */
	public static Server getInstance(int port, String pwd,ActionListener al) {
		if (server == null)
			server = new Server(port,pwd,al);
		return server;
	}

	/**
	 * Default builder. Start the listening thread.
	 */
	private Server(int port,String password, ActionListener listener) {
		al = listener;
		listeningPort = port;
		pwd=password;
		this.openSlot = 1;
		t = new Thread(this);
		clients = new HashMap<>();
		confs = new HashMap<>();
		Config empty = new EmptyConf(0);
		confs.put(empty.getClientID(), empty);
		t.start();
	}
	
	/**
	 *  Interrupt the server
	 */
	public void interrupt() {
        t.interrupt();
        close();
    } 
	
	
	private void close(){
		t.interrupt();
		for(ServerCSocket scs : clients.values()){
			scs.close(false);
		}
		clients.clear();
		confs.clear();
		openSlot = 0;
		listener = null;
		server = null;
	}
	
	public boolean isRunning(){
		return server != null;
	}

	/**
	 * start a Socket listening on the default TCP port. launch a new thread of
	 * PorkutRequest for each new connections request.
	 */
	public void run() {
		Logger.log("Server Listener launched");
		try {
			
			listener = new ServerSocket(listeningPort);
			Socket srv;
			while (true) {
				srv = listener.accept();
				ServerCSocket req = new ServerCSocket(srv,this);
				Thread socketListener = new Thread(req);
				socketListener.start();
			}

		} catch (InterruptedIOException e){
			Logger.debug("Server interrupted");
		} catch (Exception e) {
			if(!t.isInterrupted()){
				al.actionPerformed(new ActionEvent(this,12,e.getMessage()));
				Logger.error("Server crashed: "+e.getLocalizedMessage());
			}
		} finally {
			try {
				listener.close();
			} catch (Exception e) {
			}
			this.close();
		}
	}

	@Override
	public void transfertMsgTo(PlayerName clientID, Message msg) {
		ServerCSocket sock = clients.get(clientID);
		if(sock != null){
			try{
				sock.sendToThisClient(msg);
			} catch (IOException e){
				Logger.error("srv> Failed to send msg to "+sock.getClientID()+". Closing.");
				sock.close(true);
			}
		} else {
			Logger.error("clientID "+clientID+" is unknown, failed to send msg");
			//TODO remonter l'erreur à l'UI
		}		
	}

	@Override
	public void transferMsgToAll(Message msg, PlayerName senderID) {
		for(ServerCSocket sock : clients.values()){
			if (!sock.getClientID().equals(senderID)){
				try{
					sock.sendToThisClient(msg);
				} catch (IOException e){
					Logger.error("srv> Failed to send msg to "+sock.getClientID()+". Closing.");
					sock.close(true);
				}
			}
		}		
	}
	
	public synchronized void saveRule(MsgMasterGame rule){
		this.rule = rule;
	}
	
	@Override
	public synchronized boolean areAllPlayersReady(){
		
		for(Config conf : confs.values()){
			if (!conf.isReady())
				return false;
		}
		
		return true;
	}
	
	@Override
	public synchronized void startGame() throws IllegalInitialConditionException{
		if (areAllPlayersReady()){
			
			GameType gameToStart = rule.getGameType();
			game = gameToStart.getGame();
			
			MsgMasterRule rules;
			
			if(rule instanceof MsgMasterRule){
				rules = (MsgMasterRule) rule;
			} else {
				rules = gameToStart.getRulePanel().getMsgRule(SERVER_NAME);
			}
			//try to generate the game
			game.initGame(new ArrayList<Config>(confs.values()),rules);
			
			ServerCSocket current = null;
			try{
				//Send to all players the starting conf
				for(ServerCSocket player : clients.values()){
					current = player;
					MsgStartAck msg = new MsgStartAck(gameToStart);
					//MsgGameBaseConf msg = game.getMsgGameBaseConf(player.getClientID());
					player.sendToThisClient(msg);
				}
			} catch (IOException e){
				current.close(true);	
				//alert all players that the game did not start
				String reason = "Player "+current.getClientID()+" has quit";
				this.transferMsgToAll(new MsgStartNack(reason),new PlayerName(ReservedName.SERVER_NAME));

				throw new IllegalInitialConditionException(reason);
			}

			game.start(this);
			
		} else throw new IllegalInitialConditionException("All players are not ready!");
	}

	@Override
	public synchronized void updateWR(PlayerName impactedID, Config state, ServerCSocket scs) {
			
		Config c = confs.get(impactedID);
		if (c != null){
			
			if (state instanceof ClosedConf){
				//Closing an open slot
				if (!(c instanceof EmptyConf)){
					//closing an used slot (kick)
					confs.remove(impactedID);
					ServerCSocket kickedClient = clients.remove(impactedID);
					try {
						kickedClient.sendToThisClient(new MsgReset("You have been kicked"));
					} catch (Exception e) {
						Logger.debug("kicked msg failed to send:\n\t"+e.getLocalizedMessage());
					} 
					kickedClient.close(false);
					Logger.debug("Open slot: "+openSlot);
				} else {
					//Closing an empty slot
					openSlot--;
					confs.remove(impactedID);
					Logger.debug("Open slot: "+openSlot);
				}
			} else if (state instanceof EmptyConf){
				//A player haz been kicked (slot still open)
				confs.remove(impactedID);
				try {
					this.transfertMsgTo(impactedID, new MsgReset("You have been kicked"));
				} catch (Exception e) {
					Logger.error(e.getLocalizedMessage());
				} 
				ServerCSocket kickedClient = clients.remove(impactedID);
				kickedClient.close(true);
				openSlot++;
				Logger.debug("Open slot: "+openSlot);
				
			} else {
				//Updating a player config
				c.setReady(state.isReady());
				c.setTeam(state.getTeam());
			}
		
		} else if (state instanceof EmptyConf) {
			//Open a new slot
			confs.put(impactedID, state);
			openSlot++; 
			Logger.debug("Open slot: "+openSlot);
		} else if (state instanceof ClosedConf) {
			openSlot--;
			Logger.debug("Open slot: "+openSlot);
		} else {
				Logger.error("unknown client: "+impactedID+" with state: "+state);
		}
	}

	@Override
	public synchronized Message connect(MsgConnect msg, ServerCSocket scs) {
		
		if(state.equals(ServerState.none) || state.equals(ServerState.waiting)){
			Logger.debug("State set to waiting");
			state = ServerState.waiting;
			
			if(openSlot > 0){
				//if(pwd==msg.getPwd()){
					openSlot--;
					
					List<Config> cs = new ArrayList<Config>();
					//Client config
					Config clientConf = new Config(msg.getSenderID(),true);
					cs.add(clientConf);
					
					//replaced slot
					Config conf = this.getOpenSlot();
					Logger.debug("Replacing slot "+conf.toString()+" by new player"+scs.getClientID());
					confs.remove(conf.getClientID()); 
					
					//Build other config for msgCo
					for (Entry<PlayerName,Config> c : confs.entrySet()){
							Logger.debug(c.getKey()+" : "+c.getValue().toString());
							cs.add(c.getValue());
					}
					MsgConfig msgCo = new MsgConfig(msg.getSenderID(),cs,rule,conf.getClientID());
					//Add to ref this client
					clients.put(msg.getSenderID(), scs);
					confs.put(msg.getSenderID(), clientConf);
					Logger.debug("Open slot left: "+openSlot+"\n"+msgCo.toString());
					return msgCo;
				//} else
				//	return new MsgReset("Wrong password");
				
				
			} else {
				Logger.debug("Server full, refused connection to "+msg.getSenderID());
				return new MsgReset("Server full");
			}
		} else {
			Logger.debug("Received msg but was ignored: "+msg.toString());
			return new MsgReset("Game already started");
		}		
	}

	@Override
	public synchronized void disconnect(ServerCSocket scs) {
		clients.remove(scs.getClientID()); 
		Logger.debug("Client "+scs.getClientID()+" quit");
		
		int id = -1;
		Config open;
		do {
			id++;
			open = new EmptyConf(id);
			
		} while (confs.get(open.getClientID())!=null);
		confs.remove(scs.getClientID());
		confs.put(open.getClientID(), open);
		openSlot++;
		Logger.debug("Open slot: "+openSlot);
		this.transferMsgToAll(new MsgDeco(scs.getClientID(),scs.getClientID(),id), scs.getClientID());
	}
	
	private Config getOpenSlot(){		
		for (Config s : confs.values()){
			if (s instanceof EmptyConf){
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
		this.transferMsgToAll(new MsgChat(SERVER_NAME,null,txt), null);				
	}

}
