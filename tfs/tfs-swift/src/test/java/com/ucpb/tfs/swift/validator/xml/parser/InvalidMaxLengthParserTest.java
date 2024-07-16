package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
public class InvalidMaxLengthParserTest {

    private InvalidMaxLengthParser parser = new InvalidMaxLengthParser();

    @Test
    public void parseValidErrorMessage(){
        ValidationError error = parser.parse("cvc-maxLength-valid: Value 'AAAAAAAAAA000000000044444' with length = '25' is not facet-valid with respect to maxLength '24' for type 'field40A'.");
        assertNotNull(error);
        assertEquals("40A",error.getTag());
        assertEquals("AAAAAAAAAA000000000044444",error.getValue());
        assertEquals("field40A with length value=25 is invalid because it does not comply to the maximum required length value=24",error.getMessage());
    }
}
