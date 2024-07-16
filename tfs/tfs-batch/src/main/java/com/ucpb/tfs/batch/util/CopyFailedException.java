package com.ucpb.tfs.batch.util;

/**
 */
public class CopyFailedException extends Exception {

    public CopyFailedException(String message){
        super(message);
    }

    public CopyFailedException(String message, Throwable t){
        super(message,t);
    }

}
