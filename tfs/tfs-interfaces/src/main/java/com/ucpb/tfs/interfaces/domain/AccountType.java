package com.ucpb.tfs.interfaces.domain;

/**
 */
public enum AccountType {

    CURRENT("C"), SAVINGS("D");

    private  String accountCode;

    private AccountType(String accountCode){
        this.accountCode = accountCode;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public static AccountType getAccountTypeByCode(String accountCode){
        for(AccountType accountType : AccountType.values()){
            if(accountType.getAccountCode().equals(accountCode)){
                return accountType;
            }
        }
        return null;
    }
}
