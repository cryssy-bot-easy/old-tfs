package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.SwiftMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 */
public class DefaultSwiftMessageBuilder implements SwiftMessageBuilder {

    private final Map<String,Class<SwiftMessage>> mtTypeRegistry;

    public DefaultSwiftMessageBuilder(Map<String,Class<SwiftMessage>> mtTypeRegistry){
        this.mtTypeRegistry = mtTypeRegistry;
    }

    @Override
    public SwiftMessage build(String type,  Object fields) {
        Map<String,String> sourceFields = (Map<String,String>)fields;
        Class<SwiftMessage> swiftMessageClass = mtTypeRegistry.get(type);
        if(swiftMessageClass == null){
            return null;
        }
        SwiftMessage message = BeanUtils.instantiate(swiftMessageClass);
        mapToSwiftMessage(message,sourceFields);
        return message;
    }

    private void mapToSwiftMessage(SwiftMessage message, Map<String,String> fields){
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(message);
        wrapper.setAutoGrowNestedPaths(true);

        for(Map.Entry<String,String> entry : fields.entrySet()){
            wrapper.setPropertyValue("field" + entry.getKey(), StringUtils.trimWhitespace(entry.getValue()));
        }
    }


}
