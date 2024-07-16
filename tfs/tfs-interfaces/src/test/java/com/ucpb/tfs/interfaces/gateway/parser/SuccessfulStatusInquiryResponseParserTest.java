package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.AccountStatus;
import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import com.ucpb.tfs.interfaces.gateway.serializer.CasaSerializer;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static junit.framework.Assert.assertEquals;

/**
 */
public class SuccessfulStatusInquiryResponseParserTest {

    private SuccessfulStatusInquiryResponseParser parser = new SuccessfulStatusInquiryResponseParser();

    @Test
    public void successfullyParseValidResponse() throws ParseException {
        String validResponse = "00000MIGHTY CORP                                                  ";
        CasaResponse response = parser.parse(validResponse);
        assertEquals("0000",response.getResponseCode());
        assertEquals(AccountStatus.ACTIVE,response.getAccountStatus());
        assertEquals("MIGHTY CORP    ", response.getAccountName());
    }

    @Test(expected = ParseException.class)
    public void throwExceptionOnInvalidFormat() throws ParseException {
        parser.parse("INVALID FORMAT FOR A STATUS QUERY ");
    }

    @Test
    public void parseAsNewAccount() throws ParseException {
        String validResponse = "00001MIGHTY CORP                                                  ";
        CasaResponse response = parser.parse(validResponse);
        assertEquals("0000",response.getResponseCode());
        assertEquals(AccountStatus.NEW,response.getAccountStatus());
        assertEquals("MIGHTY CORP    ", response.getAccountName());
    }

    @Test
    public void parseAsClosedAccount() throws ParseException {
        String validResponse = "00002MIGHTY CORP                                                  ";
        CasaResponse response = parser.parse(validResponse);
        assertEquals("0000",response.getResponseCode());
        assertEquals(AccountStatus.CLOSED,response.getAccountStatus());
        assertEquals("MIGHTY CORP    ", response.getAccountName());
    }

}
