package com.ucpb.tfs.swift.message;

import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 */
public class MessageFactory <T extends RawSwiftMessage>{

    private Map<String,Class<T>> registry;

    public MessageFactory(Map<String,Class<T>> registry){
        this.registry = registry;
    }

    public T getInstance(String type){
        Class<T> messageClass = registry.get(type);
        if(messageClass != null){
            return BeanUtils.instantiate(messageClass);
        }
        return null;
    }

}
