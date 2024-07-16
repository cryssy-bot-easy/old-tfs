package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.service.TradeService;

import java.math.BigDecimal;

/**
 * User: IPCVal
 * Date: 9/27/12
 */
public class LCAdjustedEvent implements DomainEvent {

    private BigDecimal exchangeRate;
    private TradeService tradeService;
    private LetterOfCredit letterOfCredit;
    private String gltsNumber;

    public LCAdjustedEvent(TradeService tradeService, LetterOfCredit letterOfCredit, String gltsNumber) {
        this.tradeService = tradeService;
        this.letterOfCredit = letterOfCredit;
        this.gltsNumber = gltsNumber;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public LetterOfCredit getLetterOfCredit() {
        return letterOfCredit;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
