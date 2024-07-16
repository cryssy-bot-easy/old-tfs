package com.ucpb.tfs.domain.payment.casa;

import com.ucpb.tfs.domain.payment.casa.parser.AccountNumberParser;
import com.ucpb.tfs.domain.payment.casa.parser.SilverlakeAccountNumberParser;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class SilverlakeAccountNumberParserTest {

    private SilverlakeAccountNumberParser parser = new SilverlakeAccountNumberParser();


    @Test
    public void passValidAccountNumber(){
        assertTrue(parser.canParse("101111111111"));
    }

    @Test
    public void passValidAccountNumber2(){
        assertTrue(parser.canParse("112400000076"));
        CasaAccount account = parser.parse("112400000076");
    }

    @Test
    public void retrieveJpyCurrencyCode(){
        CasaAccount account = parser.parse("122400000020");
        assertEquals(Currency.JPY,account.getCurrency());
    }

    @Test
    public void failInvalidAccountNumberPassword(){
        assertFalse(parser.canParse("KDAJKd2415"));
    }

    @Test
    public void successfullyRetrieveAccountNumberDetails(){
        CasaAccount casaAccount = parser.parse("101111111111");
        assertEquals(AccountType.SAVINGS,casaAccount.getAccountType());
        assertEquals(Currency.PHP,casaAccount.getCurrency());
    }

}
