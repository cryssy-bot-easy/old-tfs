package com.ucpb.tfs.domain.casa.exceptions;

/**
 * Created by Marv on 2/27/14.
 */
public class RefCasaAccountParseException extends Exception {

    public RefCasaAccountParseException(String message, Throwable e){
        super(message,e);
    }

    public RefCasaAccountParseException(String message){
        super(message);
    }

}
