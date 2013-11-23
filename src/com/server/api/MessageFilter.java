package com.server.api;

import com.msg.Message;

public interface MessageFilter {

	/**
	 * Return if this message should be filtered, depending on the current state
	 * @param msg
	 * @param currentState
	 * @return
	 */
	public boolean filter(Message msg, ServerState currentState);

}
