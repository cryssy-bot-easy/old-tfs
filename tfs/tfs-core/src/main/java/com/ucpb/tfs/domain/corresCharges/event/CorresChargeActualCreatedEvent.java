package com.ucpb.tfs.domain.corresCharges.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.corresCharges.CorresChargeActual;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 1/31/13
 */
public class CorresChargeActualCreatedEvent implements DomainEvent {

    private CorresChargeActual corresChargeActual;
    private TradeService tradeService;
    private LetterOfCredit letterOfCredit;
    private String gltsNumber;

    public CorresChargeActualCreatedEvent(CorresChargeActual corresChargeActual, TradeService tradeService, LetterOfCredit letterOfCredit, String gltsNumber) {
        this.corresChargeActual = corresChargeActual;
        this.tradeService = tradeService;
        this.letterOfCredit = letterOfCredit;
        this.gltsNumber = gltsNumber;
    }

    public CorresChargeActual getCorresChargeActual() {
        return corresChargeActual;
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
