package com.ucpb.tfs.swift.message.splitter;

import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 */
public class SplitterFactory {

    private Map<String,Class<? extends Splitter>> registry;

    public SplitterFactory(Map<String,Class<? extends Splitter>> registry){
        this.registry = registry;
    }

    public Splitter getInstance(String messageType){
        Class splitterClass = registry.get(messageType);
        System.out.println("splitterClass:"+splitterClass);
        if(splitterClass != null){
            return (Splitter) BeanUtils.instantiate(splitterClass);
        }
        return null;
    }

}
