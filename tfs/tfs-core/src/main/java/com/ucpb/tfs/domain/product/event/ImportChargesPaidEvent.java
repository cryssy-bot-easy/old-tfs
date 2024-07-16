package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 9/25/12
 */
public class ImportChargesPaidEvent implements DomainEvent {

    private TradeService tradeService;
    private LetterOfCredit letterOfCredit;
    private String gltsNumber;

    public ImportChargesPaidEvent(TradeService tradeService, LetterOfCredit letterOfCredit, String gltsNumber) {
    	this.tradeService = tradeService;
        this.letterOfCredit = letterOfCredit;
        this.gltsNumber = gltsNumber;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }

    public LetterOfCredit getLetterOfCredit() {
        return letterOfCredit;
    }
}
