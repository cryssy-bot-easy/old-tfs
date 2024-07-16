package com.ucpb.tfs.domain.accounting;

/**
 * User: giancarlo
 * Date: 9/29/12
 * Time: 3:27 PM
 */
public class AccountingEventTransaction {

    private String description;
    private AccountingEventTransactionId accountingEventTransactionId;

    public AccountingEventTransaction() {
    }

    public AccountingEventTransaction(String description, AccountingEventTransactionId accountingEventTransactionId) {
        this.description = description;
        this.accountingEventTransactionId = accountingEventTransactionId;
    }

    // todo: remove this after testing
    @Override
    public String toString() {
        return this.accountingEventTransactionId.toString();
    }

    public AccountingEventTransactionId getAccountingEventTransactionId() {
        return this.accountingEventTransactionId;
    }

}
