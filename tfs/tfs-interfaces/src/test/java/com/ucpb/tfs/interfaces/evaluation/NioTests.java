package com.ucpb.tfs.interfaces.evaluation;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import org.junit.Test;

public class NioTests {

	
	@Test
	public void testGetBytes() throws UnsupportedEncodingException{
		String input = "1";
		byte[] inputAsBytes = input.getBytes();
		assertEquals(1,input.length());
		assertEquals(1,inputAsBytes.length);
		
		String username = "username";
		byte[] usernameAsBytes = username.getBytes("CP1047");
		assertEquals(8,username.length());
		assertEquals(8,usernameAsBytes.length);
		assertEquals("username",new String(usernameAsBytes,"CP1047"));
	}
}
