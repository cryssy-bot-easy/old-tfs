package com.ucpb.tfs.interfaces.services.impl;

import java.math.BigDecimal;

import com.ucpb.tfs.interfaces.gateway.*;
import com.ucpb.tfs.interfaces.services.ServiceException;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
@Ignore("convenience class to perform integration testing. should not be run during build")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:casa-integration-context.xml")
public class CasaServiceImplIntegrationTest {

    @Autowired
    @Qualifier("casaService")
    private CasaServiceImpl casaServiceImpl;

      //130.130.0.225:36115
      //*66                24000042101202400000144                           *
    @Ignore
    public void performStatusQueryRequest() throws ServiceException {
        NonFinRequest request = new NonFinRequest();
        request.setUsername("TFSCASA ");
        request.setPassword("ipc123$ ");
        request.setTransactionCode(TransactionCode.INQUIRE_STATUS_SAVINGS);
        request.setUserId("0001");
        request.setAccountNumber("102400000310");
        request.setBranchCode("909");


       CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
       assertNotNull(response);
       System.out.println("********** " + response.getErrorMessage());
        assertTrue(response.isSuccessful());
    }
//    " TFSCASA  ipc123$ 90900011610202400000715 00000000000100            "
//    " TFSCASA ipc123$90900011610202400000715 00000000000100            "
//    " TFSCASA  ipc123$90900011610202400000715 00000000000100            "
    @Test
    public void performDebitToCurrentQueryRequest() throws ServiceException {
        FinRequest request = new FinRequest();
        request.setUsername("TFSCASA");
        request.setPassword("ipc123$");
        request.setTransactionCode(TransactionCode.DEBIT_TO_CURRENT);
        request.setUserId("0001");
        request.setAccountNumber("202400000715");
        request.setBranchCode("909");
        request.setAmount(new BigDecimal("1.00"));
        System.out.println("******TO REQUEST*********:"+request.toRequestString());
        
       CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
       assertNotNull(response);
       System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
       assertNotNull(response.getResponseCode());
       assertNotNull(response.getReferenceNumber());
       assertNotNull(response.getWorkTaskId());
       assertTrue(response.isSuccessful());
    }

    @Test
    public void performDebitToSavingsQueryRequest() throws ServiceException {
    	FinRequest request = new FinRequest();
    	request.setUsername("TFSCASA ");
    	request.setPassword("ipc123$ ");
    	request.setTransactionCode(TransactionCode.DEBIT_TO_SAVINGS);
    	request.setUserId("0001");
    	request.setAccountNumber("102400000310");
    	request.setBranchCode("909");
    	request.setAmount(new BigDecimal("1.00"));
    	
    	CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
    	assertNotNull(response);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(response.getResponseCode());
    	assertNotNull(response.getReferenceNumber());
    	assertNotNull(response.getWorkTaskId());
    	assertTrue(response.isSuccessful());
    }

    @Test
    public void performCreditToCurrentQueryRequest() throws ServiceException {
    	FinRequest request = new FinRequest();
    	request.setUsername("TFSCASA ");
    	request.setPassword("ipc123$ ");
    	request.setTransactionCode(TransactionCode.CREDIT_TO_CURRENT);
    	request.setUserId("0001");
    	request.setAccountNumber("202400000715");
    	request.setBranchCode("909");
    	request.setAmount(new BigDecimal("1.00"));
    	
    	CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
    	assertNotNull(response);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(response.getResponseCode());
    	assertNotNull(response.getReferenceNumber());
    	assertNotNull(response.getWorkTaskId());
    	assertTrue(response.isSuccessful());
    }

    @Test
    public void performCreditToSavingsQueryRequest() throws ServiceException {
    	FinRequest request = new FinRequest();
    	request.setUsername("TFSCASA ");
    	request.setPassword("ipc123$ ");
    	request.setTransactionCode(TransactionCode.CREDIT_TO_SAVINGS);
    	request.setUserId("0001");
    	request.setAccountNumber("102400000310");
    	request.setBranchCode("909");
    	request.setAmount(new BigDecimal("1.00"));
    	
    	CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
    	assertNotNull(response);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(response.getResponseCode());
    	assertNotNull(response.getReferenceNumber());
    	assertNotNull(response.getWorkTaskId());
    	assertTrue(response.isSuccessful());
    }
    
    @Test
    public void performCreditToForeignQueryRequest() throws ServiceException {
    	FinRequest request = new FinRequest();
    	request.setUsername("TFSCASA ");
    	request.setPassword("ipc123$ ");
    	request.setTransactionCode(TransactionCode.CREDIT_TO_FOREIGN);
    	request.setUserId("0001");
    	request.setAccountNumber("202400000715");
    	request.setBranchCode("909");
    	request.setAmount(new BigDecimal("1.00"));
    	
    	CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
    	assertNotNull(response);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(response.getResponseCode());
    	assertNotNull(response.getReferenceNumber());
    	assertNotNull(response.getWorkTaskId());
    	assertTrue(response.isSuccessful());
    }

    @Test
    public void performDebitToForeignQueryRequest() throws ServiceException {
    	FinRequest request = new FinRequest();
    	request.setUsername("TFSCASA ");
    	request.setPassword("ipc123$ ");
    	request.setTransactionCode(TransactionCode.DEBIT_TO_FOREIGN);
    	request.setUserId("0001");
    	request.setAccountNumber("102400000310");
    	request.setBranchCode("909");
    	request.setAmount(new BigDecimal("1.00"));
    	System.out.println("******TO REQUEST*********:"+request.toRequestString());
    	
    	CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
    	assertNotNull(response);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(response.getResponseCode());
    	assertNotNull(response.getReferenceNumber());
    	assertNotNull(response.getWorkTaskId());
    	assertTrue(response.isSuccessful());
    }

    @Test
    @Ignore
    public void performCreditErrorCorrectToCurrentQueryRequest() throws ServiceException {
        //perform initial credit request (to be reversed)
        FinRequest creditRequest = new FinRequest();
        creditRequest.setUsername("TFSCASA ");
        creditRequest.setPassword("ipc123$ ");
        creditRequest.setTransactionCode(TransactionCode.CREDIT_TO_FOREIGN);
        creditRequest.setUserId("0001");
        creditRequest.setAccountNumber("202400000715");
        creditRequest.setBranchCode("909");
        creditRequest.setAmount(new BigDecimal("1.00"));

        CasaResponse response =  casaServiceImpl.sendCasaRequest(creditRequest);
        assertNotNull(response);
        System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
        assertNotNull(response.getResponseCode());
        assertNotNull(response.getReferenceNumber());
        assertFalse(StringUtils.isEmpty(response.getWorkTaskId()));
        assertTrue(response.isSuccessful());

        //perform reversal of previous request
    	ReversalRequest reversalRequest = new ReversalRequest();
    	reversalRequest.setUsername(creditRequest.getUsername());
    	reversalRequest.setPassword(creditRequest.getPassword());
    	reversalRequest.setTransactionCode(TransactionCode.CREDIT_ERROR_CORRECT_CURRENT);
    	reversalRequest.setUserId(creditRequest.getUserId());
    	reversalRequest.setAccountNumber(creditRequest.getAccountNumber());
    	reversalRequest.setBranchCode(creditRequest.getBranchCode());
    	reversalRequest.setAmount(creditRequest.getAmount());
        reversalRequest.setWorkTaskId(response.getWorkTaskId());
    	System.out.println("******TO REQUEST*********:"+ reversalRequest.toRequestString());
    	
    	
    	CasaResponse reversalResponse =  casaServiceImpl.sendCasaRequest(reversalRequest);
    	assertNotNull(reversalResponse);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(reversalResponse.getResponseCode());
    	assertNotNull(reversalResponse.getReferenceNumber());
    	assertNotNull(reversalResponse.getWorkTaskId());
    	assertTrue(reversalResponse.isSuccessful());
    }

    @Ignore
    @Test
    public void performDebitErrorCorrectToSavingsQueryRequest() throws ServiceException {
        FinRequest debitRequest = new FinRequest();
        debitRequest.setUsername("TFSCASA ");
        debitRequest.setPassword("ipc123$ ");
        debitRequest.setTransactionCode(TransactionCode.DEBIT_TO_SAVINGS);
        debitRequest.setUserId("0001");
        debitRequest.setAccountNumber("102400000310");
        debitRequest.setBranchCode("909");
        debitRequest.setAmount(new BigDecimal("1.00"));

        CasaResponse debitResponse =  casaServiceImpl.sendCasaRequest(debitRequest);
        assertNotNull(debitResponse);
        System.out.println("***ERROR MESSAGE*******:" + debitResponse.getErrorMessage());
        assertNotNull(debitResponse.getResponseCode());
        assertNotNull(debitResponse.getReferenceNumber());
        assertNotNull(debitResponse.getWorkTaskId());
        assertTrue(debitResponse.isSuccessful());


        ReversalRequest reversalRequest = new ReversalRequest();
    	reversalRequest.setUsername("TFSCASA ");
    	reversalRequest.setPassword("ipc123$ ");
    	reversalRequest.setTransactionCode(TransactionCode.DEBIT_ERROR_CORRECT_SAVINGS);
    	reversalRequest.setUserId("0001");
    	reversalRequest.setAccountNumber("102400000310");
    	reversalRequest.setBranchCode("909");
    	reversalRequest.setAmount(new BigDecimal("1.00"));
        reversalRequest.setWorkTaskId(debitResponse.getWorkTaskId());
    	
    	CasaResponse reversalResponse =  casaServiceImpl.sendCasaRequest(reversalRequest);
    	assertNotNull(reversalResponse);
    	System.out.println("***ERROR MESSAGE*******:" + reversalResponse.getErrorMessage());
    	assertNotNull(reversalResponse.getResponseCode());
    	assertNotNull(reversalResponse.getReferenceNumber());
    	assertNotNull(reversalResponse.getWorkTaskId());
    	assertTrue(reversalResponse.isSuccessful());
    }
    
    @Ignore
    @Test
    public void performCreditErrorCorrectToForeignQueryRequest() throws ServiceException {
        FinRequest creditToForeign = new FinRequest();
        creditToForeign.setUsername("TFSCASA ");
        creditToForeign.setPassword("ipc123$ ");
        creditToForeign.setTransactionCode(TransactionCode.CREDIT_TO_FOREIGN);
        creditToForeign.setUserId("0001");
        creditToForeign.setAccountNumber("102400000310");
        creditToForeign.setBranchCode("909");
        creditToForeign.setAmount(new BigDecimal("1.00"));

        CasaResponse creditResponse =  casaServiceImpl.sendCasaRequest(creditToForeign);
        assertNotNull(creditResponse);
        System.out.println("***ERROR MESSAGE*******:" + creditResponse.getErrorMessage());
        assertNotNull(creditResponse.getResponseCode());
        assertNotNull(creditResponse.getReferenceNumber());
        assertNotNull(creditResponse.getWorkTaskId());
        assertTrue(creditResponse.isSuccessful());

    	ReversalRequest reversalRequest = new ReversalRequest();
    	reversalRequest.setUsername("TFSCASA ");
    	reversalRequest.setPassword("ipc123$ ");
    	reversalRequest.setTransactionCode(TransactionCode.CREDIT_ERROR_CORRECT_FOREIGN);
    	reversalRequest.setUserId("0001");
    	reversalRequest.setAccountNumber("102400000310");
    	reversalRequest.setBranchCode("909");
    	reversalRequest.setAmount(new BigDecimal("1.00"));
        reversalRequest.setWorkTaskId(creditResponse.getWorkTaskId());
    	
    	CasaResponse response =  casaServiceImpl.sendCasaRequest(reversalRequest);
    	assertNotNull(response);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(response.getResponseCode());
    	assertNotNull(response.getReferenceNumber());
    	assertNotNull(response.getWorkTaskId());
    	assertTrue(response.isSuccessful());
    }

    @Ignore
    @Test
    public void performDebitErrorCorrectToForeignQueryRequest() throws ServiceException {
    	FinRequest request = new FinRequest();
    	request.setUsername("TFSCASA ");
    	request.setPassword("ipc123$ ");
    	request.setTransactionCode(TransactionCode.DEBIT_ERROR_CORRECT_FOREIGN);
    	request.setUserId("0001");
    	request.setAccountNumber("102400000310");
    	request.setBranchCode("909");
    	request.setAmount(new BigDecimal("1.00"));
    	
    	CasaResponse response =  casaServiceImpl.sendCasaRequest(request);
    	assertNotNull(response);
    	System.out.println("***ERROR MESSAGE*******:" + response.getErrorMessage());
    	assertNotNull(response.getResponseCode());
    	assertNotNull(response.getReferenceNumber());
    	assertNotNull(response.getWorkTaskId());
    	assertTrue(response.isSuccessful());
    }
}