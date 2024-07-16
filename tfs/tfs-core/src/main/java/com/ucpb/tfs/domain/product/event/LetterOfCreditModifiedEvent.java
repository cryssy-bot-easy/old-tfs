package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 9/25/12
 */
public class LetterOfCreditModifiedEvent implements DomainEvent {

    private TradeService tradeService;
    private LetterOfCredit lc;


    public LetterOfCreditModifiedEvent(TradeService tradeService, LetterOfCredit lc) {
        this.tradeService = tradeService;
        this.lc = lc;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public LetterOfCredit getLc() {
        return lc;
    }
}
