package com.ucpb.tfs.domain.service.utils;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import com.ucpb.tfs.domain.service.utils.SwiftMessageSenderUtils;


import org.junit.Test;

public class SwiftMessageSenderUtilsTest {

	@Test
	public void successfullyCallGetExistingBank() {
		assertEquals("UCPBPHMM", SwiftMessageSenderUtils.getExistingBank("UCPBPHMM", ""));
		assertEquals("UCPBPHMM", SwiftMessageSenderUtils.getExistingBank("", "UCPBPHMM"));		
		assertEquals("UCPBPHMM", SwiftMessageSenderUtils.getExistingBank("UCPBPHMM", null));		
		assertEquals("UCPBPHMM", SwiftMessageSenderUtils.getExistingBank(null, "UCPBPHMM"));		
	}
	
	@Test
	public void testIsNotEmpty(){
		assertEquals(true,SwiftMessageSenderUtils.isNotEmpty(null,""," ","test"));
		assertEquals(true,SwiftMessageSenderUtils.isNotEmpty(null,""," ",false));
		assertEquals(true,SwiftMessageSenderUtils.isNotEmpty(null,""," ",1234));
		assertEquals(true,SwiftMessageSenderUtils.isNotEmpty(null,""," ",new BigDecimal("10.00")));
		assertEquals(false,SwiftMessageSenderUtils.isNotEmpty(null,"","   "));
	}
	
	@Test
	public void testGetExistingValue(){
		assertEquals("TEST",SwiftMessageSenderUtils.getExistingValue(null,"TEST"));
		assertEquals("123",SwiftMessageSenderUtils.getExistingValue(null,123));
		assertEquals("123",SwiftMessageSenderUtils.getExistingValue(null,new BigDecimal("123")));
		
		assertEquals("TEST",SwiftMessageSenderUtils.getExistingValue("TEST",null));
		assertEquals("123",SwiftMessageSenderUtils.getExistingValue(123,null));
		assertEquals("123",SwiftMessageSenderUtils.getExistingValue(new BigDecimal("123"),null));
		
		assertEquals("TEST",SwiftMessageSenderUtils.getExistingValue("TEST",null,"TEST2"));
		
		assertEquals("",SwiftMessageSenderUtils.getExistingValue(" ",null,""));
		
	}
}
