package com.server.impl;

import util.Logger;

import com.msg.Message;
import com.server.api.MessageFilter;
import com.server.api.ServerState;

public class DefaultMessageFilter implements MessageFilter {

	@Override
	public boolean filter(Message msg, ServerState currentState) {

		switch (currentState) {
		case playing:
			return filterWhilePlaying(msg);
		case waiting:
			return filterWhileWaiting(msg);
		case none:
			return filterWhileNone(msg);
		default:
			Logger.debug("Msg filtered during " + currentState + ": " + msg.toString());
			return true;
		}

	}

	private boolean filterWhileNone(Message msg) {
		switch (msg.getType()) {
		case connect:
			return false;
		default:
			Logger.debug("Msg filtered during " + ServerState.none + ": " + msg.toString());
			return true;
		}
	}

	private boolean filterWhilePlaying(Message msg) {
		switch (msg.getType()) {
		case reco:
		case reset:
		case startNack:
		case gameBaseConf:
		case play:
		case chat:
			return false;
		case start:
		case config:
		case startAck:
		case disconnect:
		case masterGame:
		case masterRule:
		case newPlayer:
		case wrSlot:
		case connect:
		default:
			Logger.debug("Msg filtered during " + ServerState.playing + ": " + msg.toString());
			return true;
		}
	}

	private boolean filterWhileWaiting(Message msg) {
		switch (msg.getType()) {
		case start:
		case config:
		case startAck:
		case disconnect:
		case masterGame:
		case masterRule:
		case newPlayer:
		case wrSlot:
		case connect:
		case chat:
			return false;
		case reset:
		case startNack:
		case gameBaseConf:
		case play:
		case reco:
		default:
			Logger.debug("Msg filtered during " + ServerState.playing + ": " + msg.toString());
			return true;
		}
	}

}
