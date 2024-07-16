package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ExportAdvising;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class ExportAdvisingCreatedEvent implements DomainEvent {

    private TradeService tradeService;
    private String gltsNumber;
    private ExportAdvising exportAdvising;


    public ExportAdvisingCreatedEvent(TradeService tradeService, ExportAdvising exportAdvising, String gltsNumber){
        this.tradeService = tradeService;
        this.exportAdvising = exportAdvising;
        this.gltsNumber = gltsNumber;
    }

    public ExportAdvising getExportAdvising() {
        return exportAdvising;
    }

    public void setExportAdvising(ExportAdvising exportAdvising) {
        this.exportAdvising = exportAdvising;
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
