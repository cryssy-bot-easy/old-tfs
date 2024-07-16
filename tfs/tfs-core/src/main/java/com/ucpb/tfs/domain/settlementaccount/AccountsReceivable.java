/**
 *
 */
package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.settlementaccount.activity.ArActivity;
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

public class AccountsReceivable implements Serializable {

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
    private Set<ArActivity> activities;

    private String natureOfTransaction;

    private Date bookingDate; // booked date

    private String accountOfficer;

    private String ccbdBranchUnitCode;

    private BigDecimal originalAmount;

    private TradeServiceId tradeServiceId;

    public AccountsReceivable() {
    }

    public AccountsReceivable(SettlementAccountNumber settlementAccountNumber){
        this.settlementAccountNumber = settlementAccountNumber;
        this.settlementAccountType = SettlementAccountType.AR;

        this.activities = new HashSet<ArActivity>();

        this.currency = currency;

        this.status = SettlementAccountStatus.OUTSTANDING;
        this.modifiedDate = new Date();

    }
    public AccountsReceivable(SettlementAccountNumber settlementAccountNumber,
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
        this.settlementAccountType = SettlementAccountType.AR;

        this.activities = new HashSet<ArActivity>();

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
        ArActivity debitTarget = new ArActivity(amount, currency, bookingDate, referenceType, ActivityType.DEBIT, referenceNumber, applicationReferenceNumber);
        this.activities.add(debitTarget);

        this.modifiedDate = new Date();
    }

    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, Date bookingDate, String referenceNumber) throws Exception  {
        ArActivity creditSource = new ArActivity(amount, currency, bookingDate, referenceType, ActivityType.CREDIT, referenceNumber, null);

        this.activities.add(creditSource);

        this.modifiedDate = new Date();
    }

    protected void addOrUpdateActivity(ArActivity activity) {

        Iterator it = this.activities.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            ArActivity persistedActivity = (ArActivity)it.next();
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

    public Set<ArActivity> getActivities() {
        return activities;
    }

    public BigDecimal getArOutstandingBalance() {
        BigDecimal outstandingBalance = BigDecimal.ZERO;

        Set<ArActivity> acts = getActivities();

        for(ArActivity apa : acts) {

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


    public void setArOutstandingBalance(BigDecimal arOutstandingBalance) {
        if (arOutstandingBalance == null) {
            this.outstandingBalance = BigDecimal.ZERO;
        } else {
            this.outstandingBalance = arOutstandingBalance;
        }
    }

    public void closeAccountsReceivable() {
        this.status = SettlementAccountStatus.CLOSED;
        this.modifiedDate = new Date();
    }

    public void openAccountsReceivable() {
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
