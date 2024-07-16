package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
public class DefaultErrorMessageParserTest {

    private DefaultErrorMessageParser parser = new DefaultErrorMessageParser();

    @Test
    public void parseRandomString(){
        ValidationError error = parser.parse("the QuiCk br0wn f0x jUmPs 0v3r tH3 l@zY D06.");
        assertNotNull(error);
        assertEquals("the QuiCk br0wn f0x jUmPs 0v3r tH3 l@zY D06.",error.getMessage());
    }

    @Test
    public void parseXsdErrorMessage(){
        ValidationError error = parser.parse("cvc-pattern-valid: Value '' is not facet-valid with respect to pattern '(/[A-Z])?(/[a-zA-Z0-9/\\-?:().,'+{}\\s]{1,34})?(([a-zA-Z0-9/\\-?:().,'+{}\\s]{1,35}){1,4})' for type 'field53D'.");
        assertNotNull(error);
        assertEquals("cvc-pattern-valid: Value '' is not facet-valid with respect to pattern '(/[A-Z])?(/[a-zA-Z0-9/\\-?:().,'+{}\\s]{1,34})?(([a-zA-Z0-9/\\-?:().,'+{}\\s]{1,35}){1,4})' for type 'field53D'.",error.getMessage());
    }

}
