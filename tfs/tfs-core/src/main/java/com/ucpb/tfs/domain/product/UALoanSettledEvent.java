package com.ucpb.tfs.domain.product;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class UALoanSettledEvent implements DomainEvent{

    private TradeService tradeService;

    public UALoanSettledEvent(TradeService tradeService){
        this.tradeService = tradeService;
    }

    public TradeService getTradeService() {
        return tradeService;
    }
}
