package com.server.impl;

import com.msg.Message;
import com.server.api.MessageFilter;
import com.server.api.ServerState;

public class EmptyMessageFilter implements MessageFilter {

	@Override
	public boolean filter(Message msg, ServerState currentState) {
		return false;
	}

}
