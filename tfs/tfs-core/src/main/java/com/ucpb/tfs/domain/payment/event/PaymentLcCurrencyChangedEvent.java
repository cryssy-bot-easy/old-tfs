package com.ucpb.tfs.domain.payment.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;


/**
 * User: IPCVal
 * Date: 10/16/12
 */
public class PaymentLcCurrencyChangedEvent implements DomainEvent {

    private ServiceInstructionId serviceInstructionId;

    public PaymentLcCurrencyChangedEvent(ServiceInstructionId serviceInstructionId) {
        this.serviceInstructionId = serviceInstructionId;
    }

    public ServiceInstructionId getServiceInstructionId() {
        return serviceInstructionId;
    }
}
