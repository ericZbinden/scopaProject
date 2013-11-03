package com.server.wait;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import util.PlayerName;

public class Config implements Serializable, Externalizable, Cloneable {

	private static final long serialVersionUID = -8461455175877588015L;

	private PlayerName clientID;
	
	private int team = 1;
	
	private boolean ready = false;
	
	private boolean self = false;
	
	//Constructor used for externalizable
	public Config() {
		
	}
	
	public Config(PlayerName clientID) {
		this.clientID = clientID;
	}
	
	public Config(PlayerName clientID, boolean self){
		this.clientID=clientID;
		this.self=self;
	}
	
	public boolean isSelf(){
		return self;
	}
	

	public PlayerName getClientID() {
		return clientID;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public boolean isMaster(){
		return false;
	}
	
	public String toString(){
		return "Config: "+clientID+(isMaster()?"(master)":"")+(self?"(self)":"")+" on team "+team+", is ready: "+ready;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientID == null) ? 0 : clientID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Config))
			return false;
		Config other = (Config) obj;
		if (clientID == null) {
			if (other.clientID != null)
				return false;
		} else if (!clientID.equals(other.clientID))
			return false;
		return true;
	}

	@Override
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		clientID = (PlayerName) arg0.readObject();
		ready = arg0.readBoolean();
		team = arg0.readInt();
		
	}

	@Override
	public void writeExternal(ObjectOutput arg0) throws IOException {
		arg0.writeObject(clientID);
		arg0.writeBoolean(ready);
		arg0.writeInt(team);
	}
	
	public Config clone(){
		Config newConf = new Config(getClientID(),isSelf());
		newConf.setReady(isReady());
		newConf.setTeam(getTeam());
		return newConf;
	}

}
