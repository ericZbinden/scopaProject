package com.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import util.Logger;

import com.msg.MalformedMessageException;
import com.msg.Message;
import com.msg.MsgChat;
import com.msg.MsgConfig;
import com.msg.MsgConnect;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.msg.MsgNewPlayer;
import com.msg.MsgPlay;
import com.msg.MsgReset;
import com.msg.MsgStart;
import com.msg.MsgStartNack;
import com.msg.MsgType;
import com.msg.MsgWRslot;

public class ServerCSocket implements Runnable {
	
	private enum ServerState{
		none,waiting,playing,closing,closed
	}

	/**
	 * reference to the server socket.
	 */
	private Socket srv;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private ServerConnect sc;

	private String clientID;
	private ServerState state = ServerState.none;	
	
	/**
	 * Default builder.
	 * 
	 * @param srv
	 *            reference to the server that launched this thread.
	 */
	public ServerCSocket(Socket srv, ServerConnect sc) {
		this.srv = srv;
		this.sc = sc;
	}
	
	public String getClientID(){
		return clientID;
	}
	
	public Socket getSocket(){
		return srv;
	}

	/**
	 * method in charge of handling the actual request from the peer. call the
	 * processPacket method if the packet is correct.
	 */
	public void run() {

		boolean running = true;
		try {
			out = new ObjectOutputStream(srv.getOutputStream());
			in = new ObjectInputStream(srv.getInputStream());
		} catch (Exception e) {
			Logger.error("Connection lost during "+this.getState()+"\n"+e.getLocalizedMessage());
			this.close(false);
			running = false;
		}
		
		try{
			while(running){
				Object request = in.readObject();
				//Logger.debug("ServerClientSocket received msg:\n"+request.toString());
				Message prequest;
				if (request instanceof Message) {
					prequest = (Message) request;
					processPacket(prequest, out, in);
				}
				Logger.debug("End processPacket.");
				Logger.dot();
			}
			
		} catch ( SocketException se){
				Logger.error("Connection closed by client during "+this.getState()+"\n"+se.getLocalizedMessage());
		} catch (Exception e) {
				Logger.error("Connection lost during "+this.getState()+"\n"+e.getLocalizedMessage());			
		} finally {
			switch(this.getState()){
			case closed:
				break;
			case closing:
				this.close(false);
				break;
			default:
				this.close(true);
			}
			running = false;
		}
	}

	/**
	 * method in charge of processing the packet sent to this server.
	 * 
	 * @param prequest the packet sent.
	 * @param out the output of the server for the feedback to the request.
	 * @param in the input of the server socket.
	 */
	public void processPacket(Message prequest, ObjectOutputStream out,
			ObjectInputStream in) throws IOException {

		MsgType type = prequest.getType();
		String senderID = prequest.getSenderID();
		String receiverID = prequest.getReceiverID();

		Logger.dot();
		Logger.debug("Start processPacket:");
		Logger.debug("New packet received with type = " + type
				+ " from : " + senderID
				+ " to : " + receiverID);

		try{
			switch(type){
			case connect:
				if(prequest instanceof MsgConnect){
					MsgConnect conf = (MsgConnect) prequest;
					connect(conf);
				} else throw new MalformedMessageException();
				break;
			case disconnect:
				disconnect(prequest);
				break;
			case reco:
				reco(prequest);
				break;
			case refresh:
				refresh();
				break;
//			case config:
//				if(prequest instanceof MsgConfig){
//					MsgConfig conf = (MsgConfig) prequest;
//					config(conf);
//				} else throw new MalformedMessageException();
//				break;
			case chat:
				if(prequest instanceof MsgChat){
					MsgChat chat = (MsgChat) prequest;
					chat(chat);
				} else throw new MalformedMessageException();
				break;
			case wrSlot:
				if(prequest instanceof MsgWRslot){
					MsgWRslot wrSlot = (MsgWRslot) prequest;
					Logger.debug(wrSlot.toString());
					wrSlot(wrSlot);
				} else throw new MalformedMessageException();
				break;
			case masterGame:
				if(prequest instanceof MsgMasterGame){
					MsgMasterGame chat = (MsgMasterGame) prequest;
					masterGame(chat);
				} else throw new MalformedMessageException();
				break;
			case masterRule:
				if(prequest instanceof MsgMasterRule){
					MsgMasterRule chat = (MsgMasterRule) prequest;
					masterRule(chat);
				} else throw new MalformedMessageException();
				break;
			case play:
				if(prequest instanceof MsgPlay){
					MsgPlay play = (MsgPlay) prequest;
					play(play);
				} else throw new MalformedMessageException();
				break;
			case start:
				if(prequest instanceof MsgStart){
					start(prequest);
				}
			default:
				Logger.debug("Unknown msg type: "+type+". Packet is ignored.");
			}
			
		} catch (MalformedMessageException e2){
			Logger.debug(e2.getMessage()+" on msg: "+prequest.toString());
		}
	}
	
	private void start(Message prequest) throws IOException {
		if(isWaitingOrLog(prequest)){
			try{
				sc.startGame();
			} catch (IllegalInitialConditionException e){
					sc.transfertMsgTo(prequest.getSenderID(), new MsgStartNack("server"));
			}
		}
	}
	
	private void masterRule(MsgMasterRule prequest) throws IOException {
		if(isWaitingOrLog(prequest)){
			sc.saveRule(prequest);
			sc.transferMsgToAll(prequest, prequest.getSenderID());
		}
	}

	private void masterGame(MsgMasterGame prequest) throws IOException {
		if(isWaitingOrLog(prequest)){
			sc.saveRule(prequest);
			sc.transferMsgToAll(prequest, prequest.getSenderID());
		}
	}

	private void wrSlot(MsgWRslot prequest) throws IOException {
		if(isWaitingOrLog(prequest)){
			sc.transferMsgToAll(prequest, prequest.getSenderID());			
			sc.updateWR(prequest.getImpactedID(), prequest.getConf(), this);
		}			
	}

	private void chat(MsgChat prequest) throws IOException {
		sc.transferMsgToAll(prequest, prequest.getSenderID());		
	}

//	private void config(MsgConfig conf) throws IOException {
//		if(state.equals(ServerState.waiting)){
//			sc.transferMsgToAll(conf, clientID);
//		} else {
//			//Ignore msg
//		}
//	}
	
	private void refresh(){
		if(state.equals(ServerState.waiting)){
			//TODO
		} else {
			//Ignore msg
		}
	}
	
	private void play(MsgPlay play){
		//TODO serverclient should contain a Playable or an acces to an interface
	}
	
	private void reco(Message msg){
		//TODO reco
	}
	
	private void connect(MsgConnect msg){
		if(state.equals(ServerState.none) || state.equals(ServerState.waiting)){
			Logger.debug("State set to waiting");
			state = ServerState.waiting;
			this.clientID=msg.getSenderID();
			Message m = sc.connect(msg, this);
			
			if(m instanceof MsgReset){
				try {
					this.sendToThisClient(m);
					Logger.debug("Connection reset: "+((MsgReset)m).getReason());
				} catch (IOException e) {
				}
				this.close(false);
			} else {
				try {
					MsgConfig mc = (MsgConfig) m;
					this.sendToThisClient(m);
					Logger.debug("Connection accepted");
					sc.transferMsgToAll(new MsgNewPlayer(clientID,mc.getImpactedID()), msg.getSenderID());

				} catch (IOException e) {
					this.close(false);
				}
			}
		}	
	}
	
	public void disconnect(Message msg) throws IOException {
		sc.transferMsgToAll(msg, msg.getSenderID());
	}
	
	public boolean equals(Object that){
		if(that instanceof ServerCSocket){
			ServerCSocket scc = (ServerCSocket) that;
			if(scc.getClientID().equals(this.getClientID())){
				//return scc.getSocket().equals(this.getSocket());
				return true;
			}
		}
		return false;
	}
	
	public void sendToThisClient(Message msg) throws IOException{
		
		out.writeObject(msg);				
	}
	
	public void close(boolean sendMsgToSrv){
		Logger.debug("Socket client: "+clientID+" closing");

		try {
			if (sendMsgToSrv){
				sc.disconnect(this);
			}
			in.close();
			out.close();
			srv.close();
			
		} catch (IOException e) {
		}
		state = ServerState.closed;
	}
	
	public ServerState getState(){
		return state;
	}
	
	public void setClosingState(){
		state = ServerState.closing;
	}
	
	public String toString(){
		return "scs:"+state+" "+this.clientID+" "+srv.isClosed();
	}
	
	private boolean isWaitingOrLog(Message msg){
		if(state.equals(ServerState.waiting)){
			return true;
		} else {
			Logger.debug("Receive msg but ignored it in waiting phase: "+msg.toString());
			return false;
		}
	}
}
