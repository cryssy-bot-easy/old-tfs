/**
 * 
 */
package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.settlementaccount.activity.ApActivity;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ActivityType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountStatus;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Val
 *
 */

public class AccountsPayable implements Serializable {

    private String id;

    private SettlementAccountNumber settlementAccountNumber; // document number or settlement account number

    private SettlementAccountType settlementAccountType; // AP

    private SettlementAccountStatus status; // marv or approved

    private Date modifiedDate;

    private String cifNumber;

    private String cifName;

    private Currency currency;
    private BigDecimal outstandingBalance;

    // A SettlementAccount has a list of activities
    private Set<ApActivity> activities;

    private String natureOfTransaction;

    private Date bookingDate; // booked date

    private String accountOfficer;

    private String ccbdBranchUnitCode;

    private BigDecimal originalAmount;

    private TradeServiceId tradeServiceId;

    public AccountsPayable() {
    }

    public AccountsPayable(SettlementAccountNumber settlementAccountNumber,
                           Currency currency,
                           String cifNumber,
                           String cifName,
                           String accountOfficer,
                           String ccbdBranchUnitCode,
                           Date bookingDate,
                           String natureOfTransaction,
                           BigDecimal amount,
                           TradeServiceId tradeServiceId) {
        this.settlementAccountNumber = settlementAccountNumber;
        this.settlementAccountType = SettlementAccountType.AP;

        this.activities = new HashSet<ApActivity>();

        this.currency = currency;

        this.status = SettlementAccountStatus.OUTSTANDING;
        this.modifiedDate = new Date();

        this.cifNumber = cifNumber;
        this.cifName = cifName;
        this.accountOfficer = accountOfficer;
        this.ccbdBranchUnitCode = ccbdBranchUnitCode;

        this.bookingDate = bookingDate;

        if (natureOfTransaction != null) {
            this.natureOfTransaction = natureOfTransaction;
        }


        this.originalAmount = amount;

        this.tradeServiceId = tradeServiceId;

    }

    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, Date bookingDate, String referenceNumber, String applicationReferenceNumber) throws Exception  {
        ApActivity debitTarget = new ApActivity(amount, currency, bookingDate, referenceType, ActivityType.DEBIT, referenceNumber, applicationReferenceNumber);
        this.activities.add(debitTarget);

        this.modifiedDate = new Date();
    }

    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, Date bookingDate, String referenceNumber) throws Exception  {
        ApActivity creditSource = new ApActivity(amount, currency, bookingDate, referenceType, ActivityType.CREDIT, referenceNumber, null);

        this.activities.add(creditSource);

        this.modifiedDate = new Date();
    }

    public void credit(BigDecimal amount,
                       Currency currency,
                       ReferenceType referenceType,
                       Date bookingDate,
                       String referenceNumber,
                       String natureOfTransaction) throws Exception  {
        ApActivity creditSource = new ApActivity(amount,
                currency,
                bookingDate,
                referenceType,
                ActivityType.CREDIT,
                referenceNumber,
                null,
                natureOfTransaction);

        this.activities.add(creditSource);

        this.modifiedDate = new Date();
    }

    protected void addOrUpdateActivity(ApActivity activity) {

        Iterator it = this.activities.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            ApActivity persistedActivity = (ApActivity)it.next();
            if (persistedActivity.matches(activity.getActivityType(), activity.getReferenceType(), activity.getCurrency(), activity.getReferenceNumber())) {
                persistedActivity.update(activity.getAmount(), activity.getCurrency());
                exists = true;
                break;
            }
        }

        if (!exists) {
            this.activities.add(activity);
        }
    }

    public void setCif(String cifNumber, String cifName) {
        this.cifNumber = cifNumber;
        this.cifName = cifName;
    }

    public void updateStatus(SettlementAccountStatus status) {
        this.status = status;
        this.modifiedDate = new Date();
    }

    public SettlementAccountStatus getStatus() {
        return this.status;
    }

    public SettlementAccountNumber getSettlementAccountNumber() {
        return settlementAccountNumber;
    }

    public SettlementAccountType getSettlementAccountType() {
        return settlementAccountType;
    }

    public Set<ApActivity> getActivities() {
        return activities;
    }
    
    public BigDecimal getApOutstandingBalance() {
        BigDecimal outstandingBalance = BigDecimal.ZERO;

        Set<ApActivity> acts = getActivities();

        for(ApActivity apa : acts) {

            switch(apa.getActivityType()) {
                case CREDIT:
                    outstandingBalance = outstandingBalance.add(apa.getAmount());
                    break;

                case DEBIT:
                    outstandingBalance = outstandingBalance.subtract(apa.getAmount());
                    break;
            }
        }

        return outstandingBalance;
    }


    public void setApOutstandingBalance(BigDecimal apOutstandingBalance) {
        if (apOutstandingBalance == null) {
            this.outstandingBalance = BigDecimal.ZERO;
        } else {
            this.outstandingBalance = apOutstandingBalance;
        }
    }

    public void refundAccountsPayable() {
        this.status = SettlementAccountStatus.REFUNDED;
        this.modifiedDate = new Date();
    }

    public void openAccountsPayable() {
        this.status = SettlementAccountStatus.OUTSTANDING;
        this.modifiedDate = new Date();
    }

    public String getId() {
        return id;
    }
    
    public Currency getCurrency() {
    	return currency;
    }
}
