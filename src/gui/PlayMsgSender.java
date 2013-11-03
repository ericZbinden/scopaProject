package gui;

import util.PlayerName;

import com.msg.MsgPlay;

public interface PlayMsgSender {
	
	public void sendMsgPlay(MsgPlay msg);
	
	public PlayerName getLocalClient();


}
