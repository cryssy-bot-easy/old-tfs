package com.ucpb.tfs.domain.settlementaccount;

import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;
import com.ucpb.tfs.domain.settlementaccount.activity.Activity;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountStatus;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.SettlementAccountType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Val
 */
@DomainAggregateRoot
public abstract class SettlementAccount implements Serializable {
    
    private String id;
    
    private SettlementAccountNumber settlementAccountNumber;

    private SettlementAccountType settlementAccountType;

    private SettlementAccountStatus status;

    private Date modifiedDate;
    
    private String cifNumber;

    // A SettlementAccount has a list of activities
    protected Set<Activity> activities;

    protected SettlementAccount() {
        this.status = SettlementAccountStatus.MARV;
        this.activities = new HashSet<Activity>();
        this.modifiedDate = new Date();
    }

    protected SettlementAccount(SettlementAccountNumber settlementAccountNumber, SettlementAccountType settlementAccountType) {
        this();
        this.settlementAccountNumber = settlementAccountNumber;
        this.settlementAccountType = settlementAccountType;
    }

    protected void addOrUpdateActivity(Activity activity) {

        Iterator it = this.activities.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            Activity persistedActivity = (Activity)it.next();
            if (persistedActivity.matches(activity.getActivityType(), activity.getReferenceType(), activity.getReferenceNumber(), activity.getCurrency())) {
                persistedActivity.update(activity.getActivityType(), activity.getAmount(), activity.getCurrency());
                exists = true;
                break;
            }
        }

        if (!exists) {
            this.activities.add(activity);
        }
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

    public Set<Activity> getActivities() {
        return activities;
    }
}
