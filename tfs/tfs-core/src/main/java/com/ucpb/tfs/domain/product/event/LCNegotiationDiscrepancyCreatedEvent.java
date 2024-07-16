package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.LCNegotiationDiscrepancy;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class LCNegotiationDiscrepancyCreatedEvent implements DomainEvent {

    private TradeService tradeService;

    private LCNegotiationDiscrepancy discrepancy;

    public LCNegotiationDiscrepancyCreatedEvent(TradeService tradeService, LCNegotiationDiscrepancy discrepancy){
        this.tradeService = tradeService;
        this.discrepancy = discrepancy;
    }

    public LCNegotiationDiscrepancy getDiscrepancy() {
        return discrepancy;
    }

    public TradeService getTradeService() {
        return tradeService;
    }
}
