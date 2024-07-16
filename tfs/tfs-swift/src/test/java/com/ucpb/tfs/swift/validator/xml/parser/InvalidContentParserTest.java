package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 */
public class InvalidContentParserTest {

    private InvalidContentParser parser = new InvalidContentParser();

    @Test
    public void successfullyParseValidErrorMessage(){
        String errorMessage = "cvc-complex-type.2.4.a: Invalid content was found starting with element 'field21'. One of '{\"http://www.ucpb.com.ph/tfs/schemas/mt4series\":field20}' is expected.";
        ValidationError error = parser.parse(errorMessage);

        assertNotNull(error);
        assertEquals("field20",error.getTag());
        assertNull(error.getValue());
        assertEquals("Invalid content found around field21, field20 is expected.",error.getMessage());
    }

    @Test
    public void successfullyParseValidErrorMessageWithNoNamespace(){
        String errorMessage = "cvc-complex-type.2.4.a: Invalid content was found starting with element 'field21'. One of 'field20' is expected.";
        ValidationError error = parser.parse(errorMessage);

        assertNotNull(error);
        assertEquals("field20",error.getTag());
        assertNull(error.getValue());
        assertEquals("Invalid content found around field21, field20 is expected.",error.getMessage());
    }

    @Test
    public void successfullyParseInvalidFieldWithNamespace(){
        String errorMessage = "cvc-complex-type.2.4.a: Invalid content was found starting with element 'ns3:message_priority'. One of '{\"http://www.ucpb.com.ph/tfs/schemas/swift-message\":receiver_address}' is expected.";

        ValidationError error = parser.parse(errorMessage);
        assertEquals("receiver_address",error.getTag());
        assertEquals(null,error.getValue());
        assertEquals("Invalid content found around message_priority, receiver_address is expected.",error.getMessage());
    }




}
