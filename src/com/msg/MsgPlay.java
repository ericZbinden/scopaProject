package com.msg;

import game.GameType;

public abstract class MsgPlay extends Message {
	
	private static final long serialVersionUID = -5517220379168139748L;
	
	private String gameType;
	
	protected MsgPlay(GameType game){
		this(game,null,null);
	}
	
	protected MsgPlay(GameType game,String senderID,String receiverID){
		super(MsgType.play,senderID,receiverID);		
		this.gameType=game.toString();
	}
	
	public GameType getGameType(){
		return GameType.valueOf(gameType);
	}
	
	public String toString(){
		return super.toString()+"\n\tGame: "+gameType;
	}

}
