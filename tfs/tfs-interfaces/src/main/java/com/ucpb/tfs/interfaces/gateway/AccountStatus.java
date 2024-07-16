package com.ucpb.tfs.interfaces.gateway;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 */
public enum AccountStatus {

    ACTIVE("0"), NEW("1"), CLOSED("2"),
    FOR_TRAINING("3"), FROZEN("4"),FOR_DECEASED_CUSTOMER("5"), CALL_SUPERVISOR("6"), DORMANT("7") ;


    private String code;

    private AccountStatus(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AccountStatus getAccountStatusByCode(String code){
        for(AccountStatus status : AccountStatus.values()){
            if(status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
