package com.server.wait;

import util.PlayerName;
import util.ReservedName;

public class EmptyConf extends Config {
	
	public static final ReservedName EMPTY_CONF_NAME = ReservedName.EMPTY_CONF_NAME;
	
	public EmptyConf() {
		this(0);
	}
	
	public EmptyConf(int i){
		super(new PlayerName(EMPTY_CONF_NAME.getName()+i));
	}
	
	public EmptyConf(PlayerName impactedID){
		super(impactedID);
	}
	
	/** Always true*/
	@Override
	public boolean isReady(){
		return true;
	}
	
	public String toString(){
		return "Empty"+super.toString();
	}
	
	@Override
	public EmptyConf clone(){
		return new EmptyConf(getClientID());
	}
	
}