package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: Val
 * Date: 7/25/12
 */
public class UaLoan extends SettlementAccount implements ISettlementAccount, Serializable {

    public UaLoan(SettlementAccountNumber settlementAccountNumber) {
        super(settlementAccountNumber, SettlementAccountType.UA_LOAN);
    }

    @Override
    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {
        // TODO: UaLoan.debit()
    }

    @Override
    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {
        // TODO: UaLoan.credit()
    }
}
