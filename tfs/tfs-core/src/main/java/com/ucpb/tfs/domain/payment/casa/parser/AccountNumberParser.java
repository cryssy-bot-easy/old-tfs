package com.ucpb.tfs.domain.payment.casa.parser;

import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import org.springframework.util.Assert;

/**
 */
public abstract class AccountNumberParser {

    public final boolean canParse(String accountNumber){
        Assert.notNull(accountNumber);
        return accountNumber.matches(getAccountNumberFormat());
    }


    public final CasaAccount parse(String accountNumber){
        Assert.isTrue(canParse(accountNumber),"Account number format is invalid!");
        return doParse(accountNumber);
    }

    protected abstract CasaAccount doParse(String accountNumber);

    public abstract String getAccountNumberFormat();



}
