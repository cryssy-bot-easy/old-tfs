package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

/**
 */
//TODO: deprecate this. refactor design to accomodate mt103 and mt103Plus
public class SpelEvaluatingApplicationHeaderBuilder implements ApplicationHeaderBuilder<Object>, RawSwiftMessageBuilder {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<Expression,String> headerMapping = new HashMap<Expression,String>();

    public SpelEvaluatingApplicationHeaderBuilder(Map<String,String> headerMapping){
        for(Map.Entry<String,String> entry : headerMapping.entrySet()){
            this.headerMapping.put(parser.parseExpression(entry.getKey()),entry.getValue());
        }
    }

    @Override
    public ApplicationHeader build(Object source, String type) {
        ApplicationHeader applicationHeader = new ApplicationHeader();
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(applicationHeader);
        wrapper.setAutoGrowNestedPaths(true);
        EvaluationContext evaluationContext = new StandardEvaluationContext(source);

        for(Map.Entry<Expression,String> entry : headerMapping.entrySet()){
            wrapper.setPropertyValue(entry.getValue(),entry.getKey().getValue(evaluationContext));
        }
        //TODO: FIX the framework to accomodate this situation. Remove if-else scenario
        if("103Plus".equalsIgnoreCase(type)){
            type = "103";
        }
        applicationHeader.setMessageType(type);
        return applicationHeader;
    }

    @Override
    public void build(Object source, String type, RawSwiftMessage message) {
        message.setApplicationHeader(build(source,type));
    }
}
