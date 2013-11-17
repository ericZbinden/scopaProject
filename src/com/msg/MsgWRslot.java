package com.msg;

import util.PlayerName;

import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

public class MsgWRslot extends Message {

	private static final long serialVersionUID = 5344449282556607881L;
	private final int confType;
	private final int team;
	private final boolean ready;
	private final String impactedId;
	private final String newId;

	public MsgWRslot(Config conf, PlayerName sender) {
		this(conf,sender,conf.getClientID());
	}
	
	public MsgWRslot(Config conf, PlayerName sender, PlayerName impactedId){
		super(MsgType.wrSlot,sender,null);
		
		this.team = conf.getTeam();
		this.ready = conf.isReady();
		if(conf instanceof EmptyConf){
			this.confType = 1;
		} else if (conf instanceof ClosedConf){
			this.confType = 0;
		} else {
			this.confType = 2;
		}
		this.impactedId=impactedId.getName();
		this.newId=conf.getClientID().getName();		
	}
	
	public Config getConf(){
		switch(confType){
		case 0: 
			return new ClosedConf();
		case 1: 
			return new EmptyConf(new PlayerName(newId));
		case 2: 
			Config conf = new Config(new PlayerName(impactedId),false);
			conf.setTeam(team);
			conf.setReady(ready);
			return conf;		
		default:
			throw new RuntimeException("Expected integer in [0;2] but was: "+confType);
		}
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public PlayerName getImpactedID(){
		return new PlayerName(impactedId);
	}
	
	public int getTeam(){
		return team;
	}
	
	@Override
	public String toString(){
		return super.toString()+"\nid: "+this.getImpactedID()
				+"is now "+newId+" ready: "+this.isReady()+", team: "+this.getTeam()+" / Conf:"+this.getConf();
	}
	

}
