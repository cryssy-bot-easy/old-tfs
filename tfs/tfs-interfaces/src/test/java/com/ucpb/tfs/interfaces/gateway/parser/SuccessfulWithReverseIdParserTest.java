package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */

public class SuccessfulWithReverseIdParserTest {

    private static final String VALID_INPUT = "000066666688888888                                                ";
    private static final String INVALID_INPUT = "9241adjadkjakldjadkljadkljadklajdklajdklajdar3242sflkjsfklsfkljs";
    private SuccessfulWithReverseIdParser parser = new SuccessfulWithReverseIdParser();


    @Test
    public void formatIsNotNull(){
        assertNotNull(parser.getFormat());
    }

    @Test
    public void inputMatchesFormat(){
        assertTrue(VALID_INPUT.matches(parser.getFormat()));
    }


    @Test
    public void successfullyParseValidResponse() throws ParseException {
        CasaResponse response = parser.parse(VALID_INPUT);
        assertNotNull(response);
        assertEquals("0000",response.getResponseCode());
        assertEquals("666666",response.getReferenceNumber());
        assertEquals("88888888",response.getWorkTaskId());
    }

    @Test(expected = ParseException.class)
    public void failOnInvalidResponseFormat() throws ParseException {
     parser.parse(INVALID_INPUT);
    }


}
