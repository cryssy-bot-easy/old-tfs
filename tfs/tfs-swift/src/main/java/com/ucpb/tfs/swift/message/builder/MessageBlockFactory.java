package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 */
public class MessageBlockFactory implements SwiftMessageBlockBuilder<Object>, RawSwiftMessageBuilder {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map builders;

    private boolean mapNull = false;

    public MessageBlockFactory(Map builders){
        this.builders = builders;
    }

    @Override
    public MessageBlock build(Object source, String type) {
        Map<String,String> mapping = (Map<String, String>) builders.get(type);
        MessageBlock messageBlock = new MessageBlock();
        EvaluationContext evaluationContext = new StandardEvaluationContext(source);

        for(Map.Entry<String,String> entry : mapping.entrySet()){
            String tagValue = evaluateExpression(evaluationContext,entry.getKey());
            if(StringUtils.hasText(tagValue) || mapNull == true){
                messageBlock.addTag(entry.getValue(),tagValue);
            }
        }

        if("700".equals(type)){
        	if(messageBlock.getTagByName("27") == null){
        		messageBlock.addTag("27", "1/1",0);
        	} else if(messageBlock.getTagByName("27").getValue() == null || messageBlock.getTagByName("27").getValue() == "") {               
        		messageBlock.update("27", "1/1");
        	}
        }else if("701".equals(type)){        
        	if(messageBlock.getTagByName("27") == null){
        		messageBlock.addTag("27", "2/2",0);
        	} else if(messageBlock.getTagByName("27").getValue() == null || messageBlock.getTagByName("27").getValue() == "") {
        		messageBlock.update("27", "2/2");
        	}
        }else if("707".equals(type)){
        	if(messageBlock.getTagByName("27") == null){
        		messageBlock.addTag("27", "1/1",0);
        	} else if(messageBlock.getTagByName("27").getValue() == null || messageBlock.getTagByName("27").getValue() == "") {               
        		messageBlock.update("27", "1/1");
        	}
        }else if("708".equals(type)){        
        	if(messageBlock.getTagByName("27") == null){
        		messageBlock.addTag("27", "2/2",0);
        	} else if(messageBlock.getTagByName("27").getValue() == null || messageBlock.getTagByName("27").getValue() == "") {
        		messageBlock.update("27", "2/2");
        	}
        }
        
        return messageBlock;
    }

    public void setMapNull(boolean mapNull) {
        this.mapNull = mapNull;
    }

    private String evaluateExpression(EvaluationContext context,String expression){
        Expression parsedExpression = parser.parseExpression(expression);
        return (String)parsedExpression.getValue(context);
    }

    @Override
    public void build(Object source, String type, RawSwiftMessage message) {
        message.setMessageBlock(build(source,type));
    }
}
