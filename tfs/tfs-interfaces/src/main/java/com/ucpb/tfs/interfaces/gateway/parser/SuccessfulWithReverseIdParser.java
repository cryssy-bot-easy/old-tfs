package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class SuccessfulWithReverseIdParser extends ParsingStrategy {

    private static final String SUCCESSFUL = "([\\d]{4})([\\d]{6})([\\d]{8})([\\s]{48})";

    @Override
    protected CasaResponse mapToCasaResponse(String[] response) {
        CasaResponse casaResponse = new CasaResponse();
        casaResponse.setResponseCode(response[0]);
        casaResponse.setReferenceNumber(response[1]);
        casaResponse.setWorkTaskId(response[2]);
        return casaResponse;
    }

    @Override
    protected String getFormat() {
        return SUCCESSFUL;
    }
}