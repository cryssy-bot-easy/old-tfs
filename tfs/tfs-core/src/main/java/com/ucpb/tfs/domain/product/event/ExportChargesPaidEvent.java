package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 9/25/12
 */
public class ExportChargesPaidEvent implements DomainEvent {

    private TradeService tradeService;
    private String gltsNumber;

    public ExportChargesPaidEvent(TradeService tradeService, String gltsNumber) {
    	this.tradeService = tradeService;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
