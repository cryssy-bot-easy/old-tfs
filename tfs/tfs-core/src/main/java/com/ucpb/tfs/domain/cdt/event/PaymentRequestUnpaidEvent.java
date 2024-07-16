package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;

public class PaymentRequestUnpaidEvent implements DomainEvent {

    String iedieirdNumber;

    public PaymentRequestUnpaidEvent(String iedieirdNumber) {
        this.iedieirdNumber = iedieirdNumber;
    }

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }
}
