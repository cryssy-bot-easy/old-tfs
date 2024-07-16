package com.ucpb.tfs.swift.message;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 */
public class RawSwiftMessageTest {

    private RawSwiftMessage message;


    @Test
    public void successfullyGetTheSequenceNumber(){
        message = new RawSwiftMessage();
        message.addTag("27","1/29");

        assertEquals(Integer.valueOf(1),message.getSequenceNumber());
    }

    @Test
    public void successfullyGetTheSequenceTotal(){
        message = new RawSwiftMessage();
        message.addTag("27","1/29");

        assertEquals(Integer.valueOf(29),message.getSequenceTotal());
    }

    @Test
    public void nullSequenceNumberOnEmptyField27(){
        message = new RawSwiftMessage();
        assertNull(message.getSequenceNumber());
        assertNull(message.getSequenceTotal());
    }

    @Test
    public void returnNullOnEmptyLtIdentifier(){
        message = new RawSwiftMessage();
        assertNull(message.getMessageSender());
    }

    @Test
    public void returnMessageSenderFromLtIdentifier(){
        message = new RawSwiftMessage();
        BasicHeader basicHeader = new BasicHeader();
        basicHeader.setLtIdentifier("ABCDEFGHIJKLMNOPQRST");
        message.setBasicHeader(basicHeader);

        assertEquals("ABCDEFGH",message.getMessageSender());
    }


}
