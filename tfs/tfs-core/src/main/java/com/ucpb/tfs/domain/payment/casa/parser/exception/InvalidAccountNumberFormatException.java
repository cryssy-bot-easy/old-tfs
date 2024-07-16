package com.ucpb.tfs.domain.payment.casa.parser.exception;

/**
 */
public class InvalidAccountNumberFormatException extends RuntimeException {

    public InvalidAccountNumberFormatException(String message){
        super(message);
    }

    public InvalidAccountNumberFormatException(Exception e){
        super(e);
    }

    public InvalidAccountNumberFormatException(String message, Exception e){
        super(message,e);
    }

}
