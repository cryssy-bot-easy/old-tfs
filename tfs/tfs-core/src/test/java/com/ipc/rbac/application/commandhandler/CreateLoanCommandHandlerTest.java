
package com.ipc.rbac.application.commandhandler;

import com.incuventure.cqrs.token.TokenProvider;
import com.ucpb.tfs.application.command.CreateLoanCommand;
import com.ucpb.tfs.application.commandHandler.CreateLoanCommandHandler;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.services.LoanService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateLoanCommandHandlerTest {

    @MockitoAnnotations.Mock
    private LoanService loanService;

    @MockitoAnnotations.Mock
    private TradeProductRepository tradeProductRepository;

    @MockitoAnnotations.Mock
    private TokenProvider tokenProvider;

    @MockitoAnnotations.Mock
    private PaymentRepository paymentRepository;

    private CreateLoanCommand command;

    @MockitoAnnotations.Mock
    private TradeServiceRepository tradeServiceRepository;

    @InjectMocks
    private CreateLoanCommandHandler commandHandler;

    @Before
    public void setup(){
        command = new CreateLoanCommand();
        command.setToken("token");

        Map<String,Object> paramMap = new HashMap<String,Object>();
        String setupString  = "bookingCurrency=PHP&interestRate=1.00&repricingTerm=30&loanTerm=30&loanMaturityDate=12/12/2012&amount=30.00&paymentTerm=60";

        paramMap.put("paymentTerm","143");
        paramMap.put("setupString",setupString);
        paramMap.put("loanTerm","1");
        paramMap.put("loanMaturityDate","12/12/2012");
        paramMap.put("tradeServiceId","tradeServiceId");
        paramMap.put("interestRate","12.12");
        paramMap.put("lcNumber","documentNumberW00t");
        paramMap.put("modeOfPayment","IB_LOAN");
        command.setParameterMap(paramMap);

        when(loanService.insertLoan(any(Loan.class),false)).thenReturn(3412L);
        TradeService ts = mock(TradeService.class);
        when(ts.getDocumentNumber()).thenReturn(new DocumentNumber("124341"));
        when(ts.getDetails()).thenReturn(paramMap);
        when(ts.getCcbdBranchUnitCode()).thenReturn("134");
        when(ts.getProcessingUnitCode()).thenReturn("909");
        when(tradeServiceRepository.load(any(TradeServiceId.class))).thenReturn(ts);

        TradeProduct tp = mock(TradeProduct.class);
        when(tp.getMainCifNumber()).thenReturn("mainCif");
        when(tradeProductRepository.load(any(DocumentNumber.class))).thenReturn(tp);


        Payment payment = new Payment();
        PaymentDetail detail = new PaymentDetail(PaymentInstrumentType.IB_LOAN,
                "REFNUMBER",
                new BigDecimal("100"),
                Currency.getInstance("PHP"),
                Currency.getInstance("USD"),
                new BigDecimal("10"),
                "30",
                "D",
                "30",
                "D",
                "30",
                "D",
                new Date()
        );

        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        details.add(detail);

        when(paymentRepository.get(any(TradeServiceId.class), eq(ChargeType.PRODUCT))).thenReturn(payment);


    }


    @Test
    public void successfullyInsertLoanInquiry(){
        commandHandler.handle(command);
        verify(loanService).insertLoan(any(Loan.class),false);
//        verify(tokenProvider).addTokenForId("token","3412");

    }

}
