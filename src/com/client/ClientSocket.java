package com.client;

import game.GameType;
import gui.api.ControlPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.Message;
import com.msg.MsgCaster;
import com.msg.MsgChat;
import com.msg.MsgDeco;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.msg.MsgNewPlayer;
import com.msg.MsgPlay;
import com.msg.MsgReco;
import com.msg.MsgReset;
import com.msg.MsgStart;
import com.msg.MsgStartAck;
import com.msg.MsgStartNack;
import com.msg.MsgType;
import com.msg.MsgWRslot;
import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

public class ClientSocket implements Runnable {

	private List<MsgType> filteredWhilePlayingType = Arrays.asList(MsgType.chat, MsgType.disconnect, MsgType.play, MsgType.reset);
	private List<MsgType> filteredWhileWaitingType = Arrays.asList(MsgType.chat, MsgType.disconnect, MsgType.masterGame,//
			MsgType.masterRule, MsgType.newPlayer, MsgType.start, MsgType.wrSlot, MsgType.reset);
	private List<MsgType> filteredWhileStartingType = Arrays.asList(MsgType.chat, MsgType.disconnect, MsgType.startAck,//
			MsgType.startNack, MsgType.reset);

	private Socket sock;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ControlPanel cp;
	private boolean isClosing = false;

	public ClientSocket(Socket sock, ObjectInputStream in, ObjectOutputStream out, ControlPanel cp) {
		this.sock = sock;
		this.in = in;
		this.out = out;
		this.cp = cp;
	}

	/*********************** SEND MSG ****************************/

	public void sendChatMsg(PlayerName id, String txt) {
		this.sendOrClose(new MsgChat(id, txt));
	}

	public void sendStartMsg() {
		this.sendOrClose(new MsgStart(cp.getClientID()));
	}

	public void sendWrSlotMsg(Config conf, PlayerName impactedID) {
		MsgWRslot msg;
		if (conf instanceof ClosedConf) {
			msg = new MsgWRslot(conf, cp.getClientID(), impactedID);
		} else if (conf instanceof EmptyConf) {
			msg = new MsgWRslot(conf, cp.getClientID(), impactedID);
		} else {
			msg = new MsgWRslot(conf, cp.getClientID());
		}
		this.sendOrClose(msg);
	}

	public void sendGameMsg(GameType gameType) {
		MsgMasterGame msg = new MsgMasterGame(gameType, cp.getClientID());
		this.sendOrClose(msg);
	}

	public void sendRuleMsg(MsgMasterRule msg) {
		this.sendOrClose(msg);
	}

	public void sendPlayMsg(MsgPlay msg) {
		this.sendOrClose(msg);
	}

	/*********************** CONCREAT SEND ******************************/

	/**
	 * Send the message with succes or close the socket
	 * @param msg
	 */
	private void sendOrClose(Message msg) {
		try {
			sendMsg(msg);
		} catch (IOException e) {
			ioError(e, msg);
		}
	}

	private void sendMsg(Message msg) throws IOException {
		out.writeObject(msg);
		Logger.debug("ClientSocket sended:\n" + msg);
	}

	/**
	 * Unrecoverable error, close socket
	 * @param e
	 * @param msg
	 */
	private void ioError(IOException e, Message msg) {
		Logger.error("Unable to send msg: " + msg.toString() + "\nCaused by: " + e.getLocalizedMessage());
		e.printStackTrace();
		// kill
		this.close();
	}

	public void close() {
		this.setClosing();
		try {
			in.close();
			out.close();
			sock.close();
		} catch (Exception e) {
			// die silently
		}
	}

	/************************* MISC *******************************/

	public void setControlPanel(ControlPanel cp) {
		this.cp = cp;
	}

	public void setClosing() {
		isClosing = true;
	}

	/**************** HANDLER RECEIVING MSG ************************/

	@Override
	public void run() {
		boolean running = true;
		while (running) {

			try {

				Logger.debug("client " + cp.getClientID().getName() + " blocking waiting new Msg");
				Message msg = (Message) in.readObject();

				try {
					if (filterMsg(msg)) {
						process(msg);
					} else {
						Logger.debug("client " + cp.getClientID() + " received msg but was ignored during " + cp.getServerState() + ":\n\t"
								+ msg.toString());
					}
				} catch (MalformedMessageException mme) {
					Logger.error("Error during processing msg, just ignoring it: " + mme.getMessage());
				}

			} catch (ClassNotFoundException cnfe) {
				Logger.error("Malformed msg: " + cnfe.getMessage());
			} catch (IOException e) {
				if (isClosing) {
					Logger.debug("socket closing by server: " + e.getMessage());
					// closing procedure started, ui will dipose itself
				} else {
					Logger.debug("Unexpected socket closed by server: " + e.getMessage());
					// unexpected procedure, tell ui to dispose
					cp.serverDown();
				}
				running = false;
			} finally {
				if (!running) {
					this.close();
				}
			}
		}
	}

	private boolean filterMsg(Message msg) {
		MsgType type = msg.getType();
		if (cp.isPlaying()) {
			return filteredWhilePlayingType.contains(type);
		} else if (cp.isStarting()) {
			return filteredWhileStartingType.contains(type);
		} else if (cp.isWaiting()) {
			return filteredWhileWaitingType.contains(type);
		} else {
			// Closed ?
			return false;
		}
	}

	private void process(Message msg) throws MalformedMessageException {
		Logger.debug("ClientSocket " + cp.getClientID() + " received:\n\t" + msg.toString());

		switch (msg.getType()) {
		case wrSlot:
			wrSlot(MsgCaster.castMsg(MsgWRslot.class, msg));
			break;
		case masterGame:
			masterGame(MsgCaster.castMsg(MsgMasterGame.class, msg));
			break;
		case masterRule:
			masterRule(MsgCaster.castMsg(MsgMasterRule.class, msg));
			break;
		case start:
			start(MsgCaster.castMsg(MsgStart.class, msg));
			break;
		case startNack:
			startNack(MsgCaster.castMsg(MsgStartNack.class, msg));
			break;
		case startAck:
			startAck(MsgCaster.castMsg(MsgStartAck.class, msg));
			break;
		case chat:
			chat(MsgCaster.castMsg(MsgChat.class, msg));
			break;
		case play:
			play(MsgCaster.castMsg(MsgPlay.class, msg));
			break;
		case newPlayer:
			newPlayer(MsgCaster.castMsg(MsgNewPlayer.class, msg));
			break;
		case reco:
			reco(MsgCaster.castMsg(MsgReco.class, msg));
			break;
		case reset:
			reset(MsgCaster.castMsg(MsgReset.class, msg));
			break;
		case disconnect:
			disconnect(MsgCaster.castMsg(MsgDeco.class, msg));
			break;
		case refresh:
		default:
			String error = "Unknown msg type: " + msg.getType();
			Logger.error(error);
			throw new MalformedMessageException(error);
		}
	}

	private void reco(MsgReco msg) {
		cp.reco(msg);
	}

	private void chat(MsgChat msg) {
		if (cp.isPlaying()) {
			cp.chatDuringPlay(msg.getSenderID(), msg.getText());
		} else {
			cp.chat(msg.getSenderID(), msg.getText());
		}
	}

	private void wrSlot(MsgWRslot msg) {
		PlayerName impactedID = msg.getImpactedID();
		Config newConf = msg.getConf();
		cp.wrSlot(impactedID, newConf);
	}

	private void masterGame(MsgMasterGame msg) {
		cp.masterGame(msg.getGameType());
	}

	private void masterRule(MsgMasterRule msg) {
		cp.masterRule(msg);
	}

	private void start(MsgStart msg) {
		cp.start();
	}

	private void startNack(MsgStartNack msg) {
		cp.startNack(msg.getReason());
	}

	private void startAck(MsgStartAck msg) {
		cp.startAck(msg.getGameType());
	}

	private void play(MsgPlay msg) {
		cp.play(msg);
	}

	private void newPlayer(MsgNewPlayer msg) {
		PlayerName newP = msg.getNewPlayerID();
		cp.newPlayer(msg.getOpenID(), newP);
		cp.chatFromServer(newP.getName() + " haz connect.");
	}

	private void reset(MsgReset msg) {
		cp.reset(msg.getReason());
	}

	private void disconnect(MsgDeco msg) {
		PlayerName decoId = msg.getDecoClient();
		cp.disconnect(decoId, msg.getEmptyID());
		cp.chatFromServer(decoId.getName() + " haz quit.");
	}
}
