package com.msg;

import game.GameType;

public class MsgStartAck extends Message {

	public String gameType;
	
	public MsgStartAck(GameType gameType) {
		super(MsgType.startAck, "server", null); //TODO set srv name for authentication
		this.gameType=gameType.getGameType();
	}
	
	public GameType getGameType(){
		return GameType.valueOf(gameType);
	}

}
