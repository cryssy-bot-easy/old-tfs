package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.Rebate;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 9/25/12
 */
public class RebateCreatedEvent implements DomainEvent {

    private TradeService tradeService;
    private Rebate rebate;
    private String gltsNumber;

    public RebateCreatedEvent(TradeService tradeService, Rebate rebate, String gltsNumber) {
    	this.tradeService = tradeService;
        this.rebate = rebate;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public Rebate getRebate() {
        return rebate;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
