package com.server.wait;

public class EmptyConf extends Config {

	public static final String ID = "Open";
	
	public EmptyConf() {
		super(ID);
	}
	
	public EmptyConf(int i){
		super(ID+i);
	}
	
	public EmptyConf(String impactedID){
		super(impactedID);
	}
	
	/** Always true*/
	@Override
	public boolean isReady(){
		return true;
	}
	
	public boolean equals(Object that){
		if(that != null && that instanceof EmptyConf){
			return this.getClientID().equals(((EmptyConf)that).getClientID());
		}
		return false;
	}
	
	public String toString(){
		return "Empty"+super.toString();
	}
	
	@Override
	public EmptyConf clone(){
		return new EmptyConf(getClientID());
	}
	
}