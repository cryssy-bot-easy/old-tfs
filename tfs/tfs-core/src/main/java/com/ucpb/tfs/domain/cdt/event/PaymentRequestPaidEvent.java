package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;

public class PaymentRequestPaidEvent implements DomainEvent {

    String iedieirdNumber;
    String processingUnitCode;

    public PaymentRequestPaidEvent(String iedieirdNumber, String processingUnitCode) {
        this.iedieirdNumber = iedieirdNumber;
        this.processingUnitCode = processingUnitCode;
    }

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }

    public String getProcessingUnitCode() {
        return processingUnitCode;
    }
}
