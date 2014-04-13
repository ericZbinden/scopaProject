package scopa.com;

import com.msg.MalformedMessageException;
import com.msg.MsgPlay;

public interface ScopaClient {

	public void update(MsgPlay msg) throws MalformedMessageException;

}
