package com.ucpb.tfs.domain.settlementaccount.activity;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.ActivityStatus;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ActivityType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * @author Val
 */
public class Activity {

//    private Long id;
    private String id;

    private SettlementAccountType settlementAccountType;

    private ActivityType activityType;

    private ReferenceType referenceType;

    private String referenceNumber;

    private BigDecimal amount;

    private Currency currency;

    private ActivityStatus status;

    private Date modifiedDate;

    protected Activity() {}

    protected Activity(BigDecimal amount, Currency currency, ActivityType activityType, ReferenceType referenceType, String referenceNumber) {
        this.activityType = activityType;
        this.referenceType = referenceType;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;
        this.status = ActivityStatus.MARV;
        this.modifiedDate = new Date();
    }

    public Boolean matches(ActivityType activityType, ReferenceType referenceType, String referenceNumber, Currency currency) {
        Boolean retBool = false;
        if (this.activityType.equals(activityType) &&
            this.referenceType.equals(referenceType) &&
            this.referenceNumber.equals(referenceNumber) &&
            this.currency.equals(currency)) {
            retBool = true;
        }
        return retBool;
    }

    public void update(ActivityType activityType, BigDecimal amount, Currency currency, String... otherDetails) {
        if (this.activityType.equals(activityType) && this.currency.equals(currency)) {
            this.amount = this.amount.add(amount);
            this.modifiedDate = new Date();
        }
    }

    protected void setSettlementAccountType(SettlementAccountType settlementAccountType) {
        this.settlementAccountType = settlementAccountType;
        this.modifiedDate = new Date();
    }

    public SettlementAccountType getSettlementAccountType() {
        return settlementAccountType;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
