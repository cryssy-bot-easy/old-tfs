package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;

import java.util.regex.Matcher;

/**
 */
public class FailedResponseParser extends ParsingStrategy {

    private static final String FAILED_RESPONSE = "([\\d]{4})([.\\s\\w\\d\\,]{39})([\\s]{23})";

    @Override
    protected CasaResponse mapToCasaResponse(String[] response) {
        CasaResponse casaResponse = new CasaResponse();
        casaResponse.setResponseCode(response[0]);
        casaResponse.setErrorMessage(response[1]);
        return casaResponse;
    }

    @Override
    protected String getFormat() {
        return FAILED_RESPONSE;
    }
}
