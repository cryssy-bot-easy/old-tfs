package com.ucpb.tfs.domain.payment.casa;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.ucpb.tfs.interfaces.gateway.AccountStatus;

/**
 */
public class CasaAccount {

    private static final Currency PHP = Currency.PHP;

    private String accountNumber;

    private AccountType accountType;

    private Currency currency;

    private String accountName;

    private AccountStatus accountStatus;

    public CasaAccount(String accountNumber,AccountType accountType, Currency currency){
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
    }

    public CasaAccount(String accountNumber,AccountType accountType, Currency currency,String accountName){
        this(accountNumber,accountType,currency);
        this.accountName = accountName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isForeign(){
        return !PHP.equals(currency);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public boolean isCurrent(){
//        return AccountType.CURRENT.equals(accountType);
        System.out.println("checking if current");
        System.out.println("accountType " + accountType);
        System.out.println((AccountType.CURRENT.equals(accountType) || AccountType.CURRENT_OLD.equals(accountType)));
        return (AccountType.CURRENT.equals(accountType) || AccountType.CURRENT_OLD.equals(accountType));
    }

    public boolean isSavings(){
//        return AccountType.SAVINGS.equals(accountType);
        return (AccountType.SAVINGS.equals(accountType) || AccountType.SAVINGS_OLD.equals(accountType));
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CasaAccount that = (CasaAccount) o;

        if (accountNumber != null ? !accountNumber.equals(that.accountNumber) : that.accountNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return accountNumber != null ? accountNumber.hashCode() : 0;
    }
}
