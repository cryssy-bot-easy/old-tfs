package com.ucpb.tfs.domain.instruction.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;

public class ServiceInstructionReversalUnmarkedEvent implements DomainEvent{

    private ServiceInstructionId serviceInstructionId;
    private TradeServiceId tradeServiceId;
    private TaskStatus originalStatus;

    public ServiceInstructionReversalUnmarkedEvent(ServiceInstructionId serviceInstructionId, TradeServiceId tradeServiceId, TaskStatus originalStatus) {
        this.serviceInstructionId = serviceInstructionId;
        this.tradeServiceId = tradeServiceId;
        this.originalStatus = originalStatus;
    }

    public ServiceInstructionId getServiceInstructionId() {
        return serviceInstructionId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public TaskStatus getOriginalStatus() {
        return originalStatus;
    }
}
