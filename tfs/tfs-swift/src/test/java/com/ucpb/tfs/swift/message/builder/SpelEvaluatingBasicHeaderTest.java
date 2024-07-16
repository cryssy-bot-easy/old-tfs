package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 */
public class SpelEvaluatingBasicHeaderTest {

    private SpelEvaluatingBasicHeaderBuilder builder;

    @Before
    public void setup(){
        Map<String,String> mapping = new HashMap<String,String>();
        mapping.put("'hello'","applicationIdentifier");
        mapping.put("'world'","serviceIndentifier");
        mapping.put("'1'","ltIdentifier");
        mapping.put("'2'","sessionNumber");
        mapping.put("'3'","sequenceNumber");
        builder = new SpelEvaluatingBasicHeaderBuilder(mapping);
    }

    @Test
    public void mapValuesToBasicHeader(){
        BasicHeader header = builder.build(new Object(), null);
        assertEquals("hello",header.getApplicationIdentifier());
        assertEquals("world",header.getServiceIndentifier());
        assertEquals("1",header.getLtIdentifier());
        assertEquals("2",header.getSessionNumber());
        assertEquals("3",header.getSequenceNumber());
    }

}
