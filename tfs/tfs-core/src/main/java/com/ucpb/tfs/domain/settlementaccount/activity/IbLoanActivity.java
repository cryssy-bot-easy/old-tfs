package com.ucpb.tfs.domain.settlementaccount.activity;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ActivityType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.TermCode;
import com.ucpb.tfs.domain.settlementaccount.utils.SettlementAccountUtils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * User: Val
 * Date: 7/25/12
 */
public class IbLoanActivity extends Activity {

    private BigDecimal interestRate;

    private int interestTerm;
    private TermCode interestTermCode;

    private int loanTerm;
    private TermCode loanTermCode;

    private Date loanMaturityDate;

    private Boolean withCramApproval;

    public IbLoanActivity() {
        super();
        super.setSettlementAccountType(SettlementAccountType.IB_LOAN);
    }

    /**
     * @param amount
     * @param currency
     * @param activityType
     * @param referenceType
     * @param referenceNumber
     * @param otherDetails - [0]=interestRate, [1]=interestTerm, [2]=interestTermCode, [3]=loanTerm, [4]=loanTermCode, [5]=loanMaturityDate, [6]=withCramApproval
     * @throws Exception
     */
    public IbLoanActivity(BigDecimal amount, Currency currency, ActivityType activityType, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception {

        super(amount, currency, activityType, referenceType, referenceNumber);

        // Use otherDetails for parameters specific to IbLoanActivity only.
        this.interestRate     = SettlementAccountUtils.checkNullOrBlankDecimal(otherDetails[0]);
        this.interestTerm     = SettlementAccountUtils.checkNullOrBlankNumber(otherDetails[1]);
        this.interestTermCode = SettlementAccountUtils.checkNullOrBlankTermCode(otherDetails[2]);
        this.loanTerm         = SettlementAccountUtils.checkNullOrBlankNumber(otherDetails[3]);
        this.loanTermCode     = SettlementAccountUtils.checkNullOrBlankTermCode(otherDetails[4]);
        this.loanMaturityDate = SettlementAccountUtils.checkNullOrBlankDate(otherDetails[5]);

        String cram = SettlementAccountUtils.checkNullOrBlankString(otherDetails[6]);
        if (cram.toUpperCase().equals("Y") || cram.toUpperCase().equals("YES")) {
            this.withCramApproval = Boolean.TRUE;
        } else {
            this.withCramApproval = Boolean.FALSE;
        }
    }
}
