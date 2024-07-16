package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.MessageBlock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
public class SpelEvaluatingMessageBlockBuilderTest {

    private SpelEvaluatingMessageBlockBuilder builder;

    @Before
    public void setup(){
        Map<String,String> mapping = new HashMap<String,String>();
        mapping.put("'hello'","27");
        mapping.put("'world'","38");
        mapping.put("'!'","19");
        builder = new SpelEvaluatingMessageBlockBuilder(mapping);

    }

    @Ignore("Removed invalid character substitution")
    @Test
    public void mapEverythingToTags(){
        Object source = new Object();
        MessageBlock messageBlock = builder.build(source, null);
        assertNotNull(messageBlock.getTagByName("27"));
        assertEquals("HELLO", messageBlock.getTagByName("27").getValue());
        assertEquals("WORLD",messageBlock.getTagByName("38").getValue());
        assertEquals("??4F",messageBlock.getTagByName("19").getValue());
    }




}
