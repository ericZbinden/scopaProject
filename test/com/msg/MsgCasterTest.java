package com.msg;

import java.util.Arrays;

import org.junit.Test;

import scopa.com.MsgScopaHand;
import scopa.logic.ScopaCard;
import util.PlayerName;

public class MsgCasterTest {

	@Test
	public void testCastMsg() throws MalformedMessageException {
		
		Message connectMsg = new MsgConnect(new PlayerName("Test"),"");
		
		MsgCaster.castMsg(MsgConnect.class, connectMsg);
		
	}
	
	@Test(expected = MalformedMessageException.class)
	public void testFailingCastMsg() throws MalformedMessageException {
		
		Message connectMsg = new MsgConnect(new PlayerName("Test"),"");
		
		MsgCaster.castMsg(MsgPlay.class, connectMsg);	
	}
	
	@Test
	public void testScopaCastMsg() throws MalformedMessageException {
		
		Message scopaMsg = new MsgScopaHand(Arrays.asList(new ScopaCard(null, null)),new PlayerName("test"));
		
		MsgCaster.castMsg(MsgScopaHand.class, scopaMsg);	
	}
	
	

}
