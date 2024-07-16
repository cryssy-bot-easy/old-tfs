package com.ucpb.tfs.interfaces.services.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ucpb.tfs.interfaces.gateway.CasaGateway;
import com.ucpb.tfs.interfaces.gateway.CasaRequest;
import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import com.ucpb.tfs.interfaces.gateway.parser.DefaultFailedResponseParser;
import com.ucpb.tfs.interfaces.gateway.parser.ParseException;
import com.ucpb.tfs.interfaces.gateway.parser.ParserFactory;
import com.ucpb.tfs.interfaces.gateway.parser.ParsingStrategy;
import com.ucpb.tfs.interfaces.services.CasaService;
import com.ucpb.tfs.interfaces.services.ServiceException;
import org.springframework.integration.MessageTimeoutException;

public class CasaServiceImpl implements CasaService{

	private CasaGateway casaGateway;

    private ParserFactory parserFactory = new ParserFactory();
	
	public CasaServiceImpl(CasaGateway casaGateway){
		this.casaGateway = casaGateway;
	}

	@Override
	public CasaResponse sendCasaRequest(CasaRequest request) throws ServiceException {
        System.out.println("sending casa request...");
        String response = "";
        try {
        	response = casaGateway.sendCasaRequest(request);
            ParsingStrategy parsingStrategy = null;
            if(CasaResponse.SUCCESSFUL.equals(getResponseCode(response))){
                System.out.println("casa response successful...");
                System.out.println("transaction code : [" + request.getTransactionCode() + "]");
                parsingStrategy = parserFactory.getSuccessfulResponseParser(request.getTransactionCode());
            }else{
                System.out.println("creating new DefaultFailedResponseParser...");
                parsingStrategy = new DefaultFailedResponseParser();
            }

            System.out.println("Response : " + response);
            return parsingStrategy.parse(response);
        } catch (ParseException e) {
            System.out.println("ParseException caught...");
            System.out.println("Response '" + response + "' format is invalid.");
            System.out.println("throwing ServiceException...");
            throw new ServiceException("Response '" + response + "' format is invalid.",e);
        } catch (MessageTimeoutException e){
            System.out.println("MessageTimeoutException caught...");
            System.out.println("throwing MessageTimeoutException...");
            throw new com.ucpb.tfs.interfaces.services.exception.MessageTimeoutException(e);
        }
    }

    private String getResponseCode(String response) throws ParseException {
        Matcher matcher = Pattern.compile("([\\d]{4})(.{62})",Pattern.DOTALL).matcher(response);
        if(matcher.find()){
            return matcher.group(1);
        }
        throw new ParseException("Output does not match expected format");
    }
	
}
