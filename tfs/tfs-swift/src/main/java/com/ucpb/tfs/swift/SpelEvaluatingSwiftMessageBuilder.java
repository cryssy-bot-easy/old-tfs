package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.SwiftMessage;
import com.ucpb.tfs.util.BeanMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class SpelEvaluatingSwiftMessageBuilder implements SwiftMessageBuilder {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<String,Class<? extends SwiftMessage>> messages;

    private Map<String,BeanMapper<? extends SwiftMessage>> beanMappers;

    private Map<String,Expression> propertyMapping = new HashMap<String,Expression>();

    public SpelEvaluatingSwiftMessageBuilder(Map<String, String> propertyMapping){
        for(Map.Entry<String,String> entry : propertyMapping.entrySet()){
            this.propertyMapping.put(entry.getKey(), parser.parseExpression(entry.getValue()));
        }
    }


    @Override
    public SwiftMessage build(String type, Object fields){
        BeanMapper<? extends SwiftMessage> beanMapper = beanMappers.get(type);
        if(beanMapper == null){
            return null;
        }
        SwiftMessage message =  beanMapper.map(fields);
        mapProperties(message, fields);
        message.getApplicationHeader().setMessageType(type);
        return message;
    }

    public void setBeanMappers(Map<String, BeanMapper<? extends SwiftMessage>> beanMappers) {
        this.beanMappers = beanMappers;
    }

    private void mapProperties(SwiftMessage message, Object fields){
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(message);
        wrapper.setAutoGrowNestedPaths(true);
        for(Map.Entry<String,Expression> entry : propertyMapping.entrySet()){
            wrapper.setPropertyValue(entry.getKey(),entry.getValue().getValue(fields));
        }
    }

}
