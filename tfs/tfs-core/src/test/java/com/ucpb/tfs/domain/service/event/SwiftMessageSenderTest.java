package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.mtmessage.MtMessage;
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.LCNegotiationDiscrepancy;
import com.ucpb.tfs.domain.product.UALoanSettledEvent;
import com.ucpb.tfs.domain.product.event.DACreatedEvent;
import com.ucpb.tfs.domain.product.event.DPSettlementCreatedEvent;
import com.ucpb.tfs.domain.product.event.DRSettlementCreatedEvent;
import com.ucpb.tfs.domain.product.event.LCAmendedEvent;
import com.ucpb.tfs.domain.product.event.LCNegotiationCreatedEvent;
import com.ucpb.tfs.domain.product.event.LCNegotiationDiscrepancyCreatedEvent;
import com.ucpb.tfs.domain.product.event.LetterOfCreditCreatedEvent;
import com.ucpb.tfs.domain.product.event.OASettlementCreatedEvent;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.interfaces.services.SwiftMessageService;
import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import com.ucpb.tfs.swift.message.mt1series.MT103;
import com.ucpb.tfs.swift.message.mt2series.MT202;
import com.ucpb.tfs.swift.message.mt4series.MT400;
import com.ucpb.tfs.swift.message.mt4series.MT410;
import com.ucpb.tfs.swift.message.mt7series.MT700;
import com.ucpb.tfs.swift.message.mt7series.MT707;
import com.ucpb.tfs.swift.message.mt7series.MT740;
import com.ucpb.tfs.swift.message.mt7series.MT747;
import com.ucpb.tfs.swift.message.mt7series.MT750;
import com.ucpb.tfs.swift.message.mt7series.MT760;
import com.ucpb.tfs.swift.message.mt7series.MT767;
import com.ucpb.tfs.swift.message.writer.SwiftMessageWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class SwiftMessageSenderTest {

    @MockitoAnnotations.Mock
    private SwiftMessageService swiftMessageService;

    @MockitoAnnotations.Mock
    private SwiftMessageBuilder swiftMessageBuilder;

    @MockitoAnnotations.Mock
    private MtMessageRepository mtMessageRepository;

    @MockitoAnnotations.Mock
    private SwiftMessageWriter swiftMessageWriter;

    @MockitoAnnotations.Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private SwiftMessageSender swiftMessageSender;



    @Test
    public void generateMT700ForLetterOfCreditCreatedEvent() throws ValidationException {
        MT700 mt700 = new MT700();
        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","documentNumber");
        mt700.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("700");

        mt700.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt700);
        when(swiftMessageBuilder.build(eq("700"), any(DomainEvent.class))).thenReturn(messages);

        TradeService ts=new TradeService(null, null, null, null, null, DocumentSubType1.REGULAR,
        		null, ServiceType.OPENING,
        		new UserActiveDirectoryId());
        
        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(ts,null,"");
        swiftMessageSender.generateLetterOfCreditCreationMessages(event);

//        MtMessage messageToPersist = new MtMessage("documentNumber", Direction.OUTGOING,null,"700");
        verify(mtMessageRepository).persist(any(MtMessage.class));
        verify(swiftMessageBuilder).build(eq("700"), any(TradeService.class));
        verify(swiftMessageService).sendMessage(mt700);
    }
    
    @Test
    public void generateMT760ForLetterOfCreditCreatedEvent() throws ValidationException {
        MT760 mt760 = new MT760();
        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","documentNumber");
        mt760.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("760");

        mt760.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt760);
        when(swiftMessageBuilder.build(eq("760"), any(DomainEvent.class))).thenReturn(messages);

        TradeService ts=new TradeService(null, null, null, null, null, DocumentSubType1.STANDBY,
        		null, ServiceType.OPENING,
        		new UserActiveDirectoryId());
        
        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(ts,null,"");
        swiftMessageSender.generateLetterOfCreditCreationMessages(event);

        verify(mtMessageRepository).persist(any(MtMessage.class));
        verify(swiftMessageBuilder).build(eq("760"), any(TradeService.class));
        verify(swiftMessageService).sendMessage(mt760);
    }

    

    @Ignore
    public void generateMT740ForLetterOfCreditCreatedEvent() throws ValidationException {
    	//TODO:Resolve, sender function is empty
        MT740 mt740 = new MT740();
        MessageBlock messageBlock = new MessageBlock();
        //TODO: include reimbursing and advising in test case
        messageBlock.addTag("20","documentNumber");
        messageBlock.addTag("25","reimbursingBankAccountNumber");
        mt740.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("740");
        mt740.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt740);
        when(swiftMessageBuilder.build(eq("740"), any(DomainEvent.class))).thenReturn(messages);


        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(null,null,"");
        swiftMessageSender.generateLetterOfCreditCreationMessages(event);

        verify(mtMessageRepository).persist(any(MtMessage.class));
        verify(swiftMessageBuilder).build(eq("740"), any(LetterOfCreditCreatedEvent.class));
        verify(swiftMessageService).sendMessage(mt740);
    }
    
    @Ignore
    public void generateMT747ForLetterOfCreditCreatedEvent() throws ValidationException {
    	//TODO:Resolve, sender function is empty
        MT747 mt747 = new MT747();
        MessageBlock messageBlock = new MessageBlock();
        //TODO: include reimbursing and advising in test case
        messageBlock.addTag("20","documentNumber");
        messageBlock.addTag("25","reimbursingBankAccountNumber");
        mt747.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("747");
        mt747.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt747);
        when(swiftMessageBuilder.build(eq("747"), any(DomainEvent.class))).thenReturn(messages);


        LCAmendedEvent event = new LCAmendedEvent(null, null, null, "");
        swiftMessageSender.generateLcAmendmentStandbyMessages(event);

        verify(mtMessageRepository).persist(any(MtMessage.class));
        verify(swiftMessageBuilder).build(eq("747"), any(LCAmendedEvent.class));
        verify(swiftMessageService).sendMessage(mt747);
    }
    
    @Test
    public void generateMT707ForLCAmendedEvent() throws ValidationException {
        MT707 mt707 = new MT707();
        MessageBlock messageBlock = new MessageBlock();
       
        messageBlock.addTag("20","documentNumber");
        messageBlock.addTag("23","furtherIdentification");
        mt707.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("707");
        mt707.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt707);
        when(swiftMessageBuilder.build(eq("707"), any(DomainEvent.class))).thenReturn(messages);


        TradeService ts=new TradeService(null, null, null, null, null, DocumentSubType1.CASH,
        		null, ServiceType.AMENDMENT,
        		new UserActiveDirectoryId());
        
        LCAmendedEvent event = new LCAmendedEvent(ts,null,null,"");
        swiftMessageSender.generateLcAmendmentStandbyMessages(event);

        verify(mtMessageRepository).persist(any(MtMessage.class));
        verify(swiftMessageBuilder).build(eq("707"), any(LCAmendedEvent.class));
        verify(swiftMessageService).sendMessage(mt707);
    }
    
    @Test
    public void generateMT767ForLCAmendedEvent() throws ValidationException {
        MT767 mt767 = new MT767();
        MessageBlock messageBlock = new MessageBlock();
       
        messageBlock.addTag("20","documentNumber");
        messageBlock.addTag("23","furtherIdentification");
        mt767.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("767");
        mt767.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt767);
        when(swiftMessageBuilder.build(eq("767"), any(DomainEvent.class))).thenReturn(messages);


        TradeService ts=new TradeService(null, null, null, null, null, DocumentSubType1.STANDBY,
        		null, ServiceType.AMENDMENT,
        		new UserActiveDirectoryId());
        
        LCAmendedEvent event = new LCAmendedEvent(ts,null,null,"");
        swiftMessageSender.generateLcAmendmentStandbyMessages(event);

        verify(mtMessageRepository).persist(any(MtMessage.class));
        verify(swiftMessageBuilder).build(eq("767"), any(LCAmendedEvent.class));
        verify(swiftMessageService).sendMessage(mt767);
    }

    @Test
    public void generateMT750ForLcNegotiationDiscrepancyCreatedEvent() throws ValidationException {
        MT750 mt750 = new MT750();
        MessageBlock messageBlock = new MessageBlock();
       
        messageBlock.addTag("20","documentNumber");
        messageBlock.addTag("23","furtherIdentification");
        mt750.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("750");
        mt750.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt750);
        when(swiftMessageBuilder.build(eq("750"), any(DomainEvent.class))).thenReturn(messages);

        LCNegotiationDiscrepancyCreatedEvent event = new LCNegotiationDiscrepancyCreatedEvent(new TradeService(),new LCNegotiationDiscrepancy());
        swiftMessageSender.generateLcNegotiationDiscrepancy(event);

        verify(mtMessageRepository).persist(any(MtMessage.class));
        verify(swiftMessageBuilder).build(eq("750"), any(LCNegotiationDiscrepancyCreatedEvent.class));
        verify(swiftMessageService).sendMessage(mt750);
    }

    @Test
    public void generateMessagesForDpSettlementCreatedEvent() throws ValidationException {
    	MT103 mt103 = new MT103();
    	MessageBlock messageBlock = new MessageBlock();
    	messageBlock.addTag("20","documentNumber");
    	mt103.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header = new ApplicationHeader();
    	header.setMessageType("103");
    	mt103.setApplicationHeader(header);

        MT400 mt400 = new MT400();
        mt400.setMessageBlock(messageBlock);

        ApplicationHeader header400 = new ApplicationHeader();
    	header.setMessageType("400");
        mt400.setApplicationHeader(header400);

        MT202 mt202 = new MT202();
        mt202.setMessageBlock(messageBlock);

        ApplicationHeader header202 = new ApplicationHeader();
        header202.setMessageType("202");

        List<RawSwiftMessage> mt202messageList = new ArrayList<RawSwiftMessage>();
        mt202messageList.add(mt202);

        List<RawSwiftMessage> mt400MessageList = new ArrayList<RawSwiftMessage>();
        mt400MessageList.add(mt400);

        List<RawSwiftMessage> mt103MessageList = new ArrayList<RawSwiftMessage>();
        mt103MessageList.add(mt103);

        when(swiftMessageBuilder.build(eq("103"), any(TradeService.class))).thenReturn(mt103MessageList);
        when(swiftMessageBuilder.build(eq("400"), any(TradeService.class))).thenReturn(mt400MessageList);
        when(swiftMessageBuilder.build(eq("202"), any(TradeService.class))).thenReturn(mt202messageList);


        TradeService ts=new TradeService(null, null, null,null, DocumentType.DOMESTIC, null,
       		null, ServiceType.SETTLEMENT,
       		new UserActiveDirectoryId());
       
    	DPSettlementCreatedEvent event = new DPSettlementCreatedEvent(ts,null,"");
    	swiftMessageSender.generateDpSettlementMessage(event);
    	
    	verify(mtMessageRepository,times(3)).persist(any(MtMessage.class));
    	verify(swiftMessageBuilder).build(eq("103"), any(TradeService.class));
        verify(swiftMessageBuilder).build(eq("202"), any(TradeService.class));
        verify(swiftMessageBuilder).build(eq("400"), any(TradeService.class));

        verify(swiftMessageService).sendMessage(mt103);
        verify(swiftMessageService).sendMessage(mt202);
        verify(swiftMessageService).sendMessage(mt400);

    }

    @Test
    public void generateMT103ForDrSettlementCreatedEvent() throws ValidationException {
    	MT103 mt103 = new MT103();
    	MessageBlock messageBlock = new MessageBlock();
    	
    	messageBlock.addTag("20","documentNumber");
    	mt103.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header = new ApplicationHeader();
    	header.setMessageType("103");
    	mt103.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt103);

        when(swiftMessageBuilder.build(eq("103"), any(TradeService.class))).thenReturn(messages);
    	
    	TradeService ts=new TradeService(null, null, null,null, DocumentType.FOREIGN, null,
    			null, ServiceType.SETTLEMENT,
    			new UserActiveDirectoryId());
    	
    	DRSettlementCreatedEvent event = new DRSettlementCreatedEvent(ts,null,"");
    	swiftMessageSender.generateDrSettlementMessage(event);
    	
    	verify(mtMessageRepository).persist(any(MtMessage.class));
    	verify(swiftMessageBuilder).build(eq("103"), any(TradeService.class));
    	verify(swiftMessageService).sendMessage(mt103);
    }

    @Test
    public void generateMT103ForOaSettlementCreatedEvent() throws ValidationException {
    	MT103 mt103 = new MT103();
    	MessageBlock messageBlock = new MessageBlock();
    	
    	messageBlock.addTag("20","documentNumber");
    	mt103.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header = new ApplicationHeader();
    	header.setMessageType("103");
    	mt103.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt103);
        when(swiftMessageBuilder.build(eq("103"), any(TradeService.class))).thenReturn(messages);
    	
    	TradeService ts=new TradeService(null, null, null,null, DocumentType.FOREIGN, null,
    			null, ServiceType.SETTLEMENT,
    			new UserActiveDirectoryId());
    	
    	OASettlementCreatedEvent event = new OASettlementCreatedEvent(ts,null,"");
    	swiftMessageSender.generateOaSettlementMessage(event);
    	
    	verify(mtMessageRepository).persist(any(MtMessage.class));
    	verify(swiftMessageBuilder).build(eq("103"), any(TradeService.class));
    	verify(swiftMessageService).sendMessage(mt103);
    }

    @Test
    public void generateMT103ForDmlcNegotiationCreatedEvent() throws ValidationException {
    	MT103 mt103 = new MT103();
    	MessageBlock messageBlock = new MessageBlock();
    	
    	messageBlock.addTag("20","documentNumber");
    	mt103.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header = new ApplicationHeader();
    	header.setMessageType("103");
    	mt103.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt103);
        when(swiftMessageBuilder.build(eq("103"), any(TradeService.class))).thenReturn(messages);
    	
    	TradeService ts=new TradeService(null, null, null,null, DocumentType.DOMESTIC, null,
    			null, ServiceType.NEGOTIATION,
    			new UserActiveDirectoryId());
    	
    	LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(ts,null,"");
    	swiftMessageSender.generateMt752Mt202AndMt103ForLcNegotiation(event);
    	
    	verify(mtMessageRepository).persist(any(MtMessage.class));
    	verify(swiftMessageBuilder).build(eq("103"), any(TradeService.class));
    	verify(swiftMessageService).sendMessage(mt103);
    }

    @Test
    public void generateMt202ForUaLoanSettlement() throws ValidationException {
    	MT202 mt202 = new MT202();
    	MessageBlock messageBlock = new MessageBlock();
    	
    	messageBlock.addTag("20","documentNumber");
    	mt202.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header = new ApplicationHeader();
    	header.setMessageType("202");
    	mt202.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt202);

        when(swiftMessageBuilder.build(eq("202"), any(TradeService.class))).thenReturn(messages);
    	
    	TradeService ts=new TradeService(null, null, null,null, DocumentType.FOREIGN, null,
    			null, ServiceType.NEGOTIATION,
    			new UserActiveDirectoryId());
    	
    	UALoanSettledEvent event = new UALoanSettledEvent(ts);
    	swiftMessageSender.generateMt202AndMt103ForUaLoanSettlement(event);
    	
    	verify(mtMessageRepository).persist(any(MtMessage.class));
    	verify(swiftMessageBuilder).build(eq("202"), any(TradeService.class));
    	verify(swiftMessageService).sendMessage(mt202);
    }
    
    @Test
    public void generateMT400ForDpSettlementCreatedEvent() throws ValidationException {
    	MT400 mt400 = new MT400();
    	MessageBlock messageBlock = new MessageBlock();
    	
    	messageBlock.addTag("20","documentNumber");
    	mt400.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header = new ApplicationHeader();
    	header.setMessageType("400");
    	mt400.setApplicationHeader(header);
    	
    	
    	MT202 mt202 = new MT202();
    	mt202.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header2 = new ApplicationHeader();
    	header2.setMessageType("202");
    	mt202.setApplicationHeader(header);

        List<RawSwiftMessage> mt400List = new ArrayList<RawSwiftMessage>();
        mt400List.add(mt400);

        List<RawSwiftMessage> mt202List = new ArrayList<RawSwiftMessage>();
        mt202List.add(mt202);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();

        when(swiftMessageBuilder.build(eq("400"), any(TradeService.class))).thenReturn(mt400List);
    	when(swiftMessageBuilder.build(eq("202"), any(TradeService.class))).thenReturn(mt202List);

    	TradeService ts=new TradeService(null, null, null,null, DocumentType.FOREIGN, null,
    			null, ServiceType.NEGOTIATION,
    			new UserActiveDirectoryId());
    	
    	DPSettlementCreatedEvent event = new DPSettlementCreatedEvent(ts,null,"");
    	swiftMessageSender.generateDpSettlementMessage(event);
    	
    	verify(mtMessageRepository,times(2)).persist(any(MtMessage.class));
    	verify(swiftMessageBuilder).build(eq("400"), any(TradeService.class));
    	verify(swiftMessageService).sendMessage(mt400);
    }
    
    @Test
    public void generateMT410ForDaCreatedEvent() throws ValidationException {
    	MT410 mt410 = new MT410();
    	MessageBlock messageBlock = new MessageBlock();
    	
    	messageBlock.addTag("20","documentNumber");
    	mt410.setMessageBlock(messageBlock);
    	
    	ApplicationHeader header = new ApplicationHeader();
    	header.setMessageType("410");
    	mt410.setApplicationHeader(header);

        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        messages.add(mt410);

        when(swiftMessageBuilder.build(eq("410"), any(TradeService.class))).thenReturn(messages);
    	
    	TradeService ts=new TradeService(null, null, null,null, DocumentType.FOREIGN, null,
    			null, ServiceType.NEGOTIATION,
    			new UserActiveDirectoryId());
    	
    	DACreatedEvent event = new DACreatedEvent(ts,"");
    	swiftMessageSender.generateDaCreatedEventMessages(event);
    	
    	verify(mtMessageRepository).persist(any(MtMessage.class));
    	verify(swiftMessageBuilder).build(eq("410"), any(TradeService.class));
    	verify(swiftMessageService).sendMessage(mt410);
    }
}