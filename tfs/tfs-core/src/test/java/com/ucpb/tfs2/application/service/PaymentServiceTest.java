package com.ucpb.tfs2.application.service;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.domain.payment.*;
import com.ucpb.tfs.domain.payment.casa.AccountType;
import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import com.ucpb.tfs.domain.payment.casa.Currency;
import com.ucpb.tfs.domain.payment.modes.Loan;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.interfaces.domain.Facility;
import com.ucpb.tfs.interfaces.gateway.AccountStatus;
import com.ucpb.tfs.interfaces.services.ServiceException;
import com.ucpb.tfs2.application.service.casa.CasaService;
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @MockitoAnnotations.Mock
    private PaymentRepository paymentRepository;

    @MockitoAnnotations.Mock
    private CasaService casaService;

    @MockitoAnnotations.Mock
    private EmployeeRepository employeeRepository;

    @MockitoAnnotations.Mock
    private DomainEventPublisher eventPublisher;

    @MockitoAnnotations.Mock
    private LoanService loanService;

    @InjectMocks
    private PaymentService paymentService;


    @Test
    public void successfullyPayByCasaAccount() throws CasaServiceException {
        Payment payment = new Payment();
        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setId(2L);
        paymentDetail.setAmount(new BigDecimal("1214"));
        paymentDetail.setPaymentInstrumentType(PaymentInstrumentType.CASA);
        paymentDetail.setReferenceNumber("101111111111");

        Set<PaymentDetail> detailSet = new HashSet<PaymentDetail>();
        detailSet.add(paymentDetail);
        payment.addNewPaymentDetails(detailSet);

        when(paymentRepository.getPaymentByPaymentDetailId(2L)).thenReturn(payment);

        Employee employee = new Employee();
        employee.setUserId(new UserId("userId"));
        employee.setTellerId("tellerId");
        employee.setUnitCode("unitCode");

        TradeService mockTradeService = new TradeService();
        mockTradeService.setDocumentNumber(new DocumentNumber("docNumber"));

        when(employeeRepository.getEmployee(new UserId("userId"))).thenReturn(employee);
        when(casaService.debitAccount(any(Payment.class),any(Long.class),any(Employee.class),anyString())).thenReturn("12314");

        String transactionResult = paymentService.payViaCasaAccount(payment,2L, "userId", mockTradeService,"user");

        verify(casaService).debitAccount(any(Payment.class),any(Long.class),eq(employee),anyString());
        verify(paymentRepository).saveOrUpdate(payment);

    }

    @Test
    public void successfullyPayByLoan(){
        Payment payment = new Payment();
        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setId(2L);
        paymentDetail.setAmount(new BigDecimal("1214"));
        paymentDetail.setPaymentInstrumentType(PaymentInstrumentType.IB_LOAN);
        paymentDetail.setReferenceNumber("101111111111");

        Set<PaymentDetail> detailSet = new HashSet<PaymentDetail>();
        detailSet.add(paymentDetail);
        payment.addNewPaymentDetails(detailSet);

        TradeService mockTradeService = new TradeService();
        mockTradeService.setDocumentNumber(new DocumentNumber("docNumber"));

        Loan loan = new Loan();
        Facility facility = new Facility();
        facility.setFacilityId(12);
        facility.setFacilityReferenceNumber("Facility Ref Number");
        facility.setFacilityType("FCN");
        loan.setFacility(facility);


        when(paymentRepository.getPaymentByPaymentDetailId(2L)).thenReturn(payment);
        when(loanService.createLoan(any(Loan.class),eq("userId"),eq(paymentDetail),eq(mockTradeService))).thenReturn(23456L);

        paymentService.payByLoan(2L,loan,"userId",mockTradeService);

        verify(paymentRepository,times(2)).saveOrUpdate(payment);
        verify(loanService).createLoan(loan,"userId",paymentDetail,mockTradeService);

    }

    @Test
    public void successfullyRetrieveCasaAccountDetails() throws ServiceException, CasaServiceException {
        CasaAccount account = new CasaAccount("123456789012", AccountType.CURRENT, Currency.PHP);
        account.setAccountName("ACCOUNT NAME");
        account.setAccountStatus(AccountStatus.ACTIVE);
        Employee employee = new Employee();
        employee.setUserId(new UserId("user"));

        when(employeeRepository.getEmployee(new UserId("user"))).thenReturn(employee);
        when(casaService.getCasaAccountDetails(eq("accountNum"),any(Employee.class))).thenReturn(account);

        CasaAccount accountResult = paymentService.getAccountDetails("accountNum","user",java.util.Currency.getInstance("PHP"));
        verify(casaService).getCasaAccountDetails(eq("accountNum"), any(Employee.class));

        assertEquals("ACCOUNT NAME",accountResult.getAccountName());
        assertEquals("A",accountResult.getAccountStatus());
        assertEquals("123456789012",accountResult.getAccountNumber());
    }


    @Test
    public void creditCasaAccountForSettlementType() throws CasaServiceException {

        Payment payment = new Payment(new TradeServiceId("trade-service-id"), ChargeType.SETTLEMENT);

        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setId(2L);
        paymentDetail.setAmount(new BigDecimal("1214"));
        paymentDetail.setPaymentInstrumentType(PaymentInstrumentType.CASA);
        paymentDetail.setReferenceNumber("101111111111");

        Set<PaymentDetail> detailSet = new HashSet<PaymentDetail>();
        detailSet.add(paymentDetail);
        payment.addNewPaymentDetails(detailSet);

        TradeService mockTradeService = new TradeService();
        mockTradeService.setDocumentNumber(new DocumentNumber("docNumber"));

        when(paymentRepository.getPaymentByPaymentDetailId(2L)).thenReturn(payment);
        when(casaService.creditAccount(any(Payment.class),any(Long.class),any(Employee.class),anyString())).thenReturn("12145523");

        String transactionNumber = paymentService.payViaCasaAccount(payment,2L,"userId",mockTradeService,"user");
        verify(casaService).creditAccount(any(Payment.class),any(Long.class),any(Employee.class),anyString());
        assertEquals(transactionNumber,"12145523");

    }


    @Test
    public void deditCasaAccountForNonSettlementTypes() throws CasaServiceException {

        Payment payment = new Payment(new TradeServiceId("trade-service-id"), ChargeType.PRODUCT);

        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setId(2L);
        paymentDetail.setAmount(new BigDecimal("1214"));
        paymentDetail.setPaymentInstrumentType(PaymentInstrumentType.CASA);
        paymentDetail.setReferenceNumber("101111111111");

        Set<PaymentDetail> detailSet = new HashSet<PaymentDetail>();
        detailSet.add(paymentDetail);
        payment.addNewPaymentDetails(detailSet);

        TradeService mockTradeService = new TradeService();
        mockTradeService.setDocumentNumber(new DocumentNumber("docNumber"));

        when(paymentRepository.getPaymentByPaymentDetailId(2L)).thenReturn(payment);
        when(casaService.debitAccount(any(Payment.class),any(Long.class),any(Employee.class),anyString())).thenReturn("12145523");

        String transactionNumber = paymentService.payViaCasaAccount(payment,2L,"userId",mockTradeService,"user");
        verify(casaService).debitAccount(any(Payment.class),any(Long.class),any(Employee.class),anyString());
        assertEquals(transactionNumber,"12145523");

    }

}
