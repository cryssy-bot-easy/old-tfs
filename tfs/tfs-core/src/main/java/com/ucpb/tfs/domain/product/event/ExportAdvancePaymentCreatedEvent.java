package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ExportAdvancePayment;
import com.ucpb.tfs.domain.service.TradeService;

public class ExportAdvancePaymentCreatedEvent implements DomainEvent{

    private TradeService tradeService;
    private String gltsNumber;
    private ExportAdvancePayment exportAdvancePayment;


    public ExportAdvancePaymentCreatedEvent(TradeService tradeService, ExportAdvancePayment exportAdvancePayment, String gltsNumber){
        this.tradeService = tradeService;
        this.exportAdvancePayment = exportAdvancePayment;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public ExportAdvancePayment getExportAdvancePayment() {
        return exportAdvancePayment;
    }
    
    public String getGltsNumber() {
    	return gltsNumber;
    }
}
