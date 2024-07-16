package com.ucpb.tfs.interfaces.gateway.parser;

/**
 */
public class ParseException extends Exception {

    public ParseException(String message){
        super(message);
    }

    public ParseException(String message, Throwable e){
        super(message,e);
    }
}
