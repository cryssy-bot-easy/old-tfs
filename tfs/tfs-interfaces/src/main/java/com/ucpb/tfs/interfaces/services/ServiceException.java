package com.ucpb.tfs.interfaces.services;

import java.io.IOException;

/**
 */
public class ServiceException extends IOException {

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(String message, Throwable e){
        super(message,e);
    }
}
