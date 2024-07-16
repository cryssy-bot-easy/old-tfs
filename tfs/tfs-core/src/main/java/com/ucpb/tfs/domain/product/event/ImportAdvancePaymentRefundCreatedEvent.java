package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ImportAdvancePayment;
import com.ucpb.tfs.domain.service.TradeService;

public class ImportAdvancePaymentRefundCreatedEvent implements DomainEvent{

    private TradeService tradeService;
    private String gltsNumber;
    private ImportAdvancePayment importAdvancePayment;


    public ImportAdvancePaymentRefundCreatedEvent(TradeService tradeService,ImportAdvancePayment importAdvancePayment, String gltsNumber){
        this.tradeService = tradeService;
        this.importAdvancePayment = importAdvancePayment;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public ImportAdvancePayment getImportAdvancePayment() {
        return importAdvancePayment;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
