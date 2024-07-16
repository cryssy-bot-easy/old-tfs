package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftAddress;
import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.splitter.Splitter;
import com.ucpb.tfs.swift.message.splitter.SplitterFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 */
public class SwiftMessageBuilder {

    public static final String SWIFT_NEW_LINE = "\r\n";
    public static final String DESTINATION_TAG = "79";
    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Map<String,String> destinationMapping;
    private Map<String,String> staticMappings;
    private SplitterFactory splitterFactory;
    private List<RawSwiftMessageBuilder> builders;


    /**
     * Wraps the input swift message into an x99 message.
     * The contents of the input swift message will be placed into a tag in the x99 message.
     *
     * @param rawSwiftMessage
     * @return The wrapped x99 message
     */
    public RawSwiftMessage wrap(RawSwiftMessage rawSwiftMessage){
        Assert.notNull(rawSwiftMessage,"Input swift message must not be null!");
        Assert.notNull(rawSwiftMessage.getApplicationHeader(),"Input swift message must contain an application header!");
        Assert.notNull(rawSwiftMessage.getApplicationHeader().getMessageType(),"Message type must not be null!");

        RawSwiftMessage wrapper = new RawSwiftMessage();
        wrapper.setApplicationHeader(rawSwiftMessage.getApplicationHeader());
        wrapper.setBasicHeader(rawSwiftMessage.getBasicHeader());
        wrapper.setTrailer(rawSwiftMessage.getTrailer());
        wrapper.setUserHeader(rawSwiftMessage.getUserHeader());

        String originalMessageType = rawSwiftMessage.getApplicationHeader().getMessageType();
        wrapper.getApplicationHeader().setMessageType(originalMessageType.charAt(0) + "99");

        copyMessageContentsToField(rawSwiftMessage,wrapper);

        return wrapper;
    }


    public List<RawSwiftMessage> build(String type, Object source){
        RawSwiftMessage message = buildCompositeSwiftMessage(source, type);
        Splitter splitter = splitterFactory.getInstance(message.getMessageType());
        List<RawSwiftMessage> messageList = new ArrayList<RawSwiftMessage>();
        if(splitter != null){
            messageList.addAll(splitter.split(message));
        }else{
            messageList.add(message);
        }
        return messageList;
    }


    public RawSwiftMessage buildCompositeSwiftMessage(Object source, String type){
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();

        for(RawSwiftMessageBuilder builder : builders){
            builder.build(source,type,rawSwiftMessage);
        }

        rawSwiftMessage.setReceiverAddress(getDestination(source,type));

        if(staticMappings != null){
            mapStaticValues(rawSwiftMessage);
        }

        return rawSwiftMessage;
    }

    public void setDestinationMapping(Map<String, String> destinationMapping) {
        this.destinationMapping = destinationMapping;
    }

    public void setSplitterFactory(SplitterFactory splitterFactory) {
        this.splitterFactory = splitterFactory;
    }

    private SwiftAddress getDestination(Object source,String type){
        String destinationExpression = destinationMapping.get(type);
        Expression parsedExpression = parser.parseExpression(destinationExpression);
        SwiftAddress swiftAddress = new SwiftAddress();
        String address = (String)parsedExpression.getValue(new StandardEvaluationContext(source));

        if(!StringUtils.hasText(address)){
            return null;
        }else{
        	address = address.toUpperCase();
        }

        if(address.length() == 11 || address.length() == 12){
            swiftAddress.setCompleteAddress(address);
        }else{
            swiftAddress.setBankIdentifierCode(address);
        }
        return swiftAddress;
    }

    public void mapStaticValues(RawSwiftMessage rawSwiftMessage){
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(rawSwiftMessage);
        wrapper.setAutoGrowNestedPaths(true);
        for(Map.Entry<String,String> mapping : staticMappings.entrySet()){
            wrapper.setPropertyValue(mapping.getKey(),mapping.getValue());
        }
    }

    public void setStaticMappings(Map<String, String> staticMappings) {
        this.staticMappings = staticMappings;
    }

    public void setBuilders(List<RawSwiftMessageBuilder> builders) {
        this.builders = builders;
    }


    private void copyMessageContentsToField(RawSwiftMessage source, RawSwiftMessage destination){
        StringBuilder tagContents = new StringBuilder();
        Iterator<Tag> iterator = source.getTags().iterator();
        while(iterator.hasNext()){
            Tag tag = iterator.next();
            tagContents.append(":" + tag.getTagName() + ":" + tag.getValue());
            if(iterator.hasNext()){
                tagContents.append(SWIFT_NEW_LINE);
            }
        }
        destination.addTag(DESTINATION_TAG,tagContents.toString());
    }
}
