package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.MT700;
import com.ucpb.tfs.swift.message.SwiftMessage;
import com.ucpb.tfs.util.BeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 */
public class SwiftMessageBuilderTest {

    private SpelEvaluatingSwiftMessageBuilder builder;
    private BeanMapper beanMapper;

    @Before
    public void setup(){
        beanMapper = mock(BeanMapper.class);

        Map<String,BeanMapper<? extends SwiftMessage>> beanMappers = new HashMap<String,BeanMapper<? extends  SwiftMessage>>();
        beanMappers.put("mt707",beanMapper);

        when(beanMapper.map(any())).thenReturn(new MT700());
        builder = new SpelEvaluatingSwiftMessageBuilder(new HashMap<String,String>());
        builder.setBeanMappers(beanMappers);
    }

    @Test
    public void successfullyDelegatesToMapper(){
        Map<String,String> values = new HashMap<String,String>();
        SwiftMessage message = builder.build("mt707",values);

        assertNotNull(message);
        verify(beanMapper).map(values);
//        verify(mapper).map(values,com.ucpb.tfs.interfaces.swift.message.MT700.class);
    }

    @Test
    public void returnsNullOnUndeclaredMtMessage(){
        Map<String,String> values = new HashMap<String,String>();
        SwiftMessage message = builder.build("mt900000000000",values);
        assertNull(message);
    }

}
