package com.ucpb.tfs.interfaces.services.impl;

/**
 */
public class NonExistentLoanException extends Exception{

    public NonExistentLoanException(){

    }

    public NonExistentLoanException(String message){
        super(message);
    }
}
