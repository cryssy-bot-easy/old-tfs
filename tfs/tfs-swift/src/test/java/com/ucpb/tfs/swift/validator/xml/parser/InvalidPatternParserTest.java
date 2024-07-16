package com.ucpb.tfs.swift.validator.xml.parser;

import com.thaiopensource.relaxng.pattern.PatternBuilder;
import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Test;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
public class InvalidPatternParserTest {

    private InvalidPatternParser parser = new InvalidPatternParser();


    @Test
    public void parseValidErrorMessage(){
        ValidationError error = parser.parse("cvc-pattern-valid: Value '' is not facet-valid with respect to pattern '(/[A-Z])?(/[a-zA-Z0-9/\\-?:().,'+{}\\s]{1,34})?(([a-zA-Z0-9/\\-?:().,'+{}\\s]{1,35}){1,4})' for type 'field53D'.");

        assertNotNull(error);
        assertEquals("field53D",error.getTag());
        assertEquals("",error.getValue());
        assertEquals("field53D is invalid because it does not match the given pattern: (/[A-Z])?(/[a-zA-Z0-9/\\-?:().,'+{}\\s]{1,34})?(([a-zA-Z0-9/\\-?:().,'+{}\\s]{1,35}){1,4})",error.getMessage());
        System.out.println(error.getMessage());
    }

    @Test
    public void parseValidErrorMessage2(){
        String xmlErrorMessage = "cvc-pattern-valid: Value '\nIDENTIFIER\n CODE\nBY\n NEGOTIATION\n' is not facet-valid with respect to pattern '[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?[a-zA-Z0-9/\\-?:().,'+{}\\s]{1,14}' for type 'field41A'.";
        Pattern errorMessagePattern = Pattern.compile(parser.getFormat(),Pattern.DOTALL);
        Matcher errorMessageMatcher = errorMessagePattern.matcher(xmlErrorMessage);
        assertTrue(errorMessageMatcher.find());
    }

    @Test
    public void quotePattern(){
        String pattern1 = Pattern.quote("IDENTIFIER CODE\n" +
                "BY NEGOTIATION");
        String pattern2 = Pattern.quote("[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?[a-zA-Z0-9/\\-?:().,'+{}\\s]{1,14}");
        System.out.println("******* pattern1 " + pattern1);
        System.out.println("******* pattern2 " + pattern2);

    }


}
