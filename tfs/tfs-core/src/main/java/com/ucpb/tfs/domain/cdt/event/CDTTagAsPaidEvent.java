package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * Created by Marv on 2/6/14.
 */
public class CDTTagAsPaidEvent implements DomainEvent {

    private final TradeService tradeService;
    private String iedieirdNumber;
    private String processingUnitCode;

    public CDTTagAsPaidEvent(String iedieirdNumber, String processingUnitCode, TradeService tradeService) {
        this.iedieirdNumber = iedieirdNumber;
        this.processingUnitCode = processingUnitCode;
        this.tradeService = tradeService;
    }

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }

    public String getProcessingUnitCode() {
        return processingUnitCode;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

}
