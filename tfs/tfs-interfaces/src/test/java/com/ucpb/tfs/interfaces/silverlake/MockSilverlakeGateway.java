package com.ucpb.tfs.interfaces.silverlake;

import java.util.ArrayList;
import java.util.List;

import com.ucpb.tfs.interfaces.gateway.CasaGateway;
import com.ucpb.tfs.interfaces.gateway.CasaRequest;

/**
 * @author Robbie
 *  Mock gateway that always return a successful response
 */
public class MockSilverlakeGateway implements CasaGateway{

	private static final String DEFAULT_RESPONSE = "660000refNumtaskId01                                                ";
	private List<String> responses = new ArrayList<String>();
	
	@Override
	public String sendCasaRequest(CasaRequest request) {
		if(!responses.isEmpty()){
			return responses.remove(0);
		}
		return DEFAULT_RESPONSE;
	}
	
	public void addResponse(String response){
		responses.add(response);
	}
	
	private String buildDefaultResponse() {
		return DEFAULT_RESPONSE;
	}
}
