package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;

import java.util.regex.Matcher;


public class FailedResponseWithReverseIdParser extends ParsingStrategy {

    private static final String FAILED_RESPONSE = "([\\d]{4})([.\\s\\w\\,]{20})([\\d]{8})([\\s]{34})";


    @Override
    protected CasaResponse mapToCasaResponse(String[] response) {
        CasaResponse casaResponse = new CasaResponse();
        casaResponse.setResponseCode(response[0]);
        casaResponse.setErrorMessage(response[1]);
        casaResponse.setWorkTaskId(response[2]);
        return casaResponse;
    }

    @Override
    protected String getFormat() {
        return FAILED_RESPONSE;
    }
}
