package com.ucpb.tfs.domain.settlementaccount.activity;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.ActivityStatus;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ActivityType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;
import com.ucpb.tfs.domain.settlementaccount.utils.SettlementAccountUtils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * User: Val
 * Date: 7/22/12
 */

public class ArActivity {

    private String applicationReferenceNumber; // not used

    private String natureOfTransaction; // not used

    private String id; // unique identifier

    private Date bookingDate; // not used

    private SettlementAccountType settlementAccountType;

    private ActivityType activityType; // debit or credit

    private ReferenceType referenceType; // from excess or manual

    private String referenceNumber; // used to reference an activity

    private BigDecimal amount;

    private Currency currency;

    private ActivityStatus status; // marv, credited or debited (not used)

    private Date modifiedDate;

    public ArActivity() {
    }

    public ArActivity(BigDecimal amount,
                      Currency currency,
                      Date bookingDate,
                      ReferenceType referenceType,
                      ActivityType activityType,
                      String referenceNumber,
                      String applicationReferenceNumber) {
        this.amount = amount;
        this.currency = currency;
        this.bookingDate = bookingDate;
        this.referenceType = referenceType;

        this.activityType = activityType;

        this.referenceNumber = referenceNumber;

        if (applicationReferenceNumber != null) {
            this.applicationReferenceNumber = applicationReferenceNumber;
        }

        this.settlementAccountType = SettlementAccountType.AR;

    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public Boolean matches(ActivityType activityType, ReferenceType referenceType, Currency currency, String referenceNumber) {
        Boolean result = false;

        if (this.activityType.equals(activityType) &&
                this.referenceType.equals(referenceType) &&
                this.referenceNumber.equals(referenceNumber) &&
                this.currency.equals(currency)) {
            result = true;
        }

        return result;
    }

    public void update(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;

        this.modifiedDate = new Date();
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

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
