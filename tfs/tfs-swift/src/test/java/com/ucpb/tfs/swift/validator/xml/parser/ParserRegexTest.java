package com.ucpb.tfs.swift.validator.xml.parser;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 */
public class ParserRegexTest {


    @Test
    public void successfullyExtractDetailsFromInvalidPatternMessage(){
        String message = "cvc-pattern-valid: Value '1234dg' is not facet-valid with respect to pattern '(/[A-Z])?(/[a-zA-Z0-9/\\\\-?:().,'+{}\\\\s]{1,34})?(([a-zA-Z0-9/\\\\-?:().,'+{}\\\\s]{1,35}){1,4})' for type 'field53D'.";
        String pattern = "cvc-pattern-valid: Value '(.*)' is not facet-valid with respect to pattern '(.*)' for type 'field(.*)'.";
        assertTrue(message.matches(pattern));

        Matcher matcher = getMatcherForPattern(pattern,message);
        assertTrue(matcher.find());
        assertEquals("(/[A-Z])?(/[a-zA-Z0-9/\\\\-?:().,'+{}\\\\s]{1,34})?(([a-zA-Z0-9/\\\\-?:().,'+{}\\\\s]{1,35}){1,4})",matcher.group(2));
        assertEquals("1234dg",matcher.group(1));
        assertEquals("53D",matcher.group(3));
    }

    private Matcher getMatcherForPattern(String pattern,String source){
        return Pattern.compile(pattern).matcher(source);
    }


}
