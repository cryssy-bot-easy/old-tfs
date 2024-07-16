package com.ucpb.tfs.swift.message.splitter;

import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.mt7series.MT700;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.MessageChannel;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 */
public class SplitterFactoryTest {

    private SplitterFactory factory;

    @Before
    public void setup(){
        Map<String,Class<? extends Splitter>> splitterRegistry = new HashMap<String,Class<? extends Splitter>>();
        splitterRegistry.put("MT700",MT700Splitter.class);

        factory = new SplitterFactory(splitterRegistry);
    }

    @Test
    public void retrieveMatchingSplitter(){

        Splitter splitter = factory.getInstance("MT700");
        assertNotNull(splitter);
    }

}
