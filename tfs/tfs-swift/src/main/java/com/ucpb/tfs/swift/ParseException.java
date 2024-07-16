package com.ucpb.tfs.swift;

/**
 */
public class ParseException extends Exception {

    public ParseException(String message, Throwable e){
        super(message,e);
    }

    public ParseException(String message){
        super(message);
    }
}
