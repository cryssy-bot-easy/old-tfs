package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.PayCommand;
import com.ucpb.tfs.domain.payment.*;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.interfaces.gateway.CasaRequest;
import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import com.ucpb.tfs.interfaces.services.CasaService;
import com.ucpb.tfs.interfaces.services.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class PayCommandHandlerTest {

    @MockitoAnnotations.Mock
    private CasaService casaService;

    @MockitoAnnotations.Mock
    PaymentRepository paymentRepository;

    @MockitoAnnotations.Mock
    TradeServiceRepository tradeServiceRepository;

    @MockitoAnnotations.Mock
    DomainEventPublisher eventPublisher;

    @MockitoAnnotations.Mock
    TokenProvider tokenProvider;


    @InjectMocks
    private PayCommandHandler commandHandler;


    @Before
    public void setupTradeServiceRepo(){
        TradeService tradeService = mock(TradeService.class);
        when(tradeService.getDocumentNumber()).thenReturn(new DocumentNumber("1234567"));

        Map<String,Object> details = new HashMap<String, Object>();
        details.put("documentNumber"   ,"134919813142");
        tradeService.updateDetails(details, new UserActiveDirectoryId("someuser"));

        when(tradeServiceRepository.load(any(TradeServiceId.class))).thenReturn(tradeService);
    }

    @Before
    public void setupPaymentRepository(){
        Payment payment = new Payment();
        PaymentDetail detail = new  PaymentDetail(
                PaymentInstrumentType.CASA,"accountNumber",
                null,
                new BigDecimal("12134.08"),
                Currency.getInstance("PHP"),
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        details.add(detail);
        payment.addNewPaymentDetails(details);
        when(paymentRepository.get(any(TradeServiceId.class), any(ChargeType.class))).thenReturn(payment);

    }

    @Before
    public void setupCasa() throws ServiceException {
        //default casa response is successful
        CasaResponse successful = new CasaResponse();
        successful.setAccountName("123456789");
        successful.setAccountStatus("ACTIVE");
        successful.setReferenceNumber("121314");
        successful.setResponseCode(CasaResponse.SUCCESSFUL);

        when(casaService.sendCasaRequest(any(CasaRequest.class))).thenReturn(successful);

    }

    @Test
    public void successfullySendDebitRequestToCasa(){
        PayCommand request = new PayCommand();
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("modeOfPayment","CASA");
        parameterMap.put("chargeType","PRODUCT");
        parameterMap.put("amount","3405.40");
        parameterMap.put("tradeServiceId","1234567");
        parameterMap.put("settlementCurrency","PHP");
        parameterMap.put("accountNumber","accountNumber");
//        parameterMap.setParameterMap(request, parameterMap);
        request.setToken("tokentoken");


        commandHandler.handle(request);
//        verify(tokenProvider).addTokenForId("tokentoken","true");
    }




}
