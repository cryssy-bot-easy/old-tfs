package com.ucpb.tfs.utils;

import org.springframework.beans.BeanUtils;
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
import static java.util.Map.Entry;

/**
 */
public class BeanMapper<T> {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<String,Expression> beanMappings = new HashMap<String,Expression>();
    private Class<T> targetClass;

    public BeanMapper(Map<String,String> properties,Class<T> targetClass){
        for(Entry<String,String> entry : properties.entrySet()){
            beanMappings.put(entry.getKey(),parser.parseExpression(entry.getValue()));
        }
        this.targetClass = targetClass;
    }


    public T map(Object sourceObject){
        T bean = BeanUtils.instantiate(targetClass);
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        wrapper.setAutoGrowNestedPaths(true);
        EvaluationContext evaluationContext = new StandardEvaluationContext(sourceObject);
        for(Entry<String,Expression> mapping : beanMappings.entrySet()){
            wrapper.setPropertyValue(mapping.getKey(),mapping.getValue().getValue(evaluationContext));
        }

        return bean;
    }


}
