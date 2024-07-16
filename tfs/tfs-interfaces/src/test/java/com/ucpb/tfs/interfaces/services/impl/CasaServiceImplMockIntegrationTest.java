package com.ucpb.tfs.interfaces.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;

import com.ucpb.tfs.interfaces.gateway.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.util.ServerUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*casa-service-context.xml")
@DirtiesContext
public class CasaServiceImplMockIntegrationTest {

	@Autowired
	private CasaServiceImpl casaServiceImpl;
	
	@Autowired
	private CasaGateway gateway;
	
	@Autowired
	@Qualifier("silverlakeConnectionFactory")
	private AbstractServerConnectionFactory silverlakeConnectionFactory;
	
	@Before
	public void setup() {
		ServerUtils.waitListening(this.silverlakeConnectionFactory);
	}
	
	@Test
	public void successfulResponse() throws IOException, InterruptedException{
        FinRequest request = new FinRequest();
		request.setUsername("username");
		request.setPassword("password");
		request.setAccountNumber("122222211123");
		request.setAmount(new BigDecimal("90.99"));
        request.setTransactionCode(TransactionCode.DEBIT_TO_SAVINGS);
		CasaResponse response = casaServiceImpl.sendCasaRequest(request);
		assertNotNull(response);
		
		assertEquals("0000",response.getResponseCode());
        assertEquals("666666",response.getReferenceNumber());
	}

    @Test
    public void success(){
        assertTrue("0000refNumtaskId01                                                  ".matches("([\\d]{4})([.\\s\\w]{64})"));
    }

    @Test
    public void something(){
        assertTrue("refNumtaskId01".matches("[.\\w\\d]{14}"));
    }

}
