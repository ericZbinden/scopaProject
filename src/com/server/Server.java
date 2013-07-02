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
import com.msg.MsgConfig;
import com.msg.MsgConnect;
import com.msg.MsgDeco;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.msg.MsgReset;
import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

import util.Logger;


public class Server implements Runnable, ServerConnect {

	/** static reference to the class. */
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
	private Map<String,ServerCSocket> clients;
	private Map<String,Config> confs;
	
	// ref to game 
	private MsgMasterGame rule;
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
		clients = new HashMap<String,ServerCSocket>();
		confs = new HashMap<String,Config>();
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
	public void transfertMsgTo(String clientID, Message msg) throws IOException {
		ServerCSocket sock = clients.get(clientID);
		if(sock != null){
			sock.sendToThisClient(msg);
		} else {
			Logger.error("clientID "+clientID+" is unknown, failed to send msg");
			//TODO remonter l'erreur à l'UI
		}		
	}

	@Override
	public void transferMsgToAll(Message msg, String senderID) throws IOException {
		for(ServerCSocket sock : clients.values()){
			if (!sock.getClientID().equals(senderID)){
				sock.sendToThisClient(msg);
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
				rules = gameToStart.getRulePanel().getMsgRule("server");
			}
			//try to generate the game
			game.initGame(new ArrayList<Config>(confs.values()), (MsgMasterRule)rule);
			
			//Send to all players the starting conf
			
			//TODO

			
		} else throw new IllegalInitialConditionException("All players are not ready!");
	}

	@Override
	public synchronized void updateWR(String impactedID, Config state, ServerCSocket scs) {
			
		Config c = confs.get(impactedID);
		if (c != null){
			
			if (state instanceof ClosedConf){
				//Closing an open slot
				if (!(c instanceof EmptyConf)){
					//closing an used slot (kick)
					confs.remove(impactedID);
					ServerCSocket kickedClient = clients.remove(impactedID);
					kickedClient.setClosingState();
					try {
						kickedClient.sendToThisClient(new MsgReset("You have been kicked"));
					} catch (Exception e) {
						Logger.error(e.getLocalizedMessage());
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
		
		if(openSlot > 0){
			//if(pwd==msg.getPwd()){
				openSlot--;
				
				List<Config> cs = new ArrayList<Config>();
				//Client config
				Config clientConf = new Config(msg.getSenderID(),true);
				cs.add(clientConf);
				
				//replaced slot
				Config conf = this.getOpenSlot();
				System.out.println(conf.toString());
				confs.remove(conf.getClientID()); 
				
				//Other config
				for (Entry<String,Config> c : confs.entrySet()){
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
		confs.put(open.getClientID(), open);
		openSlot++;
		try {
			this.transferMsgToAll(new MsgDeco(scs.getClientID(),scs.getClientID(),id), scs.getClientID());
		} catch (IOException e) {
			Logger.error("Unable to transmit msg to all");
		}
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

}
