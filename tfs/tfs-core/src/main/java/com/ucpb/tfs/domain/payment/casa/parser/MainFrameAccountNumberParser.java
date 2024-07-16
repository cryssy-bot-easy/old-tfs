package com.ucpb.tfs.domain.payment.casa.parser;

import com.ucpb.tfs.domain.payment.casa.AccountType;
import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import com.ucpb.tfs.domain.payment.casa.Currency;
import org.codehaus.plexus.util.StringUtils;

/**
 */
public class MainFrameAccountNumberParser extends AccountNumberParser {



//    @Override
//    protected CasaAccount doParse(String accountNumber) {
//        Currency currency = Currency.getCurrencyByCode(StringUtils.substring(accountNumber,1,2));
//        AccountType accountType = AccountType.getAccountTypeByCode(StringUtils.substring(accountNumber,5,6));
//        return new CasaAccount(accountNumber,accountType,currency);
//    }

    @Override
    protected CasaAccount doParse(String accountNumber) {
        System.out.println("in mainframe " + accountNumber);
        Currency currency = Currency.getCurrencyByCode(StringUtils.substring(accountNumber,1,2));
        System.out.println("StringUtils.substring(accountNumber,1,2):"+StringUtils.substring(accountNumber,1,2));
        System.out.println("CURRENCY CODE:"+StringUtils.substring(accountNumber,1,2));

//        AccountType accountType = AccountType.getAccountTypeByCode(StringUtils.substring(accountNumber,5,6));
        System.out.println("StringUtils.substring(accountNumber,5,6)::::"+StringUtils.substring(accountNumber,5,6));
        AccountType accountType = AccountType.getAccountTypeByAccountNumber(StringUtils.substring(accountNumber,5,6));

        System.out.println("accountType: " + accountType);
        if (!Currency.PHP.equals(currency)) {
            accountType = AccountType.CURRENT;
        }

        return new CasaAccount(accountNumber,accountType,currency);
    }

    @Override
    public String getAccountNumberFormat() {
        return "0[\\d]{11}";
    }
}
