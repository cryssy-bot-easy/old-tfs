package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.AccountStatus;
import com.ucpb.tfs.interfaces.gateway.CasaResponse;

/**
 */
public class SuccessfulStatusInquiryResponseParser extends ParsingStrategy {

//    private static final String SUCCESSFUL_RESPONSE = "([\\d]{4})({\\d})([.\\s\\w\\,]{15})([\\s]{46})";

    // MARV: added currency code for message string
//    private static final String SUCCESSFUL_RESPONSE = "([\\d]{4})([\\d\\s])([.\\s\\w\\,]{15})([\\s]{46})";

    // MARV: incuded ampersand (&) on parsing
//    private static final String SUCCESSFUL_RESPONSE = "([\\d]{4})([\\d\\s])([.\\s\\w\\,&-/']{15})([\\s]{46})";
    // MARV: included all non-special characters
    private static final String SUCCESSFUL_RESPONSE = "([\\d]{4})([\\d\\s])([a-zA-Z0-9'/~@#\\^\\$&\\*\\(\\)-_\\+=\\[\\]\\{\\}\\|\\\\,\\.\\?\\s]*{15})([\\s]{46})";

    @Override
    protected CasaResponse mapToCasaResponse(String[] response) {
        CasaResponse casaResponse = new CasaResponse();
        System.out.println("response code : [" + response[0] + "]");
        casaResponse.setResponseCode(response[0]);
        System.out.println("account status code :  [" + response[1] + "]");
        casaResponse.setAccountStatus(AccountStatus.getAccountStatusByCode(response[1]));
        System.out.println("account name : [" + response[2] + "]");
        casaResponse.setAccountName(response[2]);
        return casaResponse;
    }

    @Override
    protected String getFormat() {
        return SUCCESSFUL_RESPONSE;
    }


}
