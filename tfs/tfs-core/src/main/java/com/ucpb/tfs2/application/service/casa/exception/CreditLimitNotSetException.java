package com.ucpb.tfs2.application.service.casa.exception;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 12/5/13
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreditLimitNotSetException extends Exception {

    public CreditLimitNotSetException(String message){
        super(message);
    }

    public CreditLimitNotSetException(String message,Throwable e){
        super(message,e);
    }

}
