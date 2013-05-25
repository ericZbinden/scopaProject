package com.server.wait;

public class ClosedConf extends Config {

	public static final String ID = "Closed";
	
	public ClosedConf() {
		super(ID);
	}
	
	/** Always true*/
	@Override
	public boolean isReady(){
		return true;
	}
	
	public boolean equals(Object that){
		if(that != null && that instanceof ClosedConf){
			return true;
		}
		return false;
	}
	
	public String toString(){
		return "Closed"+super.toString();
	}
	
	@Override
	public ClosedConf clone(){
		return new ClosedConf();
	}
}
