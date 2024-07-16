package com.ucpb.tfs2.infrastructure.rest;

import com.google.gson.Gson;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.application.service.ConversionService;
import com.ucpb.tfs.domain.payment.*;
import com.ucpb.tfs.domain.payment.casa.*;
import com.ucpb.tfs.domain.payment.casa.Currency;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.interfaces.gateway.AccountStatus;
import com.ucpb.tfs.interfaces.services.ServiceException;
import com.ucpb.tfs2.application.service.PaymentService;
import com.ucpb.tfs2.application.service.casa.CasaService;
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException;
import com.ucpb.tfs2.application.service.casa.exception.NonExistentBranchException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentRestServicesTest {


    @MockitoAnnotations.Mock
    private PaymentRepository paymentRepository;

    @MockitoAnnotations.Mock
    private LoanService loanService;

    @MockitoAnnotations.Mock
    private TradeServiceRepository tradeServiceRepository;

    @MockitoAnnotations.Mock
    private DomainEventPublisher eventPublisher;

    @MockitoAnnotations.Mock
    private PaymentService paymentService;

    @MockitoAnnotations.Mock
    private CasaService casaService;

    @MockitoAnnotations.Mock
    private ConversionService conversionService;

    @InjectMocks
    private PaymentRestServices paymentRestServices;


    @Before
    public void setup(){

    }

//    @Test
//    public void passAmountThatFallsWithinTheAllocationUnitsCreditLimit() throws NonExistentBranchException {
//        UriInfo allUri = mock(UriInfo.class);
//        String postRequestBody = "{'unitCode':'unitCode','amount':'12312.24','currency':'PHP'}";
//
//
//        when(casaService.getCasaTransactionLimit("unitCode")).thenReturn(new BigDecimal("3413143"));
//
//
//        Response response = paymentRestServices.validateCasaAmount(allUri,postRequestBody);
//
//        Gson gson = new Gson();
//        Map responseBody = gson.fromJson(response.getEntity().toString(), Map.class);
//        assertTrue((Boolean) responseBody.get("success"));
//        verify(conversionService,never()).convertToPhpUsingUrr(any(java.util.Currency.class),any(BigDecimal.class));
//    }

//    @Test
//    public void passThirdCurrencyAmount() throws NonExistentBranchException {
//        UriInfo allUri = mock(UriInfo.class);
//        String postRequestBody = "{'unitCode':'unitCode','amount':'12312.24','currency':'EUR'}";
//
//
//        when(casaService.getCasaTransactionLimit("unitCode")).thenReturn(new BigDecimal("3413143"));
//        when(conversionService.convertToPhpUsingUrr(java.util.Currency.getInstance("EUR"),new BigDecimal("12312.24"))).thenReturn(new BigDecimal("3413142"));
//
//        Response response = paymentRestServices.validateCasaAmount(allUri,postRequestBody);
//
//        Gson gson = new Gson();
//        Map responseBody = gson.fromJson(response.getEntity().toString(), Map.class);
//        System.out.println("ERROR MESSAGE: " + responseBody.get("errorMessage"));
//        assertTrue((Boolean) responseBody.get("success"));
//        verify(conversionService).convertToPhpUsingUrr(any(java.util.Currency.class), any(BigDecimal.class));
//
//    }

//    @Test
//    public void failThirdCurrencyAmountBecauseItsValueExceedsLimitAfterConversion() throws NonExistentBranchException {
//        UriInfo allUri = mock(UriInfo.class);
//        String postRequestBody = "{'unitCode':'unitCode','amount':'12312.24','currency':'EUR'}";
//
//        when(casaService.getCasaTransactionLimit("unitCode")).thenReturn(new BigDecimal("3413143"));
//        when(conversionService.convertToPhpUsingUrr(java.util.Currency.getInstance("EUR"),new BigDecimal("12312.24"))).thenReturn(new BigDecimal("999999999999"));
//
//        Response response = paymentRestServices.validateCasaAmount(allUri,postRequestBody);
//
//
//        Gson gson = new Gson();
//        Map responseBody = gson.fromJson(response.getEntity().toString(), Map.class);
//        System.out.println("ERROR MESSAGE: " + responseBody.get("errorMessage"));
//        assertFalse((Boolean) responseBody.get("success"));
//        verify(conversionService).convertToPhpUsingUrr(any(java.util.Currency.class), any(BigDecimal.class));
//    }


    @Test
    public void successfullyGetLoanErrors(){
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{'sequenceNumber':'1'}";

        List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();

        Map<String,Object> result = new HashMap<String,Object>();
        result.put("ERRDSC","This loan is invalid");
        results.add(result);

        when(loanService.getLoanErrorRecord(Long.valueOf(1))).thenReturn(results);

        Response response = paymentRestServices.getLoanErrors(allUri,postRequestBody);

        verify(loanService).getLoanErrorRecord(Long.valueOf(1));
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity().toString().contains("This loan is invalid"));
        System.out.println(response.getEntity().toString());
    }

    @Test
    public void successfullyGetDetailsOfValidCasaAccountNumber() throws ServiceException, CasaServiceException {
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{'userId':'userId','accountNumber':'128499200013'}";
        CasaAccount account = new CasaAccount("128499200013", AccountType.CURRENT, Currency.PHP);
        account.setAccountName("ACCOUNT NAME");
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(paymentService.getAccountDetails("128499200013", "userId",java.util.Currency.getInstance("PHP"))).thenReturn(account);

        Response response = paymentRestServices.getAccountStatus(allUri,postRequestBody);
        System.out.println(response.getEntity().toString());
        assertTrue(response.getEntity().toString().contains("ACCOUNT NAME"));
        account.setAccountStatus(AccountStatus.ACTIVE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionForNullAccountNumber() throws ServiceException {
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{'accountNumber':'128499200013'}";
        paymentRestServices.getAccountStatus(allUri,postRequestBody);
    }

    @Test
    public void throwExceptionForNullUserId(){
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{'userId':'userId'}";
        paymentRestServices.getAccountStatus(allUri,postRequestBody);
    }

    @Test
    public void returnEmptyListOnValidLoan(){
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{'sequenceNumber':'1'}";
        Response response = paymentRestServices.getLoanErrors(allUri,postRequestBody);

        when(loanService.getLoanErrorRecord(Long.valueOf(1))).thenReturn(Collections.<Map<String, Object>>emptyList());
        verify(loanService).getLoanErrorRecord(Long.valueOf(1));
        assertEquals(200, response.getStatus());
        System.out.println(response.getEntity().toString());
        assertFalse(response.getEntity().toString().contains("This loan is invalid"));
        System.out.println(response.getEntity().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnNullSequenceNumber(){
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{}";
        Response response = paymentRestServices.getLoanErrors(allUri,postRequestBody);
    }

    @Test
    public void successfullyPayGenericItem() throws Exception {
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{'paymentDetailId':'1','userId':'USERID'}";

        Payment mockPayment = new Payment(new TradeServiceId("thisisthetradeserviceid"), ChargeType.PRODUCT);
        PaymentDetail mockPaymentDetail = mock(PaymentDetail.class);
        when(mockPaymentDetail.getId()).thenReturn(1L);
        when(mockPaymentDetail.getPaymentInstrumentType()).thenReturn(PaymentInstrumentType.AR);
        mockPayment.addPaymentDetail(mockPaymentDetail);

        TradeService tradeService = new TradeService();
        DocumentNumber documentNumber = new DocumentNumber("documentNumber");
        tradeService.setDocumentNumber(documentNumber);

        when(tradeServiceRepository.load(new TradeServiceId("thisisthetradeserviceid"))).thenReturn(tradeService);
        when(paymentRepository.getPaymentByPaymentDetailId(Long.valueOf(1))).thenReturn(mockPayment);
        when(paymentRepository.getPaymentDetail(1L)).thenReturn(mockPaymentDetail);

        Response response = paymentRestServices.payTransaction(allUri,postRequestBody);
        verify(paymentService).payTransaction(mockPayment,1L,tradeService);
    }

    @Test
    public void successfullyPayViaCasa() throws CasaServiceException {
        UriInfo allUri = mock(UriInfo.class);
        String postRequestBody = "{'paymentDetailId':'1','userId':'USERID'}";

        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getTradeServiceId()).thenReturn(new TradeServiceId("thisisthetradeserviceid"));
        PaymentDetail mockPaymentDetail = mock(PaymentDetail.class);
        when(mockPaymentDetail.getId()).thenReturn(1L);

        TradeService tradeService = new TradeService();
        DocumentNumber documentNumber = new DocumentNumber("documentNumber");
        tradeService.setDocumentNumber(documentNumber);

        when(tradeServiceRepository.load(new TradeServiceId("thisisthetradeserviceid"))).thenReturn(tradeService);
        when(paymentRepository.getPaymentByPaymentDetailId(Long.valueOf(1))).thenReturn(mockPayment);
        when(paymentRepository.getPaymentDetail(1L)).thenReturn(mockPaymentDetail);
        when(paymentService.payViaCasaAccount(mockPayment,1L, "USERID", tradeService,anyString())).thenReturn("1213141424");

        Response response = paymentRestServices.payViaCasaAccount(allUri,postRequestBody);

        assertEquals("{\"success\":true,\"transactionNumber\":\"1213141424\"}",response.getEntity().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireUserIdForCasaPayment(){
        Response response = paymentRestServices.payViaCasaAccount(mock(UriInfo.class),"{'paymentDetailId':'1'}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePaymentDetailIdForCasaPayment(){
        String postRequestBody = "{'userId':'USERID'}";
        Response response = paymentRestServices.payViaCasaAccount(mock(UriInfo.class),postRequestBody);
    }




}
