package com.ucpb.tfs.swift.message;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 */
public class UserTag {

    private String tag;
    private String value;

    public UserTag(String tag, String value){
        Assert.notNull(tag, "Tag name must not be null");
        this.tag = tag.toUpperCase();        
    	if(tag.equalsIgnoreCase("121")){
            this.value = value;
    	} else {
            this.value = value.toUpperCase();
    	}
    }

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }
}
