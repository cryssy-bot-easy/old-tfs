package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 2/18/14
 */
public class CDTRefundCreatedEvent implements DomainEvent {

    private final CDTPaymentRequest cdtPaymentRequest;
    private final TradeService tradeService;
    private String gltsNumber;

    public CDTRefundCreatedEvent(TradeService tradeService, CDTPaymentRequest cdtPaymentRequest, String gltsNumber) {
        this.cdtPaymentRequest = cdtPaymentRequest;
        this.gltsNumber = gltsNumber;
        this.tradeService = tradeService;
    }

    public CDTPaymentRequest getCdtPaymentRequest() {
        return cdtPaymentRequest;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
