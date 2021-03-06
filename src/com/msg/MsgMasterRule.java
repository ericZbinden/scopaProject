package com.msg;

import util.PlayerName;
import game.GameType;

/**
 * Abstract class used as a base for all MsgMasterRule
 * Implement your own msg on the top of this
 * @author Coubii
 *
 */
public abstract class MsgMasterRule extends MsgMasterGame {

	private static final long serialVersionUID = -863958308972918195L;
	
	protected MsgMasterRule(GameType relatedGame, PlayerName senderID) {
		super(MsgType.masterRule,relatedGame,senderID);
	}
	
}
