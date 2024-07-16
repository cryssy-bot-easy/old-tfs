package com.ucpb.tfs.domain.payment.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * User: IPCVal
 * Date: 10/20/12
 */
public class PaymentDeletedEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;
    private Payment payment;

    public PaymentDeletedEvent(TradeServiceId tradeServiceId, Payment payment) {
        this.tradeServiceId = tradeServiceId;
        this.payment = payment;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public Payment getPayment() {
        return payment;
    }
}
