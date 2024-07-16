package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class DefaultFailedResponseParserTest {

    private DefaultFailedResponseParser defaultFailedResponseParser = new DefaultFailedResponseParser();


    @Test
    public void successfullyParseValidResponse() throws ParseException {
        String validResponse = "9213This is an error message                                      ";
        CasaResponse response = defaultFailedResponseParser.parse(validResponse);
        assertEquals("9213",response.getResponseCode());
        assertEquals("This is an error message               ",response.getErrorMessage());
    }

    @Test(expected = ParseException.class)
    public void exceptionOnInvalidResponseFormat() throws ParseException {
        String invalidResponse = "2403892901582905825908259285902859028590234824fkzncjkaakfjalfkjalfkj";
        defaultFailedResponseParser.parse(invalidResponse);
    }

    @Test//this is an actual response from sibs
    public void successfullyParseValidResponse2() throws ParseException {
        String validResponse = "0001001 Acct# not found 10291090                                  ";
        CasaResponse response = defaultFailedResponseParser.parse(validResponse);
        assertEquals("0001",response.getResponseCode());

    }


}
