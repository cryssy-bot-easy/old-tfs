package com.ucpb.tfs.interfaces.services;

import com.ucpb.tfs.interfaces.gateway.CasaRequest;
import com.ucpb.tfs.interfaces.gateway.CasaResponse;

public interface CasaService {

	public CasaResponse sendCasaRequest(CasaRequest request) throws ServiceException;
	
}
