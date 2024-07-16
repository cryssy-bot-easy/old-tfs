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

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SpelEvaluatingMessageBlockBuilder implements SwiftMessageBlockBuilder<Object>,RawSwiftMessageBuilder {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<Expression,String> tagMapping = new HashMap<Expression,String>();

    private boolean mapNull = false;


    public SpelEvaluatingMessageBlockBuilder(Map<String, String> tagMapping) {
        for(Map.Entry<String,String> entry : tagMapping.entrySet()){
            this.tagMapping.put(parser.parseExpression(entry.getKey()),entry.getValue());
        }
    }


    @Override
    public MessageBlock build(Object source, String type) {
        MessageBlock messageBlock  = new MessageBlock();
        EvaluationContext evaluationContext = new StandardEvaluationContext(source);
        for(Map.Entry<Expression,String> entry : tagMapping.entrySet()){
            String tagValue = (String)entry.getKey().getValue(evaluationContext);
            if(StringUtils.hasText(tagValue) || mapNull == true){
                messageBlock.addTag(entry.getValue(),tagValue);
            }
        }
        if("700".equals(type)){
        	if(messageBlock.getTagByName("27") == null){
        		messageBlock.addTag("27", "1/1",0);
        	} else if(messageBlock.getTagByName("27").getValue() == null || messageBlock.getTagByName("27").getValue().equals("")) {
        		messageBlock.update("27", "1/1");
        	}
        }else if("701".equals(type)){        
        	if(messageBlock.getTagByName("27") == null){
        		messageBlock.addTag("27", "2/2",0);
        	} else if(messageBlock.getTagByName("27").getValue() == null || messageBlock.getTagByName("27").getValue().equals("")) {
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

    @Override
    public void build(Object source, String type, RawSwiftMessage message) {
        message.setMessageBlock(build(source,type));
    }
}
