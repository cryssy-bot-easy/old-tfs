package com.ucpb.tfs.domain.product.event;


import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;

public class CDTPaymentRequestPaidEvent implements DomainEvent {

    private CDTPaymentRequest cdtPaymentRequest;

    public CDTPaymentRequestPaidEvent(CDTPaymentRequest cdtPaymentRequest){
        this.cdtPaymentRequest = cdtPaymentRequest;
    }

    public CDTPaymentRequest getCdtPaymentRequest() {
        return cdtPaymentRequest;
    }
}
