package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.cdt.CDTRemittance;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 9/25/12
 */
public class CDTRemittanceCreatedEvent implements DomainEvent {

    private TradeService tradeService;
    private CDTRemittance cdtRemittance;
    private String gltsNumber;

    public CDTRemittanceCreatedEvent(TradeService tradeService, CDTRemittance cdtRemittance, String gltsNumber) {
    	this.tradeService = tradeService;
        this.cdtRemittance = cdtRemittance;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public CDTRemittance getCdtRemittance() {
        return cdtRemittance;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
