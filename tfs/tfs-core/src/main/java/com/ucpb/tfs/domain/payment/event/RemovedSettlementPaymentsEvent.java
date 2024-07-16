package com.ucpb.tfs.domain.payment.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/27/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemovedSettlementPaymentsEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;

    public RemovedSettlementPaymentsEvent(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

}
