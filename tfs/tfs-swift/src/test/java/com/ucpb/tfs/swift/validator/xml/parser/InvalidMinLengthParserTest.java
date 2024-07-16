package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
public class InvalidMinLengthParserTest {

    private InvalidMinLengthParser parser = new InvalidMinLengthParser();

    @Test
    public void parseValidErrorMessage(){
        ValidationError error = parser.parse("cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'field44E'.");
        assertNotNull(error);
        assertEquals("44E",error.getTag());
        assertEquals("",error.getValue());
        assertEquals("field44E with length value=0 is invalid because it does not comply to the minimum required length value=1",error.getMessage());
    }
}
