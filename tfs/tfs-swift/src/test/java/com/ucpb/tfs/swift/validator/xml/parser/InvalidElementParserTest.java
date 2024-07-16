package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
public class InvalidElementParserTest {

    private InvalidElementParser parser = new InvalidElementParser();

    @Test
    public void parseValidErrorMessage(){
        ValidationError error = parser.parse("cvc-type.3.1.3: The value 'POPPOPO BY NEGOTIATION' of element 'field41A' is not valid.");
        assertNotNull(error);
        System.out.println(error.getMessage());
        assertEquals("field41A",error.getTag());
        assertEquals("POPPOPO BY NEGOTIATION",error.getValue());
        assertEquals("field41A with value=POPPOPO BY NEGOTIATION is invalid.",error.getMessage());
    }


    @Test
    public void namespaceMatcher(){
        assertTrue("{\"http://www.ucpb.com.ph/tfs/schemas/swift-message\":receiver_address}".matches("\\{\".+\":(.+)}"));
    }
}
