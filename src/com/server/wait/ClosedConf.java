package com.server.wait;

import util.PlayerName;
import util.ReservedName;

public class ClosedConf extends Config {
	
	public ClosedConf() {
		super(new PlayerName(ReservedName.CLOSED_CONF_NAME.getName()));
	}
	
	/** Always true*/
	@Override
	public boolean isReady(){
		return true;
	}
	
	@Override
	public boolean equals(Object that){
		if(that != null && that instanceof ClosedConf){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "Closed"+super.toString();
	}
	
	@Override
	public ClosedConf clone(){
		return new ClosedConf();
	}
}
