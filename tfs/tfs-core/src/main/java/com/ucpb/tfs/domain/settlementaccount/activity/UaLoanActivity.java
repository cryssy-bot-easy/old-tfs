package com.ucpb.tfs.domain.settlementaccount.activity;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ActivityType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import com.ucpb.tfs.domain.settlementaccount.utils.SettlementAccountUtils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * User: Val
 * Date: 7/25/12
 */
public class UaLoanActivity extends Activity {

    private Date loanMaturityDate;

    public UaLoanActivity() {
        super();
        super.setSettlementAccountType(SettlementAccountType.UA_LOAN);
    }

    /**
     * @param amount
     * @param currency
     * @param activityType
     * @param referenceType
     * @param referenceNumber
     * @param otherDetails - [0]=loanMaturityDate
     * @throws Exception
     */
    public UaLoanActivity(BigDecimal amount, Currency currency, ActivityType activityType, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        super(amount, currency, activityType, referenceType, referenceNumber);

        // Use otherDetails for parameters specific to UaLoanActivity only.
        this.loanMaturityDate = SettlementAccountUtils.checkNullOrBlankDate(otherDetails[0]);
    }
}
