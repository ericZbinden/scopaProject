package com.msg;

public enum MsgType {
	//Connect
	connect,reco,reset,
	
	//Waiting
	wrSlot,masterGame,masterRule,newPlayer,
	config,refresh,start,startNack,
	
	//game
	gameBaseConf,play,
	
	//misc
	chat,disconnect
}
