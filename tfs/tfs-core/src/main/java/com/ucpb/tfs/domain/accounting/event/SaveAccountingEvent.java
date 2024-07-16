package com.ucpb.tfs.domain.accounting.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.accounting.AccountingEventId;
import com.ucpb.tfs.domain.accounting.AccountingEventTransactionId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * Created with IntelliJ IDEA.
 * User: giancarlo
 * Date: 10/5/12
 * Time: 6:54 PM
 */
public class SaveAccountingEvent implements DomainEvent {

    AccountingEventId accountingEventId;
    AccountingEventTransactionId accountingEventTransactionId;
    UserActiveDirectoryId userActiveDirectoryId;
    TradeServiceId tradeServiceId;
    TradeService tradeService;

    public SaveAccountingEvent(AccountingEventId accountingEventId,
                               AccountingEventTransactionId accountingEventTransactionId,
                               UserActiveDirectoryId userActiveDirectoryId, TradeServiceId tradeServiceId,
                               TradeService tradeService) {
        this.accountingEventId = accountingEventId;
        this.accountingEventTransactionId = accountingEventTransactionId;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.tradeServiceId = tradeServiceId;
        this.tradeService = tradeService;
    }

    public AccountingEventId getAccountingEventId() {
        return accountingEventId;
    }

    public AccountingEventTransactionId getAccountingEventTransactionId() {
        return accountingEventTransactionId;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public TradeService getTradeService() {
        return tradeService;
    }
}
