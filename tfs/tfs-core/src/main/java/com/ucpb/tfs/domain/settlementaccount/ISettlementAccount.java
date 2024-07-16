package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * User: Val
 * Date: 7/19/12
 */
public interface ISettlementAccount {

    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception;

    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception;
}
