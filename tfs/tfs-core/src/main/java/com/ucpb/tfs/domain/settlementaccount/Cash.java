/**
 * 
 */
package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * @author Val
 *
 */
public class Cash extends SettlementAccount implements ISettlementAccount, Serializable {

    public Cash(SettlementAccountNumber settlementAccountNumber) {
        // settlementaccount number = GL Trade Suspense SettlementAccount number
        super(settlementAccountNumber, SettlementAccountType.CASH);
    }

    @Override
    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        System.out.println("\nDEBIT from CASH! referenceNumber = " + referenceNumber + "\n");
    }

    @Override
    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        System.out.println("\nCREDIT to CHECK! referenceNumber = " + referenceNumber + "\n");
    }
}
