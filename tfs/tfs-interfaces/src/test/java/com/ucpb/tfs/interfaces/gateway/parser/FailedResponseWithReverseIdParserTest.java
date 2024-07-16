package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 */
public class FailedResponseWithReverseIdParserTest {

    private FailedResponseWithReverseIdParser parser = new FailedResponseWithReverseIdParser();
    private static final String FAILED_RESPONSE = "([\\d]{4})([.\\s\\w]{20})([\\d]{8})([\\s]{34})";


    @Test
    public void successfullyParseValidResponse() throws ParseException {
        String validResponse = "3124This is a message   88888888                                  ";
        CasaResponse response = parser.parse(validResponse);
        assertEquals("3124",response.getResponseCode());
        assertEquals("This is a message   ",response.getErrorMessage());
        assertEquals("88888888",response.getWorkTaskId());
    }

}
