package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ExportAdvising;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class ExportAdvisingAmendedEvent implements DomainEvent {

    private TradeService tradeService;
    private String gltsNumber;
    private ExportAdvising amendedExportAdvising;

    public ExportAdvisingAmendedEvent(TradeService tradeService, ExportAdvising amendedExportAdvising, String gltsNumber){
        this.tradeService = tradeService;
        this.amendedExportAdvising = amendedExportAdvising;
        this.gltsNumber = gltsNumber;
    }


    public ExportAdvising getAmendedExportAdvising() {
        return amendedExportAdvising;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
