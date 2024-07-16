package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 9/25/12
 */
public class LCNegotiationCreatedEvent implements DomainEvent {

    private TradeService tradeService;
    private LetterOfCredit negotiatedLetterOfCredit;
    private String gltsNumber;

    public LCNegotiationCreatedEvent(TradeService tradeService, LetterOfCredit negotiatedLetterOfCredit, String gltsNumber) {
        this.tradeService = tradeService;
        this.negotiatedLetterOfCredit = negotiatedLetterOfCredit;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public LetterOfCredit getNegotiatedLetterOfCredit() {
        return negotiatedLetterOfCredit;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}