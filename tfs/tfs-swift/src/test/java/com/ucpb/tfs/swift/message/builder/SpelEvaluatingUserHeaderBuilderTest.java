package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.UserHeader;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 */
public class SpelEvaluatingUserHeaderBuilderTest {

    private SpelEvaluatingUserHeaderBuilder builder;


    @Before
    public void setup(){
        LinkedHashMap mappings = new LinkedHashMap<String,Map<String, String>>();

        Map<String,String> mt103Mapping = new HashMap<String,String>();
        mt103Mapping.put("33","'this is tag 33'");
        mt103Mapping.put("SOMETAG","'this is a message'");
        mappings.put("103",mt103Mapping);

        builder = new SpelEvaluatingUserHeaderBuilder(mappings);
    }


    @Test
    public void successfullyBuildUserHeaderForTypeWithExistingMapping(){
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();
        builder.build(new Object(),"103",rawSwiftMessage);

        UserHeader header = rawSwiftMessage.getUserHeader();
        assertNotNull(header);
        assertEquals("THIS IS TAG 33",header.getTagValue("33"));
        assertEquals("THIS IS A MESSAGE",header.getTagValue("SOMETAG"));
    }

    @Test
    public void returnNullForUnconfiguredType(){
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();
        builder.build(new Object(),"NON-EXISTENT-MESSAGE-TYPE",rawSwiftMessage);
        assertNull(rawSwiftMessage.getUserHeader());
    }




}
