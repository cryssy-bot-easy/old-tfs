package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class FailedResponseParserTest {

    private FailedResponseParser failedResponseParser = new FailedResponseParser();


    @Test
    public void successfullyParseValidResponse() throws ParseException {
        String FAILED_RESPONSE = "([\\d]{4})([.\\s\\w\\d]{39})([\\s]{23})";

        String validResponse = "9991This is my message wuuut                                      ";
        assertEquals(66,validResponse.length());
        CasaResponse response = failedResponseParser.parse(validResponse);
        assertEquals("9991",response.getResponseCode());
        assertEquals("This is my message wuuut               ",response.getErrorMessage());
    }

    @Test(expected = ParseException.class)
    public void exceptionOnInvalidResponse() throws ParseException {
        String invalidResponse = "lkfjskl;fhwjrusflk;sjf;kasjdl;adjakl;djakl;dja";
        failedResponseParser.parse(invalidResponse);
    }

}
