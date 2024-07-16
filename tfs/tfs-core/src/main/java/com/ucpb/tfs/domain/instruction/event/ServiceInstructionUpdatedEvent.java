package com.ucpb.tfs.domain.instruction.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 8/19/12
 */
public class ServiceInstructionUpdatedEvent implements DomainEvent {

    private ServiceInstruction serviceInstruction;
    private ServiceInstructionStatus serviceInstructionStatus;
    private UserActiveDirectoryId userActiveDirectoryId;

    private TradeService tradeService;

    public ServiceInstructionUpdatedEvent() {}

    public ServiceInstructionUpdatedEvent(ServiceInstruction serviceInstruction, UserActiveDirectoryId userActiveDirectoryId) {
        this.serviceInstruction = serviceInstruction;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public ServiceInstructionUpdatedEvent(ServiceInstruction serviceInstruction, UserActiveDirectoryId userActiveDirectoryId, TradeService tradeService) {
        this.serviceInstruction = serviceInstruction;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.tradeService = tradeService;
    }

    public ServiceInstructionUpdatedEvent(ServiceInstruction serviceInstruction, ServiceInstructionStatus serviceInstructionStatus, UserActiveDirectoryId userActiveDirectoryId) {
        this.serviceInstruction = serviceInstruction;
        this.serviceInstructionStatus = serviceInstructionStatus;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public ServiceInstruction getServiceInstruction() {
        return serviceInstruction;
    }

    public ServiceInstructionStatus getServiceInstructionStatus() {
        return serviceInstructionStatus;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

    public TradeService getTradeService() {
        return tradeService;
    }
}
