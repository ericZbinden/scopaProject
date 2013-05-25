package com.msg;

import game.GameType;

public class MsgPlay extends Message {
	
	private static final long serialVersionUID = -5517220379168139748L;
	
	private GameType gameType;
	
	public MsgPlay(){
		super(MsgType.play);
	}
	
	public MsgPlay(GameType game,String senderID){
		super(MsgType.play,senderID,"server");
	}
	
	public GameType getGameType(){
		return gameType;
	}
	
	public String toString(){
		return super.toString()+"\n\tGame: "+gameType;
	}

}
