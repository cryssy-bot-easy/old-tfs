package com.ucpb.tfs.domain.accounting;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: giancarlo
 * Date: 9/30/12
 * Time: 10:04 PM
 */
public class AccountingEventTransactionId implements Serializable {

    private String accountingEventTransactionId;

    private String description;

    public AccountingEventTransactionId() {
    }

    public AccountingEventTransactionId(final String accountingEventTransactionId) {
        Validate.notNull(accountingEventTransactionId);
        this.accountingEventTransactionId = accountingEventTransactionId;
    }

    @Override
    public String toString() {
        return this.accountingEventTransactionId;
    }

    public String getAccountingEventTransactionId() {
        return accountingEventTransactionId;
    }

    public String getDescription() {
        return description;
    }


}
