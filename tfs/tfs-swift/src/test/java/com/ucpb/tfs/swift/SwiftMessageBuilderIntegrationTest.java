package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.MT700;
import com.ucpb.tfs.swift.message.MT760;
import com.ucpb.tfs.swift.message.SwiftMessage;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
@Ignore("Test class is no longer valid. SwiftMessage and SpelEvaluatingSwiftMessageBuilder have been deprecated")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:parser-config.xml")
public class SwiftMessageBuilderIntegrationTest {

    @Autowired
    private SpelEvaluatingSwiftMessageBuilder swiftMessageBuilder;


    @Test
    public void successfullyMapToMT700(){
        SwiftMessage message = swiftMessageBuilder.build("700",new HashMap<String, String>());
        assertNotNull(message);
        assertTrue(message instanceof MT700);
    }

    @Test
    public void successfullyMapToMT760(){
    	SwiftMessage message = swiftMessageBuilder.build("760",new HashMap<String, String>());
    	assertNotNull(message);
    	assertTrue(message instanceof MT760);
    }

    @Test
    public void successfullyMapFieldFromMap(){
        Map<String,String> input = new HashMap<String,String>();
        input.put("generalDescriptionOfGoods","GENERAL GOODS AND SERVICES");
        Wrapper wrapper = new Wrapper(input);

        MT700 message = (MT700)swiftMessageBuilder.build("700",input);
        assertNotNull(input);
        assertEquals("GENERAL GOODS AND SERVICES",message.getField45A());

    }

    @Test
    public void mapOnlyNotNullValue(){
        Map<String,String> input = new HashMap<String,String>();
        input.put("documentNumber","document-number-1");

        Wrapper wrapper = new Wrapper(input);

        MT700 message = (MT700)swiftMessageBuilder.build("700",input);
        assertNotNull(input);
        assertEquals("document-number-1",message.getField20());
    }

    @Test
    public void mapOnlyNotNullValue2(){
        Map<String,String> input = new HashMap<String,String>();
        input.put("20",null);
        input.put("documentNumber","document-number-1");

        Wrapper wrapper = new Wrapper(input);


        MT700 message = (MT700)swiftMessageBuilder.build("700",input);
        assertNotNull(input);
        assertEquals("document-number-1",message.getField20());
    }
}