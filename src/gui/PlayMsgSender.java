package gui;

import com.msg.MsgPlay;

public interface PlayMsgSender {
	
	public void sendMsgPlay(MsgPlay msg);
	
	public String getLocalClient();


}
