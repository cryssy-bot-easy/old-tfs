package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ExportAdvancePayment;
import com.ucpb.tfs.domain.service.TradeService;

public class ExportAdvancePaymentRefundCreatedEvent implements DomainEvent {

    private TradeService tradeService;
    private String gltsNumber;
    private ExportAdvancePayment exportAdvancePayment;


    public ExportAdvancePaymentRefundCreatedEvent(TradeService tradeService, ExportAdvancePayment exportAdvancePayment, String gltsNumber){
        this.tradeService = tradeService;
        this.exportAdvancePayment = exportAdvancePayment;
        this.gltsNumber = gltsNumber;
    }

    public ExportAdvancePayment getExportAdvancePayment() {
        return exportAdvancePayment;
    }

    public TradeService getTradeService() {
        return tradeService;
    }
    
    public String getGltsNumber() {
    	return gltsNumber;
    }
}
