/**
 * 
 */
package com.ucpb.tfs.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.activity.MdActivity;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.*;
import org.joda.money.Money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Val
 *
 */
//public class MarginalDeposit extends SettlementAccount implements ISettlementAccount, Serializable {
public class MarginalDeposit implements ISettlementAccount, Serializable {

    private String id;

    private SettlementAccountNumber settlementAccountNumber;

    private SettlementAccountType settlementAccountType;

    private SettlementAccountStatus status;

    private Date modifiedDate;

    private String cifNumber;

    private String cifName;

    private String accountOfficer;

    private String ccbdBranchUnitCode;

    private MdPnSupport pnSupport;

    private BigDecimal outstandingBalance;

    private String longName;

    private String address1;

    private String address2;

    // A SettlementAccount has a list of activities
//    private Set<Activity> activities;
    private Set<MdActivity> activities;

    public MarginalDeposit() {
//        super();
        this.pnSupport = MdPnSupport.NO;
    }

    public MarginalDeposit(SettlementAccountNumber settlementAccountNumber) {
//        super(settlementAccountNumber, SettlementAccountType.MD);
        this.settlementAccountNumber = settlementAccountNumber;
        this.settlementAccountType = SettlementAccountType.MD;

//        this.activities = new HashSet<Activity>();
        this.activities = new HashSet<MdActivity>();
        this.pnSupport = MdPnSupport.NO;

        this.status = SettlementAccountStatus.MARV;

        this.modifiedDate = new Date();
    }

    @Override
    public void debit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception  {
        MdActivity debitTarget = new MdActivity(amount, currency, ActivityType.DEBIT, referenceType, referenceNumber, otherDetails);
        this.activities.add(debitTarget);
        // super.addOrUpdateActivity(debitTarget);

        this.modifiedDate = new Date();
    }

    @Override
    public void credit(BigDecimal amount, Currency currency, ReferenceType referenceType, String referenceNumber, String... otherDetails) throws Exception  {
        MdActivity creditSource = new MdActivity(amount, currency, ActivityType.CREDIT, referenceType, referenceNumber, otherDetails);
        this.activities.add(creditSource);
        // super.addOrUpdateActivity(creditSource);
        
        this.modifiedDate = new Date();
    }

    public Money getOutstandingBalance(Currency currency) {
		return null;
	}

	public List<Currency> getCurrencies() {
		return null;
	}

    public void upDatePnSupport(MdPnSupport mdPnSupport) {
        this.pnSupport = pnSupport;
    }

    public MdPnSupport getPnSupport() {
        return pnSupport;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber;
    }
    
    public void setCifNumber(String cifNumber, String cifName, String accountOfficer, String ccbdBranchUnitCode) {
        this.cifNumber = cifNumber;
        this.cifName = cifName;
        this.accountOfficer = accountOfficer;
        this.ccbdBranchUnitCode = ccbdBranchUnitCode;
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

//    public Set<Activity> getActivities() {
    public Set<MdActivity> getActivities() {
        return activities;
    }

    public BigDecimal getMdOutstandingBalance() {
        BigDecimal outstandingBalance = BigDecimal.ZERO;

        Set<MdActivity> acts = getActivities();

        for(MdActivity mda : acts) {

            switch(mda.getActivityType()) {
                case CREDIT:
                    outstandingBalance = outstandingBalance.add(mda.getAmount());
                    break;

                case DEBIT:
                    outstandingBalance = outstandingBalance.subtract(mda.getAmount());
                    break;
            }
        }

        return outstandingBalance;
    }

    public void setMdOutstandingBalance(BigDecimal mdOutstandingBalance) {
        if (mdOutstandingBalance == null) {
            this.outstandingBalance = BigDecimal.ZERO;
        } else {
            this.outstandingBalance = mdOutstandingBalance;
        }
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }
}
