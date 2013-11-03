package com.msg;

import com.server.Server;

import game.GameType;

public class MsgStartAck extends Message {

	private static final long serialVersionUID = 2292479819403955597L;
	
	public String gameType;
	
	public MsgStartAck(GameType gameType) {
		super(MsgType.startAck, Server.SERVER_NAME, null);
		this.gameType=gameType.getGameType();
	}
	
	public GameType getGameType(){
		return GameType.valueOf(gameType);
	}

}
