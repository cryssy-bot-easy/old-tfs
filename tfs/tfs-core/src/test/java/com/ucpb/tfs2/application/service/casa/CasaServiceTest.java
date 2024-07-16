package com.ucpb.tfs2.application.service.casa;

import com.ucpb.tfs.domain.audit.infrastructure.repositories.CasaTransactionLogRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.interfaces.gateway.*;
import com.ucpb.tfs.interfaces.services.ServiceException;
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class CasaServiceTest {

    @MockitoAnnotations.Mock
    private com.ucpb.tfs.interfaces.services.CasaService interfaceCasaService;

    @MockitoAnnotations.Mock
    private CasaTransactionLogRepository casaTransactionLogRepository;

    @InjectMocks
    private CasaService casaService;



    @Test
    public void nullCasaAccountIfAccountNumberIsInvalid() throws ServiceException, CasaServiceException {
        CasaResponse response = new CasaResponse();
        response.setAccountName("ACCOUNT NAME");
        response.setAccountStatus(AccountStatus.ACTIVE);
        response.setReferenceNumber("1");
        response.setResponseCode("1920");

        Employee employee = new Employee();
        employee.setUserId(new UserId("userId"));
        employee.setTellerId("tellerId");
        employee.setUnitCode("909");

        when(interfaceCasaService.sendCasaRequest(any(CasaRequest.class))).thenReturn(response);

        CasaAccount account = casaService.getCasaAccountDetails("101111111111",employee);
        assertNull(account);
    }



    @Test
    public void sendSavingsAccountDebitRequest() throws ServiceException, CasaServiceException {
        CasaResponse response = new CasaResponse();
        response.setAccountName("ACCOUNT NAME");
        response.setAccountStatus(AccountStatus.ACTIVE);
        response.setReferenceNumber("1");
        response.setResponseCode("0000");

        Payment payment = new Payment(new TradeServiceId("12131"), ChargeType.PRODUCT);



        PaymentDetail detail = new PaymentDetail();
        detail.setReferenceNumber("101111111111");
        detail.setAmount(new BigDecimal("1123"));
        detail.setId(12L);
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        details.add(detail);
        payment.addNewPaymentDetails(details);

        when(interfaceCasaService.sendCasaRequest(any(FinRequest.class))).thenReturn(response);
        Employee employee = new Employee();
        employee.setUserId(new UserId("USERID"));
        employee.setTellerId("tellerIDDDD");

        casaService.debitAccount(payment,detail.getId(),employee,"this");
        verify(interfaceCasaService).sendCasaRequest(any(FinRequest.class));

    }

    @Test
    public void sendCurrentAccountDebitRequest() throws ServiceException, CasaServiceException {
        PaymentDetail detail = new PaymentDetail();
        detail.setReferenceNumber("202400000715");
        detail.setAmount(new BigDecimal("1123"));
        detail.setId(121L);

        Payment payment = new Payment(new TradeServiceId("12131"), ChargeType.PRODUCT);
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        details.add(detail);
        payment.addNewPaymentDetails(details);


        CasaResponse response = new CasaResponse();
        response.setAccountName("ACCOUNT NAME");
        response.setAccountStatus(AccountStatus.ACTIVE);
        response.setReferenceNumber("1");
        response.setResponseCode("0000");

        when(interfaceCasaService.sendCasaRequest(any(FinRequest.class))).thenReturn(response);
        Employee employee = new Employee();
        employee.setUserId(new UserId("USERID"));
        employee.setTellerId("tellerIDDDD");

        casaService.debitAccount(payment,detail.getId(),employee,"this");
        verify(interfaceCasaService).sendCasaRequest(any(FinRequest.class));

    }

    @Test
    public void reverseSavingsAccountDebitRequest() throws ServiceException, CasaServiceException {
        PaymentDetail detail = new PaymentDetail();
        detail.setReferenceNumber("101111111111");
        detail.setAmount(new BigDecimal("1241.14"));
        detail.setId(341L);

        CasaResponse response = new CasaResponse();
        response.setAccountName("ACCOUNT NAME");
        response.setAccountStatus(AccountStatus.ACTIVE);
        response.setReferenceNumber("1");
        response.setResponseCode("0000");

        when(interfaceCasaService.sendCasaRequest(any(ReversalRequest.class))).thenReturn(response);
        Employee employee = new Employee();
        employee.setUserId(new UserId("USERID"));
        employee.setTellerId("tellerIDDDD");


        Payment payment = new Payment(new TradeServiceId("12131"), ChargeType.PRODUCT);
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        details.add(detail);
        payment.addNewPaymentDetails(details);


        casaService.reverseDebitTransaction(payment,detail.getId(), employee);
        verify(interfaceCasaService).sendCasaRequest(any(ReversalRequest.class));
    }

    @Test(expected = CasaServiceException.class)
    public void throwCasaExceptionOnFailedCasaResponse() throws ServiceException, CasaServiceException {
        PaymentDetail detail = new PaymentDetail();
        detail.setReferenceNumber("101111111111");
        detail.setAmount(new BigDecimal("1123"));
        detail.setId(512L);

        CasaResponse response = new CasaResponse();
        response.setAccountName("ACCOUNT NAME");
        response.setAccountStatus(AccountStatus.ACTIVE);
        response.setReferenceNumber("1");
        response.setResponseCode("1234");

        when(interfaceCasaService.sendCasaRequest(any(FinRequest.class))).thenReturn(response);
        Employee employee = new Employee();
        employee.setUserId(new UserId("USERID"));
        employee.setTellerId("tellerIDDDD");


        Payment payment = new Payment(new TradeServiceId("12131"), ChargeType.PRODUCT);
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        details.add(detail);
        payment.addNewPaymentDetails(details);


        casaService.debitAccount(payment,detail.getId(),employee,"this");
    }

    @Test(expected = CasaServiceException.class)
    public void throwCasaExceptionOnServiceException() throws ServiceException, CasaServiceException {
        PaymentDetail detail = new PaymentDetail();
        detail.setReferenceNumber("101111111111");
        detail.setAmount(new BigDecimal("1123"));
        detail.setId(52L);

        when(interfaceCasaService.sendCasaRequest(any(FinRequest.class))).thenThrow(new ServiceException("An error occurred"));
        Employee employee = new Employee();
        employee.setUserId(new UserId("USERID"));
        employee.setTellerId("tellerIDDDD");


        Payment payment = new Payment(new TradeServiceId("12131"), ChargeType.PRODUCT);
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        details.add(detail);
        payment.addNewPaymentDetails(details);


        casaService.debitAccount(payment,detail.getId(),employee,"this");
    }

}
