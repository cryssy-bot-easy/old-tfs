package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.settlementaccount.AccountsReceivable;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class ARCreatedEvent implements DomainEvent {

    private TradeService tradeService;
    private AccountsReceivable accountsReceivable;
    private String gltsNumber;

    public ARCreatedEvent(TradeService tradeService, AccountsReceivable accountsReceivable, String gltsNumber) {
        this.tradeService = tradeService;
        this.accountsReceivable = accountsReceivable;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public AccountsReceivable getAccountsReceivable() {
        return accountsReceivable;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
