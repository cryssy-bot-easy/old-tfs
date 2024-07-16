package com.ucpb.tfs.interfaces.services.exception;

/**
 *
 */
public class MessageTimeoutException extends RuntimeException {

    public MessageTimeoutException(Throwable e){
        super(e);
    }

    public MessageTimeoutException(String message, Throwable e){
        super(message,e);
    }



}
