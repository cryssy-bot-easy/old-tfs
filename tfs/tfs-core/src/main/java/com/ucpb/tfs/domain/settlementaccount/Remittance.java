package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: Val
 * Date: 7/22/12
 */
public class Remittance extends SettlementAccount implements ISettlementAccount, Serializable {

    public Remittance(SettlementAccountNumber settlementAccountNumber) {
        super(settlementAccountNumber, SettlementAccountType.REMITTANCE);
    }

    @Override
    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        System.out.println("\nDEBIT from REMITTANCE! referenceNumber = " + referenceNumber + "\n");
    }

    @Override
    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        System.out.println("\nCREDIT to REMITTANCE! referenceNumber = " + referenceNumber + "\n");
    }
}
