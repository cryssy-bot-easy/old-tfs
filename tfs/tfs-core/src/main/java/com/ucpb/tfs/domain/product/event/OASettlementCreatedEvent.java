package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.OpenAccount;
import com.ucpb.tfs.domain.service.TradeService;

public class OASettlementCreatedEvent implements DomainEvent {

	private TradeService tradeService;
    private OpenAccount openAccount;
    private String gltsNumber;

	public OASettlementCreatedEvent(TradeService tradeService, OpenAccount openAccount, String gltsNumber) {
        this.tradeService = tradeService;
        this.openAccount = openAccount;
        this.gltsNumber = gltsNumber;
    }
	
    public TradeService getTradeService() {
        return tradeService;
    }

    public OpenAccount getOpenAccount() {
        return openAccount;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
