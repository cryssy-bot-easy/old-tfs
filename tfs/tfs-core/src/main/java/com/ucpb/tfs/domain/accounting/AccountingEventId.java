package com.ucpb.tfs.domain.accounting;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: giancarlo
 * Date: 9/30/12
 * Time: 10:00 PM
 */
public class AccountingEventId implements Serializable {

    private String accountingEventId;

    public AccountingEventId() {
    }

    public AccountingEventId(final String accountingEventId) {
        Validate.notNull(accountingEventId);
        this.accountingEventId = accountingEventId;
    }

    @Override
    public String toString() {
        return this.accountingEventId;
    }

}
