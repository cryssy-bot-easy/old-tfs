package com.ucpb.tfs2.application.service.casa.exception;

/**
 */
public class NonExistentBranchException extends Exception {

    public NonExistentBranchException(String message){
        super(message);
    }

    public NonExistentBranchException(String message,Throwable e){
        super(message,e);
    }



}
