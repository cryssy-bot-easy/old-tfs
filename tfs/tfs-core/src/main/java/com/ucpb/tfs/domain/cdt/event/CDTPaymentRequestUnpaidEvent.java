package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * User: IPCVal
 * Date: 2/3/14
 */
public class CDTPaymentRequestUnpaidEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;

    public CDTPaymentRequestUnpaidEvent(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }
}
