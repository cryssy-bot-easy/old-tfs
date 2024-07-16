package com.ucpb.tfs.domain.payment.casa;

import com.ucpb.tfs.domain.payment.casa.parser.AccountNumberParser;
import com.ucpb.tfs.domain.payment.casa.parser.MainFrameAccountNumberParser;
import com.ucpb.tfs.domain.payment.casa.parser.SilverlakeAccountNumberParser;
import com.ucpb.tfs.domain.payment.casa.parser.exception.InvalidAccountNumberFormatException;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class CasaAccountFactory {

    private static List<AccountNumberParser> accountNumberParserList = new ArrayList<AccountNumberParser>();

    static {
        accountNumberParserList.add(new SilverlakeAccountNumberParser());
        accountNumberParserList.add(new MainFrameAccountNumberParser());
    }


    public static CasaAccount getInstance(String accountNumber) throws InvalidAccountNumberFormatException{
        for(AccountNumberParser accountNumberParser : accountNumberParserList){
            if(accountNumberParser.canParse(accountNumber)){
                return accountNumberParser.parse(accountNumber);
            }
        }
        throw new InvalidAccountNumberFormatException("Account number '"+accountNumber +"' format is invalid");
    }

}
