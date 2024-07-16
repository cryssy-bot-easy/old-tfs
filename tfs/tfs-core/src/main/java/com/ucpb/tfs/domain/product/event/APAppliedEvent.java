package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayable;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class APAppliedEvent implements DomainEvent {

    private TradeService tradeService;
    private AccountsPayable accountsPayable;
    private String gltsNumber;

    public APAppliedEvent(TradeService tradeService, AccountsPayable accountsPayable, String gltsNumber) {
        this.tradeService = tradeService;
        this.accountsPayable = accountsPayable;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public AccountsPayable getAccountsPayable() {
        return accountsPayable;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
