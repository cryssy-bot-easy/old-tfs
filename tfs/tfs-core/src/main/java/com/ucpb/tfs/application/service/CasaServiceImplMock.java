package com.ucpb.tfs.application.service;

import com.ucpb.tfs.interfaces.gateway.AccountStatus;
import com.ucpb.tfs.interfaces.gateway.CasaRequest;
import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import com.ucpb.tfs.interfaces.services.CasaService;
import com.ucpb.tfs.interfaces.services.ServiceException;

/**
 */
public class CasaServiceImplMock implements CasaService {


    @Override
    public CasaResponse sendCasaRequest(CasaRequest request) throws ServiceException {
        CasaResponse response = new CasaResponse();
        response.setResponseCode(CasaResponse.SUCCESSFUL);
        response.setReferenceNumber("referenceNumber");
        response.setWorkTaskId("workTaskId");
        response.setAccountStatus(AccountStatus.ACTIVE);
        System.out.println("************* SENDING MOCK CASA RESPONSE ****************");
        return response;
    }
}
