package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.DocumentAgainstAcceptance;
import com.ucpb.tfs.domain.service.TradeService;

public class DAAcceptedEvent implements DomainEvent {

    private TradeService tradeService;
    private DocumentAgainstAcceptance documentAgainstAcceptance;
    private String gltsNumber;

    public DAAcceptedEvent(DocumentAgainstAcceptance documentAgainstAcceptance, TradeService tradeService, String gltsNumber) {
        this.documentAgainstAcceptance = documentAgainstAcceptance;
        this.tradeService = tradeService;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public DocumentAgainstAcceptance getDocumentAgainstAcceptance() {
        return documentAgainstAcceptance;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
