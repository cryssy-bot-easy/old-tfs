package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class SuccessfulResponseParser extends ParsingStrategy {
	//"000000004500000045                                                "
    public static final String SUCCESSFUL_RESPONSE = "([\\d]{4})([\\d]{6})([\\s]{56})";

    @Override
    protected CasaResponse mapToCasaResponse(String[] response) {
        CasaResponse casaResponse = new CasaResponse();
        casaResponse.setResponseCode(response[0]);
        casaResponse.setReferenceNumber(response[1]);
        return casaResponse;
    }

    @Override
    protected String getFormat() {
        return SUCCESSFUL_RESPONSE;
    }
}
