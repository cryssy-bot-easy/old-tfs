package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.service.TradeService;

public class DPCreatedEvent implements DomainEvent {

	private TradeService tradeService;
    private String gltsNumber;

	public DPCreatedEvent(TradeService tradeService, String gltsNumber) {
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
