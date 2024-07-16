package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.Indemnity;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class IndemnityCreatedEvent implements DomainEvent {

    private TradeService tradeService;
    private Indemnity indemnity;
    private LetterOfCredit letterOfCredit;
    private String gltsNumber;

    public IndemnityCreatedEvent(TradeService tradeService, Indemnity indemnity, LetterOfCredit letterOfCredit, String gltsNumber) {
        this.tradeService = tradeService;
        this.indemnity = indemnity;
        this.letterOfCredit = letterOfCredit;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public Indemnity getIndemnity() {
        return indemnity;
    }

    public LetterOfCredit getLetterOfCredit() {
        return letterOfCredit;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
