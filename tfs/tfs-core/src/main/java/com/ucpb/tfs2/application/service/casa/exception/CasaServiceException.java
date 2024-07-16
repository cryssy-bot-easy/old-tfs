package com.ucpb.tfs2.application.service.casa.exception;

/**
 */
public class CasaServiceException extends Exception {

    private String casaErrorMessage;

    private String errorCode;

    public CasaServiceException(Throwable e){
        super(e);
    }

    public CasaServiceException(String message,Throwable e){
        super(message,e);
    }

    public CasaServiceException(String message,String casaErrorMessage){
        super(message);
        this.casaErrorMessage = casaErrorMessage;
    }

    public CasaServiceException(String message,String casaErrorMessage,String errorCode){
        this(message,casaErrorMessage);
        this.errorCode = errorCode;
    }

    public String getCasaErrorMessage() {
        return casaErrorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
