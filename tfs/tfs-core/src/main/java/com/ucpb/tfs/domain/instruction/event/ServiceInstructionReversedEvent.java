package com.ucpb.tfs.domain.instruction.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.service.TradeServiceId;

public class ServiceInstructionReversedEvent implements DomainEvent {

    private ServiceInstructionId serviceInstructionId;
    private TradeServiceId tradeServiceId;

    public ServiceInstructionReversedEvent(ServiceInstructionId serviceInstructionId, TradeServiceId tradeServiceId) {
        this.serviceInstructionId = serviceInstructionId;
        this.tradeServiceId = tradeServiceId;
    }

    public ServiceInstructionId getServiceInstructionId() {
        return serviceInstructionId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }
}
