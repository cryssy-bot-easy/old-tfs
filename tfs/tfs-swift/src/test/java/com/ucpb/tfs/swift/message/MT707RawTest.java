package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:swift/config/swift-builder.xml")
public class MT707RawTest {

    @Autowired
    @Qualifier("swiftMessageBuilder")
    private SwiftMessageBuilder swiftMessageBuilder;

    private Map<String,String> source = new HashMap<String,String>();

    @Before
    public void cleanup(){
        source.clear();
        source.put("numberOfAmendments","0");
    }


    @Test
    public void mapIncreaseInAmountToField32B(){
        source.put("amountSwitchDisplay","on");
        source.put("productAmount","100,000,000.00");
        source.put("currency","PHP");
        source.put("amountTo","200,000,000.00");

        RawSwiftMessage swiftMessage = buildMt707();

        Tag field32B = swiftMessage.getMessageBlock().getTagByName("32B");
        assertNotNull(field32B);
        assertEquals("PHP100000000,", field32B.getValue());
        assertEquals("PHP200000000,", swiftMessage.getMessageBlock().getTagByName("34B").getValue());
    }

    @Test
    public void mapIncreaseInAmountToField33B(){
        source.put("amountSwitchDisplay","on");
        source.put("productAmount","200,000,000.00");
        source.put("currency","PHP");
        source.put("amountTo","100,000,000.00");

        RawSwiftMessage swiftMessage = buildMt707();

        Tag field33B = swiftMessage.getMessageBlock().getTagByName("33B");
        assertNotNull(field33B);
        assertEquals("PHP100000000,", field33B.getValue());
        assertEquals("PHP100000000,", swiftMessage.getMessageBlock().getTagByName("34B").getValue());
    }

    @Test
    public void doNotMapPercentageIfFlagIsFalse(){
        source.put("positiveToleranceLimitSwitchDisplay","off");
        source.put("negativeToleranceLimitSwitchDisplay","off");
        source.put("positiveToleranceLimitTo","12");
        source.put("'negativeToleranceLimitTo'","6");

        RawSwiftMessage swiftMessage = buildMt707();

        Tag field39A = swiftMessage.getMessageBlock().getTagByName("39A");
        assertNull(field39A);
    }

    @Test
    public void mapPercentageIfPositiveToleranceChanged(){
        source.put("positiveToleranceLimitSwitchDisplay","on");
        source.put("negativeToleranceLimitSwitchDisplay","off");
        source.put("positiveToleranceLimitTo","12");
        source.put("negativeToleranceLimitFrom","6");

        RawSwiftMessage swiftMessage = buildMt707();

        Tag field39A = swiftMessage.getMessageBlock().getTagByName("39A");
        assertNotNull(field39A);
        assertEquals("12/6",field39A.getValue());
    }

    @Test
    public void mapPercentageIfNegativeChanged(){
        source.put("positiveToleranceLimitSwitchDisplay","off");
        source.put("negativeToleranceLimitSwitchDisplay","on");
        source.put("positiveToleranceLimitFrom","12");
        source.put("negativeToleranceLimitTo","6");

        RawSwiftMessage swiftMessage = buildMt707();

        Tag field39A = swiftMessage.getMessageBlock().getTagByName("39A");
        assertNotNull(field39A);
        assertEquals("12/6",field39A.getValue());
    }

    @Test
    public void mapPercentageIfBothFieldsChanged(){
        source.put("positiveToleranceLimitSwitchDisplay","on");
        source.put("negativeToleranceLimitSwitchDisplay","on");
        source.put("positiveToleranceLimitTo","24");
        source.put("negativeToleranceLimitTo","6");

        RawSwiftMessage swiftMessage = buildMt707();

        Tag field39A = swiftMessage.getMessageBlock().getTagByName("39A");
        assertNotNull(field39A);
        assertEquals("24/6",field39A.getValue());
    }





    private RawSwiftMessage buildMt707(){
        List<RawSwiftMessage> messages = swiftMessageBuilder.build("707",source);
        assertNotNull("Configuration Error",messages.get(0));
        printMessage(messages.get(0));
        return messages.get(0);
    }

    private void printMessage(RawSwiftMessage message){
        for(Tag tag : message.getMessageBlock().getTags()){
            System.out.println(tag.getTagName() + ":" + tag.getValue());
        }
    }

}
