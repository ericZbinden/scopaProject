package scopa.com;

import com.msg.MalformedMessageException;
import com.msg.MsgType;

public class MalformedScopaMessageException extends MalformedMessageException {

	private final ScopaMsgType scopaType;
	
	public MalformedScopaMessageException(ScopaMsgType scopaType) {
		super(MsgType.play);
		this.scopaType=scopaType;
	}

	public String getMessage(){
		return DEFAULT_ERR_MSG+". Message type and subtype are not ["+type+", "+scopaType+"]";
	}

}
