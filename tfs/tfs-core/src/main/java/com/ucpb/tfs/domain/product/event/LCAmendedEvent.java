package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.service.TradeService;

/**
 */
public class LCAmendedEvent implements DomainEvent{

    private TradeService tradeService;
    private LetterOfCredit originalLc;
    private LetterOfCredit amendedLc;
    private String gltsNumber;

    public LCAmendedEvent(TradeService tradeService, LetterOfCredit originalLc, LetterOfCredit amendedLc, String gltsNumber){
        this.tradeService = tradeService;
        this.originalLc = originalLc;
        this.amendedLc = amendedLc;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public LetterOfCredit getOriginalLc() {
        return originalLc;
    }

    public LetterOfCredit getAmendedLc() {
        return amendedLc;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
