package com.ucpb.tfs.domain.casa;

import com.ucpb.tfs.domain.casa.enums.CasaAccountType;

import java.io.Serializable;
import java.util.Currency;

/**
 * Created by Marv on 1/27/14.
 */
public class RefCasaAccount implements Serializable {

    private String id;

    private String cifNumber;
    private Currency currency;
    private String accountNumber;
    private String accountName;
    private CasaAccountType accountType;

    public RefCasaAccount() {}

    public RefCasaAccount(String cifNumber, String currency, String accountNumber, String accountName, String accountType) {
        this.cifNumber = cifNumber;
        this.currency = Currency.getInstance(currency);
        this.accountNumber = accountNumber;
        this.accountName = accountName;

        this.accountType = CasaAccountType.valueOf(accountType);
    }

    public String getAccountName() {
        return accountName;
    }

    public CasaAccountType getAccountType() {
        return accountType;
    }

}
