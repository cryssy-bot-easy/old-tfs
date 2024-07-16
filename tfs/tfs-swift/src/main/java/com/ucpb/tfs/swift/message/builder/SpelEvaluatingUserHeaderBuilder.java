package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.UserHeader;
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


/**
 */
public class SpelEvaluatingUserHeaderBuilder implements RawSwiftMessageBuilder {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<String,Map<String,Expression>> headerMappings = new HashMap<String,Map<String,Expression>>();

    private boolean mapNull = false;

    //I removed the generic type for the constructor because spring does not currently support
    //nested generics e.g. Map<String,Map<String,String>>. There may be a cleaner way of fixing this,
    //but since I'm out of time, this will have to do.
    public SpelEvaluatingUserHeaderBuilder(Map headerMappings){
        for(Object mappings : headerMappings.entrySet()){
            HashMap<String,Expression> parsedExpressions = new HashMap<String,Expression>();

            Entry<String,Map<String,String>> typeToExpressionMappings = (Entry<String,Map<String,String>>)mappings;
            for(Entry<String,String> expressionsToParse : typeToExpressionMappings.getValue().entrySet()){
                parsedExpressions.put(expressionsToParse.getKey(),parser.parseExpression(expressionsToParse.getValue()));
            }
            this.headerMappings.put(typeToExpressionMappings.getKey(),parsedExpressions);
        }
    }


    @Override
    public void build(Object source, String type, RawSwiftMessage message) {

        Map<String,Expression> userHeaderMappings = headerMappings.get(type);
        if(userHeaderMappings != null){
            UserHeader userHeader = new UserHeader();
            EvaluationContext evaluationContext = new StandardEvaluationContext(source);
            for(Entry<String,Expression> mapping : userHeaderMappings.entrySet()){
                String tagValue = evaluateExpression(evaluationContext,mapping.getKey());
                if(StringUtils.hasText(tagValue) || mapNull == true){
                    userHeader.addUserTag(tagValue,mapping.getValue().getValue(evaluationContext).toString());
                }
            }
            if(!userHeader.isEmpty()){
            	message.setUserHeader(userHeader);            	
            }
        }
    }
    
    private String evaluateExpression(EvaluationContext context,String expression){
        Expression parsedExpression = parser.parseExpression(expression);
        return (String)parsedExpression.getValue(context);
    }

    public void setMapNull(boolean mapNull) {
        this.mapNull = mapNull;
    }
}
