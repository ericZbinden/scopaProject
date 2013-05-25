package com.msg;

import game.GameType;

public class MsgMasterGame extends Message {

	private static final long serialVersionUID = -6174607564806419661L;
	private GameType type;
	
	public MsgMasterGame(GameType type, String senderId) {
		super(MsgType.masterGame,senderId,null);
		this.type=type;
	}
	
	protected MsgMasterGame(MsgType mType,GameType type, String senderId){
		super(mType,senderId,null);
		this.type = type;
	}
	
	public GameType getGameType(){
		return type;
	}


}
