package com.ucpb.tfs.domain.payment.casa;

/**
 */
public enum AccountType {

    CURRENT("D","0"), SAVINGS("S","1"), CURRENT_OLD("DO", "2"), SAVINGS_OLD("SO", "1");

    private  String accountCode;

    private String accountNumber;

    private AccountType(String accountCode, String accountNumber){
        this.accountCode = accountCode;
        this.accountNumber = accountNumber;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public static AccountType getAccountTypeByCode(String accountCode){
        System.out.println("accountCode " + accountCode);
        for(AccountType accountType : AccountType.values()){
            System.out.println("accountTypeCode " + accountType.getAccountCode());

//            if(accountType.getAccountCode().equals(accountCode)){
            if(accountType.getAccountNumber().equals(accountCode)){
                System.out.println("EQUALS YEHEY ANGOL");
                return accountType;
            }
        }
        return null;
    }

    public static AccountType getAccountTypeByAccountNumber(String accountNumber){
        System.out.println("accountNumber: " + accountNumber);
        for(AccountType accountType : AccountType.values()){
            System.out.println("accountTypeNumber " + accountType.getAccountNumber());
            if(accountType.getAccountNumber().equals(accountNumber)){
                return accountType;
            }
        }
        return null;
    }

}
