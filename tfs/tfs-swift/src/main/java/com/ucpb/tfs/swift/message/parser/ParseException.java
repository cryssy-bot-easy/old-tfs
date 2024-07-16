package com.ucpb.tfs.swift.message.parser;

/**
 */
public class ParseException extends Exception {

    public ParseException(String message){
        super(message);
    }

    public ParseException(String message,Throwable e){
        super(message,e);
    }

}
