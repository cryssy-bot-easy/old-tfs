package com.ucpb.tfs.swift.validator.enums;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.ucpb.tfs.swift.validator.enums.MessageTypeFormat;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

public class MessageTypeFormatTest {
	
	@Test
	public void successfullyReturnMessageFormatString(){
		String messageFormatField41A="4!a2!a2!c[3!c]\n14x";
		System.out.println(messageFormatField41A);
		assertEquals(messageFormatField41A,MessageTypeFormat.valueOf("field41A").toString());
	}
}
