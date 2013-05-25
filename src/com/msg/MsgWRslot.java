package com.msg;

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

	public MsgWRslot(Config conf, String sender) {
		super(MsgType.wrSlot,sender,null);
		this.team = conf.getTeam();
		this.ready = conf.isReady();
		this.confType = 2;
		this.impactedId=conf.getClientID();
		this.newId=conf.getClientID();
	}
	
	public MsgWRslot(ClosedConf conf, String sender, String impactedId){
		super(MsgType.wrSlot,sender,null);
		this.team = conf.getTeam();
		this.ready = conf.isReady();
		this.confType = 0;
		this.impactedId=impactedId;
		this.newId=conf.getClientID();
	}
	
	public MsgWRslot(EmptyConf conf, String sender, String impactedId){
		super(MsgType.wrSlot,sender,null);
		this.team = conf.getTeam();
		this.ready = conf.isReady();
		this.confType = 1;
		this.impactedId=impactedId;
		this.newId=conf.getClientID();
	}
	
	public Config getConf(){
		switch(confType){
		case 0: return new ClosedConf();
		case 1: return new EmptyConf(newId);
		case 2: 
			Config conf = new Config(impactedId,false);
			conf.setTeam(team);
			conf.setReady(ready);
			
			return conf;
		}
		return null;
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public String getImpactedID(){
		return impactedId;
	}
	
	public int getTeam(){
		return team;
	}
	
	public String toString(){
		return super.toString()+"\nid: "+this.getImpactedID()
				+"is now "+newId+" ready: "+this.isReady()+", team: "+this.getTeam()+" / Conf:"+this.getConf();
	}

}
