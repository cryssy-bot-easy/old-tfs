package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.UserHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:swift/swift-builder-integration-test.xml")
public class SwiftMessageBuilderIntegrationTest {
    @Autowired
    private SwiftMessageBuilder swiftMessageBuilder;

    private Map<String,String> source;

    @Before
    public void setup(){
        source = new HashMap<String,String>();
        source.put("documentNumber","THIS IS THE DOCUMENT NUMBER");
        source.put("destinationBank","ASAXCVAS");
    }

    @Test
    public void buildMT700FromMap(){
        List<RawSwiftMessage> messageList = swiftMessageBuilder.build("700",source);
        RawSwiftMessage swiftMessage = messageList.get(0);
        assertNotNull(swiftMessage);
        assertFalse(swiftMessage.getMessageBlock().getTags().isEmpty());
        for(Tag tag : swiftMessage.getMessageBlock().getTags()){
            System.out.println(tag.getTagName() + ":" + tag.getValue());
        }
        ApplicationHeader header = swiftMessage.getApplicationHeader();
        assertEquals("ASAXCVAS",header.getReceiverAddress().getBankIdentifierCode());
        assertEquals("THIS IS THE DOCUMENT NUMBER",swiftMessage.getMessageBlock().getTagByName("20").getValue());
//        Tag tag = swiftMessage.getMessageBlock().getTagByName("20");
//        assertNotNull(tag);
//        assertEquals("20",tag.getTagName());
//        assertEquals("THIS IS THE DOCUMENT NUMBER",tag.getValue());

        UserHeader userHeader = swiftMessage.getUserHeader();
        assertNotNull(userHeader);
        assertEquals("THIS IS A MESSAGE",userHeader.getTagValue("43"));
        assertEquals("STP",userHeader.getTagValue("119"));
    }
}