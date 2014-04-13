package com.server.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.Message;
import com.msg.MsgCaster;
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
import com.server.api.MessageFilter;
import com.server.api.ServerConnect;
import com.server.api.ServerState;
import com.server.exceptions.IllegalInitialConditionException;

public class ServerCSocket implements Runnable {

	// Refs to the server socket.
	private Socket srv;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	// Refs to server
	private boolean isClosing;
	private ServerConnect sc;
	private MessageFilter filter;
	// Ref to client
	private PlayerName clientID;

	/**
	 * Default builder.
	 * 
	 * @param srv reference to the server that launched this thread.
	 */
	public ServerCSocket(Socket srv, ServerConnect sc, MessageFilter filter) {
		this.srv = srv;
		this.sc = sc;
		this.filter = filter;
	}

	public PlayerName getClientID() {
		return clientID;
	}

	public Socket getSocket() {
		return srv;
	}

	/**
	 * method in charge of handling the actual request from the peer. call the processPacket method if the packet is correct.
	 */
	@Override
	public void run() {

		boolean running = true;
		try {
			out = new ObjectOutputStream(srv.getOutputStream());
			in = new ObjectInputStream(srv.getInputStream());
		} catch (Exception e) {
			Logger.error("Connection lost during initial connect when srv is " + sc.getServerState() + "\n\t" + e.getLocalizedMessage());
			this.close(false);
			running = false;
		}

		try {
			while (running) {
				Object request = in.readObject();
				if (request instanceof Message) {
					processPacket((Message) request);
				}
				Logger.debug("End processPacket.");
				Logger.dot();
			}

		} catch (Exception e) {
			if (!isClosing) {
				Logger.error("scs> Connection lost during " + sc.getServerState() + "\n\t" + e.getMessage());
				e.printStackTrace();
			}
		} finally {
			if (!isClosing) {
				this.close(true);
			}
		}
	}

	/**
	 * method in charge of processing the packet sent to this server.
	 * 
	 * @param prequest the packet sent.
	 * @param out the output of the server for the feedback to the request.
	 * @param in the input of the server socket.
	 */
	public void processPacket(Message prequest) {

		if (filter.filter(prequest, sc.getServerState())) {
			return; // msg type is not expected during this state
		}

		MsgType type = prequest.getType();
		PlayerName senderID = prequest.getSenderID();
		PlayerName receiverID = prequest.getReceiverID();

		Logger.dot();
		Logger.debug("New packet received with type = " + type + " from : " + senderID + " to : " + receiverID);

		try {
			switch (type) {
			case connect:
				connect(MsgCaster.castMsg(MsgConnect.class, prequest));
				break;
			case disconnect:
				disconnect(prequest);
				break;
			case reco:
				reco(prequest);
				break;
			// case refresh:
			// refresh();
			// break;
			// case config:
			// if(prequest instanceof MsgConfig){
			// MsgConfig conf = (MsgConfig) prequest;
			// config(conf);
			// } else throw new MalformedMessageException(type);
			// break;
			case chat:
				chat(MsgCaster.castMsg(MsgChat.class, prequest));
				break;
			case wrSlot:
				wrSlot(MsgCaster.castMsg(MsgWRslot.class, prequest));
				break;
			case masterGame:
				masterGame(MsgCaster.castMsg(MsgMasterGame.class, prequest));
				break;
			case masterRule:
				masterRule(MsgCaster.castMsg(MsgMasterRule.class, prequest));
				break;
			case play:
				play(MsgCaster.castMsg(MsgPlay.class, prequest));
				break;
			case start:
				start(MsgCaster.castMsg(MsgStart.class, prequest));
				break;
			default:
				Logger.debug("Unknown msg type: " + type + ". Packet is ignored.\n\t" + prequest.toString());
			}

		} catch (MalformedMessageException e2) {
			Logger.debug(e2.getMessage() + " on msg: " + prequest.toString());
		}
	}

	private void start(MsgStart prequest) {
		try {
			// FIXME Should store players participating and fail if one
			// of them quit
			sc.transferMsgToAll(prequest, prequest.getSenderID());
			sc.startGame();
		} catch (IllegalInitialConditionException e) {
			String errorReason = "Game can not start: " + e.getLocalizedMessage();
			sc.transferMsgToAll(new MsgStartNack(errorReason), null);
		}
	}

	private void masterRule(MsgMasterRule prequest) {
		sc.saveRule(prequest);
		sc.transferMsgToAll(prequest, prequest.getSenderID());
	}

	private void masterGame(MsgMasterGame prequest) {
		sc.saveRule(prequest);
		sc.transferMsgToAll(prequest, prequest.getSenderID());
	}

	private void wrSlot(MsgWRslot prequest) {
		sc.transferMsgToAll(prequest, prequest.getSenderID());
		sc.updateWR(prequest.getImpactedID(), prequest.getConf(), this);
	}

	private void chat(MsgChat prequest) {
		sc.transferMsgToAll(prequest, prequest.getSenderID());
	}

	// private void config(MsgConfig conf) throws IOException {
	// if(state.equals(ServerState.waiting)){
	// sc.transferMsgToAll(conf, clientID);
	// } else {
	// //Ignore msg
	// }
	// }
	//
	// private void refresh(){
	// if(state.equals(ServerState.waiting)){
	//
	// } else {
	// //Ignore msg
	// }
	// }

	private void play(MsgPlay play) {
		sc.play(play);
	}

	private void reco(Message msg) {
		// TODO reco
	}

	private void connect(MsgConnect msg) {
		ServerState state = sc.getServerState();
		if (state.equals(ServerState.none) || state.equals(ServerState.waiting)) {
			this.clientID = msg.getSenderID();
			Message respond = sc.connect(msg, this);

			switch (respond.getType()) {
			case reset:
				try {
					this.sendToThisClient(respond);
					Logger.debug("Connection reset: " + ((MsgReset) respond).getReason());
				} catch (IOException e) {
					// Die silently
				}
				this.close(false);
				return;
			case config:
				try {
					MsgConfig mc = MsgCaster.castMsg(MsgConfig.class, respond);
					this.sendToThisClient(respond);
					Logger.debug("Connection accepted");
					sc.transferMsgToAll(new MsgNewPlayer(clientID, mc.getImpactedID()), msg.getSenderID());

				} catch (IOException | MalformedMessageException e) {
					Logger.error(e.getMessage());
					this.close(false);
				}
				return;
			default:
				throw new RuntimeException("Unexpected msg type: " + respond.getType());
			}
		} else {
			Logger.debug("Received msg but was ignored: " + msg.toString());
		}
	}

	public void disconnect(Message msg) {
		sc.transferMsgToAll(msg, msg.getSenderID());
	}

	@Override
	public boolean equals(Object that) {
		if (that != null && that instanceof ServerCSocket) {
			ServerCSocket scc = (ServerCSocket) that;
			if (this.getClientID().equals(scc.getClientID())) {
				return true;
			}
		}
		return false;
	}

	public void sendToThisClient(Message msg) throws IOException {
		out.writeObject(msg);
	}

	public void close(boolean sendMsgToSrv) {
		Logger.debug("Socket client: " + clientID + " closing");
		isClosing = true;

		try {
			if (sendMsgToSrv) {
				sc.disconnect(this);
			}
			in.close();
			out.close();
			srv.close();

		} catch (IOException e) {
			// Die silently
		}
	}

	@Override
	public String toString() {
		return "ServerClientSocket:\n" + "\tServerState:" + sc.getServerState() + "\n" + "Socket:" + this.clientID + "\tState:" + srv.isClosed();
	}

}
