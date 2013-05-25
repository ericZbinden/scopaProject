package com.msg;

import java.util.ArrayList;
import java.util.List;

import com.server.wait.Config;

public class MsgConfig extends Message {

	private static final long serialVersionUID = 8205787536934261663L;
	
	private List<Config> configs;
	
	private MsgMasterGame rule;
		
	private String impactedID;

	public MsgConfig(String receiverID, List<Config> configs, MsgMasterGame rule, String impactedID) {
		super(MsgType.config, null, receiverID);
		this.impactedID = impactedID;
		this.configs = configs;	
		this.rule = rule;
	}
	
//	public MsgConfig(String senderID, String receiverID, Config config, String impactedID) {
//		super(MsgType.config, senderID, receiverID);
//		this.impactedID = impactedID;
//		this.configs = new ArrayList<Config>();
//		configs.add(config);	
//	}
	
	public List<Config> getConfigs(){
		return configs;
	}
	
	public String toString(){
		return super.toString()+",\n\t"+configs.toString();
	}
	
	public String getImpactedID(){
		return impactedID;
	}
	
	public MsgMasterGame getRule(){
		return rule;
	}

}
