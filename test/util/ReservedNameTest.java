package util;

import org.junit.Test;

import junit.framework.Assert;

public class ReservedNameTest{

	@Test
	public void testFromString_ReservedName(){
		
		Assert.assertEquals(ReservedName.EMPTY_CONF_NAME,
				ReservedName.fromString(ReservedName.EMPTY_CONF_NAME.getName()));
		Assert.assertEquals(ReservedName.SERVER_NAME,
				ReservedName.fromString(ReservedName.SERVER_NAME.getName()));
		Assert.assertEquals(ReservedName.CLOSED_CONF_NAME,
				ReservedName.fromString(ReservedName.CLOSED_CONF_NAME.getName()));
		Assert.assertEquals(ReservedName.UNKNOWN_NAME,
				ReservedName.fromString(ReservedName.UNKNOWN_NAME.getName()));
		Assert.assertEquals(ReservedName.EMPTY_NAME,
				ReservedName.fromString(ReservedName.EMPTY_NAME.getName()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromString_NotReserved(){
		ReservedName.fromString("Coubii");
	}
	
	@Test
	public void testIsReserved_ReservedName(){
		
		Assert.assertEquals(true, ReservedName.isReserved(ReservedName.EMPTY_CONF_NAME.getName()));
		Assert.assertEquals(true, ReservedName.isReserved(ReservedName.SERVER_NAME.getName()));
		Assert.assertEquals(true, ReservedName.isReserved(ReservedName.CLOSED_CONF_NAME.getName()));
		Assert.assertEquals(true, ReservedName.isReserved(ReservedName.UNKNOWN_NAME.getName()));
		Assert.assertEquals(true, ReservedName.isReserved(ReservedName.EMPTY_NAME.getName()));
	}
	
	@Test
	public void testIsReserved_NotReserved(){
		
		Assert.assertEquals(false, ReservedName.isReserved("Coubii"));
		Assert.assertEquals(false, ReservedName.isReserved("Open1")); //TODO find a way to find a reservedName
		Assert.assertEquals(false, ReservedName.isReserved("Closed ")); //TODO trim to catch reservedName
	}
	

}
