package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class SuccessfulResponseParserTest {

    private SuccessfulResponseParser parser = new SuccessfulResponseParser();
    public static final String SUCCESSFUL_RESPONSE = "([\\d]{4})([\\d]{6})([\\s]{56})";


    @Test
    public void successfullyParseValidResponse() throws ParseException {
        String validResponse = "0000333333                                                        ";
        CasaResponse response = parser.parse(validResponse);
        assertEquals("0000",response.getResponseCode());
        assertEquals("333333",response.getReferenceNumber());
    }

    @Test(expected = ParseException.class)
    public void exceptionOnInvalidFormat() throws ParseException {
        String invalidResponse = "invalid response!!!!";
        parser.parse(invalidResponse);

    }
}
