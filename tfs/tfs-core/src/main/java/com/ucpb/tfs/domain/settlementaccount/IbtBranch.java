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
public class IbtBranch extends SettlementAccount implements ISettlementAccount, Serializable {

    public IbtBranch(SettlementAccountNumber settlementAccountNumber) {
        // settlementAccountNumber = branch unit code
        super(settlementAccountNumber, SettlementAccountType.IBT_BRANCH);
    }

    @Override
    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, String refNumber, String... otherDetails) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, String refNumber, String... otherDetails) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
