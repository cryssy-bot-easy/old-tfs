package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.service.TradeService;

import java.util.List;

/**
 * User: IPCVal
 * Date: 2/27/14
 */
public class CDTRemittanceErrorCorrectedEvent implements DomainEvent {

    TradeService tradeService;
    List<CDTPaymentRequest> cdtPaymentRequests;

    public  CDTRemittanceErrorCorrectedEvent(TradeService tradeService, List<CDTPaymentRequest> cdtPaymentRequests) {
        this.tradeService = tradeService;
        this.cdtPaymentRequests = cdtPaymentRequests;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public List<CDTPaymentRequest> getCdtPaymentRequests() {
        return cdtPaymentRequests;
    }
}
