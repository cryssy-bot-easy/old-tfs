package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ExportAdvising;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class ExportAdvisingCancelledEvent implements DomainEvent {

    private TradeService tradeService;
    private String gltsNumber;
    private ExportAdvising cancelledExportAdvising;

    public ExportAdvisingCancelledEvent(TradeService tradeService, ExportAdvising cancelledExportAdvising, String gltsNumber){
        this.tradeService = tradeService;
        this.cancelledExportAdvising = cancelledExportAdvising;
        this.gltsNumber = gltsNumber;
    }

    public ExportAdvising getCancelledExportAdvising() {
        return cancelledExportAdvising;
    }

    public void setCancelledExportAdvising(ExportAdvising cancelledExportAdvising) {
        this.cancelledExportAdvising = cancelledExportAdvising;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public void setTradeService(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
