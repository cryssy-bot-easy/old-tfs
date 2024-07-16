package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.utils.UtilSetFields;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/5/13
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoanDetails {

    private String facilityType;
    private String faciltiyId;

    private Currency bookingCurrency;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private String interestTermCode;
    private Long interestTerm;

//    private String repricingTermCode;
//    private Long repricingTerm;

    private String loanTermCode;
    private Long loanTerm;

    private Date loanMaturityDate;

    private Long numberOfFreeFloatDays;

    private String agriAgraTagging;

    private String paymentCode;

    private String pnNumber;

    private String transactionPostingStatus;

    public LoanDetails() {}

    public LoanDetails(Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);
    }

}
