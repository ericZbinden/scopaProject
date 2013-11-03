package util;

import java.io.Serializable;

public class PlayerName implements Serializable{

	private static final long serialVersionUID = -4808847924983614453L;
	
	private String playerName;
	
	public PlayerName(String name) {
		this.playerName = name;
	}
	
	public PlayerName(ReservedName name){
		this.playerName = name.getName();
	}
	
	public String getName(){
		return playerName;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((playerName == null) ? 0 : playerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerName other = (PlayerName) obj;
		if (playerName == null) {
			if (other.playerName != null)
				return false;
		} else if (!playerName.equals(other.playerName))
			return false;
		return true;
	}
	
	

}
