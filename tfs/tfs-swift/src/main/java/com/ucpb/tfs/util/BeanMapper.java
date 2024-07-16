package com.ucpb.tfs.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BeanMapper<T> {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<Expression,String> beanMappings = new HashMap<Expression,String>();
    private Class<T> targetClass;
    private boolean mapNull = false;
    private boolean trimStrings = false;

    public BeanMapper(Map<String, String> properties, Class<T> targetClass){
        for(Entry<String,String> entry : properties.entrySet()){
            beanMappings.put(parser.parseExpression(entry.getKey()),entry.getValue());
        }
        this.targetClass = targetClass;
    }


    public T map(Object sourceObject){
        T bean = BeanUtils.instantiate(targetClass);
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        wrapper.setAutoGrowNestedPaths(true);
        EvaluationContext evaluationContext = new StandardEvaluationContext(sourceObject);
        for(Entry<Expression,String> mapping : beanMappings.entrySet()){
            Object value = trim(mapping.getKey().getValue(evaluationContext));
            if(mapNull == false){
                if(!isNull(value)){
                    wrapper.setPropertyValue(mapping.getValue(),value);
                }
            }else{
                wrapper.setPropertyValue(mapping.getValue(),value);
            }
        }

        return bean;
    }

    public void setMapNull(boolean mapNull) {
        this.mapNull = mapNull;
    }

    public void setTrimStrings(boolean trimStrings) {
        this.trimStrings = trimStrings;
    }

    private Object trim(Object value){
        if(trimStrings == true && value instanceof String){
            return StringUtils.trimWhitespace((String)value);
        }
        return value;
    }

    private boolean isNull(Object value){
        if(value instanceof String){
            return !StringUtils.hasText((CharSequence) value);
        }
        return value == null;
    }
}
