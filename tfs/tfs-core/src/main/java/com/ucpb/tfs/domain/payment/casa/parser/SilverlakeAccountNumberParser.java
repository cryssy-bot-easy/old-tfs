package com.ucpb.tfs.domain.payment.casa.parser;

import com.ucpb.tfs.domain.payment.casa.AccountType;
import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import com.ucpb.tfs.domain.payment.casa.Currency;
import com.ucpb.tfs.domain.payment.casa.parser.exception.InvalidAccountNumberFormatException;
import org.apache.commons.lang.StringUtils;

/**
 */
public class SilverlakeAccountNumberParser extends AccountNumberParser {

    @Override
    protected CasaAccount doParse(String accountNumber) {
        System.out.println("in silverlake " + accountNumber);
        AccountType accountType = AccountType.getAccountTypeByCode(StringUtils.left(accountNumber, 1));
        Currency currency = Currency.getCurrencyByCode(StringUtils.substring(accountNumber,1,2));

        if(currency == null){
            throw new InvalidAccountNumberFormatException("Currency code: " + StringUtils.substring(accountNumber,1,2) + " is invalid!");
        }

        return new CasaAccount(accountNumber,accountType,currency);
    }

    @Override
    public String getAccountNumberFormat() {
        return "[1-5][\\d]{11}";
    }
}
