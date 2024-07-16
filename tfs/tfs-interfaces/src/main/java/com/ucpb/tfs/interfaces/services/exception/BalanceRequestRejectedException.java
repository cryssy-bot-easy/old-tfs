package com.ucpb.tfs.interfaces.services.exception;

/**
 */
public class BalanceRequestRejectedException extends Exception {

   public BalanceRequestRejectedException(String message){
       super(message);
   }

   public BalanceRequestRejectedException(String message,Throwable e){
       super(message,e);
   }
}
