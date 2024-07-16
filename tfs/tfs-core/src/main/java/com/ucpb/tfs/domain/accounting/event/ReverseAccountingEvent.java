package com.ucpb.tfs.domain.accounting.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.accounting.AccountingEventId;
import com.ucpb.tfs.domain.accounting.AccountingEventTransactionId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;

/**
 * Created with IntelliJ IDEA.
 * User: giancarlo
 * Date: 10/5/12
 * Time: 6:54 PM
 */
public class ReverseAccountingEvent  implements DomainEvent {

    AccountingEventId accountingEventId;
    AccountingEventTransactionId accountingEventTransactionId;
    UserActiveDirectoryId userActiveDirectoryId;
    TradeServiceId tradeServiceId;

    public ReverseAccountingEvent() {}

    public ReverseAccountingEvent(TradeServiceId tradeServiceId, AccountingEventId accountingEventId, AccountingEventTransactionId accountingEventTransactionId, UserActiveDirectoryId userActiveDirectoryId) {
        this.tradeServiceId = tradeServiceId;
        this.accountingEventId = accountingEventId;
        this.accountingEventTransactionId = accountingEventTransactionId;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

    public AccountingEventId getAccountingEventId() {
        return accountingEventId;
    }

    public AccountingEventTransactionId getAccountingEventTransactionId() {
        return accountingEventTransactionId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

}
