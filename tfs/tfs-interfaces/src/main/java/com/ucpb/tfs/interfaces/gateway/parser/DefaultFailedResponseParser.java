package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class DefaultFailedResponseParser extends ParsingStrategy {

//    private static final String FAILED_RESPONSE = "([\\d]{4})([.\\s\\w\\,#]{39})([\\s]{23})";
//    private static final String FAILED_RESPONSE = "([\\d]{4})({39})([\\s]{23})";
    private  static final String FAILED_RESPONSE = "([\\d]{4})([a-zA-Z0-9'/~@#\\^\\$&\\*\\(\\)-_\\+=\\[\\]\\{\\}\\|\\\\,\\.\\?\\s]*{39})([\\s]{23})";

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