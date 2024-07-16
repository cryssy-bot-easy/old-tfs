package com.ucpb.tfs.functionaltests;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.product.LCNegotiationDiscrepancy;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.event.*;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:swift/message-builder.xml")
public class SwiftMessageMappingNullTest {

	@Autowired
    private SwiftMessageBuilder builder;

    @Test
    public void successfullyMapNullToMT700(){
        Map<String,Object> lcDetails = asMap();

        TradeService ts = new TradeService();
        
        LetterOfCredit letterOfCredit = new LetterOfCredit(null,lcDetails);
        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(ts,letterOfCredit,"");


        List<RawSwiftMessage> messages = builder.build("700",event.getTradeService());
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertNotNull(message);
        assertNull(block.getReference());
//        assertNull(block.getTagValue("27")); Value is static, skipped
        assertNull(block.getTagValue("40A"));
        assertNull(block.getTagValue("31C"));
        assertNull(block.getTagValue("40E"));
//        assertNull(block.getTagValue("31D")); Value contains static string, skipped
        assertNull(block.getTagValue("42C"));
        assertNull(block.getTagValue("42A"));
        assertNull(block.getTagValue("42M"));
        assertNull(block.getTagValue("42P"));
        assertNull(block.getTagValue("43P"));
        assertNull(block.getTagValue("43T"));
        assertNull(block.getTagValue("44A"));
        assertNull(block.getTagValue("44B"));
        assertNull(block.getTagValue("44E"));
        assertNull(block.getTagValue("44F"));
        assertNull(block.getTagValue("44C"));
    }  
    
    @Test
    public void successfullyMapNullToMT740(){

        Map<String,Object> lcDetails = asMap();
        LetterOfCredit letterOfCredit = new LetterOfCredit(null, lcDetails);
        LetterOfCreditCreatedEvent lcEvent = new LetterOfCreditCreatedEvent(new TradeService(), letterOfCredit,"");
        
        List<RawSwiftMessage> messages = builder.build("740",lcEvent.getTradeService());
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertNotNull(message);
        assertNull(block.getReference());
        assertNull(block.getTagValue("25"));
        assertNull(block.getTagValue("40F"));
        assertNull(block.getTagValue("31D"));
        assertNull(block.getTagValue("59"));
        assertNull(block.getTagValue("32B"));
        assertNull(block.getTagValue("39A"));
        assertNull(block.getTagValue("39B"));
        assertNull(block.getTagValue("39C"));
        assertNull(block.getTagValue("41A"));     
//        assertNull(block.getTagValue("41D"));        
        assertNull(block.getTagValue("42C"));
//        assertNull("",block.getTagValue("42A"));
        assertNull(block.getTagValue("42M"));
        assertNull(block.getTagValue("42P"));
//        assertNull("",block.getTagValue("71B"));
        assertNull(block.getTagValue("72"));
    }
    
    @Test
    public void successfullyMapNullToMT760(){
        Map<String,Object> lcDetails = asMap();
        
        TradeService ts = new TradeService();
        LetterOfCredit letterOfCredit = new LetterOfCredit(null,lcDetails);
        
        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(ts,letterOfCredit,"");

        List<RawSwiftMessage> messages = builder.build("760",event.getTradeService());
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertNotNull(message);
//        assertNull(block.getTagValue("27")); Value is static, skipped	
        assertNull(block.getReference());	
        assertNull(block.getTagValue("23"));	
        assertNull(block.getTagValue("30"));	
        assertNull(block.getTagValue("40C"));
        assertNull(block.getTagValue("77C"));
        assertNull(block.getTagValue("72"));
    }
    
    @Test
    public void successfullyMapNullToMT707(){
    	Map<String,Object> lcDetails = asMap();

    	LetterOfCredit letterOfCredit = new LetterOfCredit(null,lcDetails);
    	LCAmendedEvent e = new LCAmendedEvent(new TradeService(), letterOfCredit,letterOfCredit,"");
    	
		List<RawSwiftMessage> messages = builder.build("707",e.getTradeService());
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
    	printTags(block);
    	assertNotNull(message);
    	assertNull(block.getReference());	
//    	assertNull(block.getRelatedReference());Value is static, skipped
//    	assertNull(block.getTagValue("23"));	Value is static, skipped	
//    	assertNull(block.getTagValue("52A"));	Value is static, skipped	
//    	assertNull(block.getTagValue("52D"));	Value is static, skipped	
    	assertNull(block.getTagValue("31C"));
    	assertNull(block.getTagValue("30"));
//    	assertNull(block.getTagValue("26E"));
    	assertNull(block.getTagValue("59"));
    	assertNull(block.getTagValue("31E"));
    	assertNull(block.getTagValue("32B"));
    	assertNull(block.getTagValue("33B"));
    	assertNull(block.getTagValue("34B"));
    	assertNull(block.getTagValue("39A"));
    	assertNull(block.getTagValue("39B"));
    	assertNull(block.getTagValue("39C"));
    	assertNull(block.getTagValue("44A"));
    	assertNull(block.getTagValue("44E"));
    	assertNull(block.getTagValue("44F"));
    	assertNull(block.getTagValue("44B"));
    	assertNull(block.getTagValue("44C"));
    	assertNull(block.getTagValue("44D"));
    	assertNull(block.getTagValue("79"));
    	assertNull(block.getTagValue("72"));
    }
    
    @Test
    public void successfullyMapNullToMT747(){
    	Map<String,Object> lcDetails = asMap();

    	LetterOfCredit letterOfCredit = new LetterOfCredit(null,lcDetails);
    	LCAmendedEvent e = new LCAmendedEvent(new TradeService(), letterOfCredit,letterOfCredit,"");
    	
    	List<RawSwiftMessage> messages = builder.build("747",e.getTradeService());
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
    	printTags(block);
    	assertNotNull(message);
    	assertNull(block.getReference());	
//    	assertNull(block.getRelatedReference());Value is static, skipped	
//    	assertNull(block.getTagValue("30"));	Value is static, skipped
    	assertNull(block.getTagValue("31E"));	
    	assertNull(block.getTagValue("32B"));
    	assertNull(block.getTagValue("33B"));
    	assertNull(block.getTagValue("39B"));
    	assertNull(block.getTagValue("39C"));
    	assertNull(block.getTagValue("72"));
    	assertNull(block.getTagValue("77A"));
    }
    
    @Test
    public void successfullyMapNullToMT767(){
        Map<String,Object> lcDetails = asMap();
        LetterOfCredit letterOfCredit = new LetterOfCredit(null,lcDetails);
        LCAmendedEvent lcAmendedEvent = new LCAmendedEvent(new TradeService(), letterOfCredit, letterOfCredit,"");
        
        List<RawSwiftMessage> messages = builder.build("767",lcAmendedEvent.getTradeService());
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertNotNull(message);
        assertNull(block.getTagValue("20"));
//        assertNull(block.getTagValue("21"));	Value is static, skipped
//        assertNull(block.getTagValue("27"));	Value is static, skipped
        assertNull(block.getTagValue("23"));	
//        assertNull(block.getTagValue("30"));
//        assertNull(block.getTagValue("26E"));
        assertNull(block.getTagValue("31C"));	
        assertNull(block.getTagValue("77C"));	
        assertNull(block.getTagValue("72"));	
    }
    
    @Test
    public void successfullyMapNullToMT750(){
    	//TODO: Resolve static field values in mt750.xml
        LCNegotiationDiscrepancy lcNegoDiscrepancy = new LCNegotiationDiscrepancy();
        LCNegotiationDiscrepancyCreatedEvent lcNegoDiscrepancyEvent = new LCNegotiationDiscrepancyCreatedEvent(new TradeService(),lcNegoDiscrepancy);
       
        List<RawSwiftMessage> messages = builder.build("750",lcNegoDiscrepancyEvent.getTradeService());
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        
        assertNotNull(message);
        assertNull(block.getReference());	
        assertNull(block.getTagValue("21"));	      
        assertNull(block.getTagValue("72"));	      
    }
    
    @Test
    public void successfullyMapNullToMT752(){
    	//TODO: Resolve field values in mt752.xml
        TradeService ts=new TradeService();
        LCNegotiationCreatedEvent event=new LCNegotiationCreatedEvent(ts,null,"");

        List<RawSwiftMessage> messages = builder.build("752",event.getTradeService());
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertNotNull(message);
        assertNull(block.getReference());	
    }

    @Test
    public void successfullyMapNullToMT103(){
    	TradeService ts=new TradeService();
    	//any settlement created event
    	DPSettlementCreatedEvent event=new DPSettlementCreatedEvent(ts,null,"");
    	
    	List<RawSwiftMessage> messages = builder.build("103",event.getTradeService());
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
    	printTags(block);
    	assertNotNull(message);
    	assertNull(block.getReference());	
    }

    @Test
    public void successfulMappingOfMt202(){
        Map<String,Object> nonLcDetails = new HashMap<String,Object>();

        TradeService tradeService = new TradeService();
        tradeService.updateDetails(nonLcDetails,null);
        
        List<RawSwiftMessage> messages = builder.build("202", tradeService);
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
        printTags(block);	
    	
        assertNull(block.getReference());	
        assertNull(block.getRelatedReference());
        assertNull(block.getTagValue("32A"));
        assertNull(block.getTagValue("52A"));
        assertNull(block.getTagValue("53A"));
        assertNull(block.getTagValue("54A"));
        assertNull(block.getTagValue("56A"));
        assertNull(block.getTagValue("57A"));
        assertNull(block.getTagValue("58A"));
        assertNull(block.getTagValue("72"));
    }
    
    @Test
    public void successfullyMapNullToMT400(){
    	Map<String,Object> details = new HashMap<String,Object>();
    	
    	TradeService ts=new TradeService();
    	ts.updateDetails(details, null);
    	
		List<RawSwiftMessage> messages = builder.build("400",ts);
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
    	printTags(block);
    	assertNotNull(message);
    	assertNull(block.getReference());
    	assertNull(block.getRelatedReference());	
    	assertNull(block.getTagValue("32A"));	
    	assertNull(block.getTagValue("33A"));	
    	assertNull(block.getTagValue("72"));
    	assertNull(block.getTagValue("52A"));
    	assertNull(block.getTagValue("53A"));
    	assertNull(block.getTagValue("54A"));
    	assertNull(block.getTagValue("57A"));
    	assertNull(block.getTagValue("58A"));
//    	assertNull(block.getTagValue("71B")); Value is static, skipped
//    	assertNull(block.getTagValue("73"));  Value is static, skipped
    }
    
    @Test
    public void successfullyMapNullToMT410Da(){
        Map<String,Object> nonLcDetails = new HashMap<String,Object>();

        TradeService tradeService = new TradeService();
        tradeService.updateDetails(nonLcDetails, new UserActiveDirectoryId());
        
        List<RawSwiftMessage> messages = builder.build("410", tradeService);
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        assertNull(block.getTagValue("20"));	
        assertNull(block.getTagValue("21"));
        assertNull(block.getTagValue("32A"));
        assertNull(block.getTagValue("72"));
    }
    
    @Test
    public void successfullyMapNullToMT410Dp(){
        Map<String,Object> nonLcDetails = new HashMap<String,Object>();

        TradeService tradeService = new TradeService();
        tradeService.updateDetails(nonLcDetails, new UserActiveDirectoryId());
        
        List<RawSwiftMessage> messages = builder.build("410",tradeService);
        RawSwiftMessage message = messages.get(0);

        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        assertNull(block.getTagValue("20"));	
        assertNull(block.getTagValue("21"));
        assertNull(block.getTagValue("32A"));
        assertNull(block.getTagValue("72"));
    }
    
    
    ////////////////////////////////////////////
    private void printTags(MessageBlock block){
        for(Tag tag : block.getTags()){
            System.out.println(tag.getTagName() + ":" + tag);
        }
    }

	private Map<String,Object> asMap(Object... input){
        Assert.isTrue(input.length % 2 == 0, "Input is invalid. Length is uneven");

		Map<String,Object> map = new HashMap<String,Object>();
        for(int ctr = 0; ctr < input.length; ctr = ctr + 2){
            map.put(input[ctr].toString(),input[ctr+1]);
        }
        return map;
    }
}