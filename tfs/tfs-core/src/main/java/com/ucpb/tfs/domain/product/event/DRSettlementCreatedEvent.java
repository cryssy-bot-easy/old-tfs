package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.DirectRemittance;
import com.ucpb.tfs.domain.service.TradeService;

public class DRSettlementCreatedEvent implements DomainEvent {

	private TradeService tradeService;
    private DirectRemittance directRemittance;
    private String gltsNumber;

	public DRSettlementCreatedEvent(TradeService tradeService, DirectRemittance directRemittance, String gltsNumber) {
        this.tradeService = tradeService;
        this.directRemittance = directRemittance;
        this.gltsNumber = gltsNumber;
    }
	
    public TradeService getTradeService() {
        return tradeService;
    }

    public DirectRemittance getDirectRemittance() {
        return directRemittance;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
