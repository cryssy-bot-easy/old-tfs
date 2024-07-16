package com.ucpb.tfs.domain.payment.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * User: IPCVal
 * Date: 10/20/12
 */
public class PaymentSavedEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;
    private Payment payment;
    private UserActiveDirectoryId userActiveDirectoryId;

    public PaymentSavedEvent(TradeServiceId tradeServiceId, Payment payment) {
        this.tradeServiceId = tradeServiceId;
        this.payment = payment;
    }
    
    public PaymentSavedEvent(TradeServiceId tradeServiceId, Payment payment, UserActiveDirectoryId userActiveDirectoryId) {
    	this.tradeServiceId = tradeServiceId;
    	this.payment = payment;
    	this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public Payment getPayment() {
        return payment;
    }
    
    public UserActiveDirectoryId getUserActiveDirectoryId() {
    	return userActiveDirectoryId;
    }
}
