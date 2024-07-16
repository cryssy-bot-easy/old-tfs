package com.ucpb.tfs.util;

import com.ucpb.tfs.utils.BeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.expression.spel.SpelParseException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 */
public class BeanMapperTest {

    private BeanMapper beanMapper;

    @Before
    public void setup(){
        Map<String,String> beanMappings = new HashMap<String,String>();
        beanMappings.put("stringTarget","string + ' World!'");
        beanMappings.put("integerTarget","integer + 2");
        beanMappings.put("bigDecimalTarget","bigDecimal.add(T(java.math.BigDecimal).ONE)");

        beanMapper = new BeanMapper(beanMappings,TargetObject.class);
    }

    @Test
    public void successfullyMapToTargetObject(){
        SourceObject source = new SourceObject();
        source.setBigDecimal(new BigDecimal("123"));
        source.setInteger(Integer.valueOf(3));
        source.setString("Hello");

        TargetObject target = (TargetObject) beanMapper.map(source);
        assertEquals("124",target.getBigDecimalTarget().toString());
        assertEquals(Integer.valueOf("5"),target.getIntegerTarget());
        assertEquals("Hello World!",target.getStringTarget());
    }

    @Test(expected = SpelParseException.class)
    public void parseExceptionOnInvalidExpressions(){
        Map<String,String> beanMappings = new HashMap<String,String>();
        beanMappings.put("stringTarget","INVALID ASKJDAKLDJ");
        beanMapper = new BeanMapper(beanMappings,TargetObject.class);

        SourceObject source = new SourceObject();
        source.setBigDecimal(new BigDecimal("123"));

        TargetObject target = (TargetObject) beanMapper.map(source);
    }





}
