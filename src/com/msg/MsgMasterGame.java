package com.msg;

import util.PlayerName;
import game.GameType;

public class MsgMasterGame extends Message {

	private static final long serialVersionUID = -6174607564806419661L;
	private String type;
	
	public MsgMasterGame(GameType type, PlayerName senderId) {
		super(MsgType.masterGame,senderId,null);
		this.type=type.getGameType();
	}
	
	protected MsgMasterGame(MsgType mType,GameType type, PlayerName senderId){
		super(mType,senderId,null);
		this.type = type.getGameType();
	}
	
	public GameType getGameType(){
		return GameType.valueOf(type);
	}


}
