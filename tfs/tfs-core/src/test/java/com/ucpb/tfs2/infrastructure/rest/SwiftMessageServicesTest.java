package com.ucpb.tfs2.infrastructure.rest;

import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.interfaces.services.SwiftMessageService;
import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import com.ucpb.tfs.utils.SwiftMessageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class SwiftMessageServicesTest {

    @MockitoAnnotations.Mock
    private TradeServiceRepository tradeServiceRepository;

    @MockitoAnnotations.Mock
    private SwiftMessageService swiftService;

    @MockitoAnnotations.Mock
    private SwiftMessageFactory swiftMessageFactory;

    @MockitoAnnotations.Mock
    private SwiftMessageBuilder swiftMessageBuilder;

    @InjectMocks
    private SwiftMessageServices swiftMessageService;


    @Test
    public void generateAndSendSwiftMessagesForApprovedTradeService() throws ValidationException {
        TradeService tradeService = new TradeService();
        tradeService.setStatus(TradeServiceStatus.APPROVED);

        List<RawSwiftMessage> messagesToSend = new ArrayList<RawSwiftMessage>();
        messagesToSend.add(new RawSwiftMessage());

        when(tradeServiceRepository.load(new TradeServiceId("tradeId"))).thenReturn(tradeService);
        when(swiftMessageFactory.generateSwiftMessages(tradeService)).thenReturn(messagesToSend);

        Response response = swiftMessageService.generateSwiftMessages(null,"{'tradeServiceId':'tradeId'}");

        verify(tradeServiceRepository).load(new TradeServiceId("tradeId"));
        verify(swiftMessageFactory).generateSwiftMessages(tradeService);
        verify(swiftService).sendMessage(any(RawSwiftMessage.class));

    }

    @Test
    public void dontSendSwiftMessagesForUnapprovedTradeService() throws ValidationException {
        TradeService tradeService = new TradeService();
        tradeService.setStatus(TradeServiceStatus.PENDING);

        List<RawSwiftMessage> messagesToSend = new ArrayList<RawSwiftMessage>();
        messagesToSend.add(new RawSwiftMessage());

        when(tradeServiceRepository.load(new TradeServiceId("tradeId"))).thenReturn(tradeService);
        when(swiftMessageFactory.generateSwiftMessages(tradeService)).thenReturn(messagesToSend);

        Response response = swiftMessageService.generateSwiftMessages(null,"{'tradeServiceId':'tradeId'}");

        verify(tradeServiceRepository).load(new TradeServiceId("tradeId"));
        verify(swiftMessageFactory,never()).generateSwiftMessages(tradeService);
        verify(swiftService,never()).sendMessage(any(RawSwiftMessage.class));

    }

    @Test
    public void generateAndSendForPostApproved() throws ValidationException {
        TradeService tradeService = new TradeService();
        tradeService.setStatus(TradeServiceStatus.POST_APPROVED);

        List<RawSwiftMessage> messagesToSend = new ArrayList<RawSwiftMessage>();
        messagesToSend.add(new RawSwiftMessage());

        when(tradeServiceRepository.load(new TradeServiceId("tradeId"))).thenReturn(tradeService);
        when(swiftMessageFactory.generateSwiftMessages(tradeService)).thenReturn(messagesToSend);

        Response response = swiftMessageService.generateSwiftMessages(null,"{'tradeServiceId':'tradeId'}");

        verify(tradeServiceRepository).load(new TradeServiceId("tradeId"));
        verify(swiftMessageFactory).generateSwiftMessages(tradeService);
        verify(swiftService).sendMessage(any(RawSwiftMessage.class));

    }

    @Test
    public void generateAndSendForAllTradeServices() throws ValidationException {
        TradeService ts1 = generateApprovedTradeService();
        TradeService ts2 = generateApprovedTradeService();
        List<TradeService> tradeServices = new ArrayList<TradeService>();
        tradeServices.add(ts1);
        tradeServices.add(ts2);

        List<RawSwiftMessage> messagesToSend = new ArrayList<RawSwiftMessage>();
        messagesToSend.add(new RawSwiftMessage());

        when(tradeServiceRepository.list()).thenReturn(tradeServices);
        when(swiftMessageFactory.generateSwiftMessages(ts1)).thenReturn(messagesToSend);
        when(swiftMessageFactory.generateSwiftMessages(ts2)).thenReturn(messagesToSend);

        Response response = swiftMessageService.generateAllSwiftMessages(null);
        verify(swiftMessageFactory).generateSwiftMessages(ts1);
        verify(swiftMessageFactory).generateSwiftMessages(ts2);
        verify(tradeServiceRepository).list();
        verify(swiftService,times(2)).sendMessage(any(RawSwiftMessage.class));
    }


    private TradeService generateApprovedTradeService(){
        TradeService tradeService = new TradeService();
        tradeService.setStatus(TradeServiceStatus.APPROVED);
        return tradeService;
    }






}
