package com.msg;

import game.GameType;

public abstract class MsgGameBaseConf extends Message {
	
	private String gameType;
	
	protected MsgGameBaseConf(GameType gameType, String senderID) {
		super(MsgType.gameBaseConf, senderID,null);
		this.gameType = gameType.getGameType();
	}

	public GameType getGameType() {
		return GameType.valueOf(gameType);
	}


}
