package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.BasicHeader;
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
public class SpelEvaluatingBasicHeaderBuilder implements BasicHeaderBuilder<Object>, RawSwiftMessageBuilder {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<Expression,String> basicHeaderMapping = new HashMap<Expression,String>();

    public SpelEvaluatingBasicHeaderBuilder(Map<String,String> mapping){
        for(Map.Entry<String,String> entry : mapping.entrySet()){
            this.basicHeaderMapping.put(parser.parseExpression(entry.getKey()),entry.getValue());
        }
    }


    @Override
    public BasicHeader build(Object source, String type) {
        BasicHeader basicHeader = new BasicHeader();
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(basicHeader);
        wrapper.setAutoGrowNestedPaths(true);

        EvaluationContext evaluationContext = new StandardEvaluationContext(source);
        for(Map.Entry<Expression,String> entry : basicHeaderMapping.entrySet()){
            wrapper.setPropertyValue(entry.getValue(),entry.getKey().getValue(evaluationContext));
        }
        return basicHeader;
    }

    @Override
    public void build(Object source, String type, RawSwiftMessage message) {
        message.setBasicHeader(build(source,type));
    }
}
