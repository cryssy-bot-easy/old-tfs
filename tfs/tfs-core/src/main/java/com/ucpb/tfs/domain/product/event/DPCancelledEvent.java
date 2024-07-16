package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.DocumentAgainstPayment;
import com.ucpb.tfs.domain.service.TradeService;

public class DPCancelledEvent implements DomainEvent {

	private TradeService tradeService;
    private DocumentAgainstPayment documentAgainstPayment;
    private String gltsNumber;

	public DPCancelledEvent(TradeService tradeService, DocumentAgainstPayment documentAgainstPayment, String gltsNumber) {
        this.tradeService = tradeService;
        this.documentAgainstPayment = documentAgainstPayment;
        this.gltsNumber = gltsNumber;
    }
	
    public TradeService getTradeService() {
        return tradeService;
    }

    public DocumentAgainstPayment getDocumentAgainstPayment() {
        return documentAgainstPayment;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
