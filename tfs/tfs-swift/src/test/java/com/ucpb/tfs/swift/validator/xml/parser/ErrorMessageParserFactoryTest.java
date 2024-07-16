package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.xml.XmlErrorCode;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class ErrorMessageParserFactoryTest {


    @Test
    public void getInvalidPatternParserForInvalidPatternErrorCode(){
        ErrorMessageParser parser = ErrorMessageParserFactory.getInstance("cvc-pattern-valid: Value '' is not facet-valid with respect to pattern '(/[A-Z])?(/[a-zA-Z0-9/\\-?:().,'+{}\\s]{1,34})?(([a-zA-Z0-9/\\-?:().,'+{}\\s]{1,35}){1,4})' for type 'field53D'.");
        assertEquals(InvalidPatternParser.class,parser.getClass());
    }
}
