package com.ucpb.tfs.interfaces.services.impl;

import java.util.HashSet;
import java.util.Set;

import com.ucpb.tfs.interfaces.gateway.*;
import com.ucpb.tfs.interfaces.services.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CasaServiceImplTest {

	private CasaServiceImpl casaService;

	private CasaGateway casaGateway;
	
	@Before
	public void setup(){
		casaGateway = mock(CasaGateway.class);
		when(casaGateway.sendCasaRequest(any(CasaRequest.class))).thenReturn("1211141847175618751678561785167851678361783                       ");
		Set<String> transactionCodes = new HashSet<String>();
		transactionCodes.add("1234");
		transactionCodes.add("3241");
		casaService = new CasaServiceImpl(casaGateway);
	}
	
	@Test
	public void successfullyDelegateCasaRequestToGateway() throws ServiceException {
        FinRequest request = new FinRequest();
        request.setTransactionCode(TransactionCode.CREDIT_ERROR_CORRECT_CURRENT);
        when(casaGateway.sendCasaRequest(any(CasaRequest.class))).thenReturn("1211141847175618751678561785167851678361783                       ");
        casaService.sendCasaRequest(request);
		verify(casaGateway).sendCasaRequest(any(CasaRequest.class));
	}
	
	@Test
	public void successfullyParseCreditErrorCorrectToCurrentResponse() throws ServiceException {
		StringBuilder mockResponse = new StringBuilder();
		mockResponse
                .append("0000")
                .append("111111").append("88888888")
		.append(StringUtils.leftPad("", 48," "));
		when(casaGateway.sendCasaRequest(any(CasaRequest.class))).thenReturn(mockResponse.toString());
        CasaRequest request = new FinRequest();
        request.setTransactionCode(TransactionCode.CREDIT_ERROR_CORRECT_CURRENT);
		CasaResponse response = casaService.sendCasaRequest(request);
		assertNotNull(response);
		assertEquals("0000",response.getResponseCode());
		assertEquals("111111",response.getReferenceNumber());
	}


	
}
