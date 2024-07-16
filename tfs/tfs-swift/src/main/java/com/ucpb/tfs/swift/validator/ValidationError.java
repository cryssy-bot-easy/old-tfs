package com.ucpb.tfs.swift.validator;

/**
 */
public class ValidationError {

    private String tag;
    private String value;
    private String message;

    public ValidationError(String message){
        this.message = message;
    }

    public ValidationError(String tag, String value, String message){
        this(message);
        this.tag = tag;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public String getTag() {
        return tag;
    }
}
