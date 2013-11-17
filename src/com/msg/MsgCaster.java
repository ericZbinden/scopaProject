package com.msg;

public class MsgCaster {
	
	public static <T extends Message> T castMsg(Class<? extends T> expectedClass, Message msg) throws MalformedMessageException{
		
		if(msg == null){
			throw new MalformedMessageException();
		}
		
		try {
			return expectedClass.cast(msg);
		} catch (ClassCastException e) {
			throw new MalformedMessageException("Expected class: "+expectedClass.getSimpleName()+" but was: "+msg.getClass().getSimpleName());
		}
	}

}
