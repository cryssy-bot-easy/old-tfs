package com.ucpb.tfs.domain.accounting;

import org.apache.commons.lang.Validate;
import org.hibernate.type.StringType;

import java.io.Serializable;

/**
 * User: giancarlo
 * Date: 9/29/12
 * Time: 4:28 PM
 */
public class AccountingEntryId extends StringType implements Serializable {

    private String accountingEntryId;

    public AccountingEntryId() {
    }

    public AccountingEntryId(final String accountingEntryId) {
        Validate.notNull(accountingEntryId);
        this.accountingEntryId = accountingEntryId;
    }

    @Override
    public String toString() {
        return this.accountingEntryId;
    }

}
