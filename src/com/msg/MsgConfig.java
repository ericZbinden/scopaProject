package com.msg;

import java.util.List;

import util.PlayerName;

import com.server.wait.Config;

public class MsgConfig extends Message {

	private static final long serialVersionUID = 8205787536934261663L;
	
	private List<Config> configs;
	
	private MsgMasterGame rule;
		
	private String impactedID;

	public MsgConfig(PlayerName receiverID, List<Config> configs, MsgMasterGame rule, PlayerName impactedID) {
		super(MsgType.config, null, receiverID);
		
		if(impactedID == null)
			throw new IllegalArgumentException("Impacted player can not be null");
		
		this.impactedID = impactedID.getName();
		this.configs = configs;	
		this.rule = rule;
	}
	
	public List<Config> getConfigs(){
		return configs;
	}
	
	public String toString(){
		return super.toString()+",\n\t"+configs.toString();
	}
	
	public PlayerName getImpactedID(){
		return new PlayerName(impactedID);
	}
	
	public MsgMasterGame getRule(){
		return rule;
	}

}
