/**
 * 
 */
package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * @author Val
 *
 */
public class Casa extends SettlementAccount implements ISettlementAccount, Serializable {

    // private CasaServiceImpl casaService;

    public Casa(SettlementAccountNumber settlementAccountNumber) {
        // SettlementAccount number = CASA Account Number
        super(settlementAccountNumber, SettlementAccountType.CASA);
    }

    @Override
    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        System.out.println("\nDEBIT from CASA! referenceNumber = " + referenceNumber + "\n");
        // CasaResponse response = casaService.sendCasaRequest(new CasaRequest());
    }

    @Override
    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        System.out.println("\nCREDIT to CASA! referenceNumber = " + referenceNumber + "\n");
        // CasaResponse response = casaService.sendCasaRequest(new CasaRequest());
    }
}

