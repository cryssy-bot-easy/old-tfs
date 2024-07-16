package com.ucpb.tfs.domain.instruction.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import org.springframework.stereotype.Component;

/**
 * User: IPCVal
 * Date: 8/14/12
 */
@Component
public class ServiceInstructionCreatedEvent implements DomainEvent {

    private ServiceInstruction serviceInstruction;
    private ServiceInstructionStatus serviceInstructionStatus;
    private UserActiveDirectoryId userActiveDirectoryId;

    public ServiceInstructionCreatedEvent() {}

    public ServiceInstructionCreatedEvent(ServiceInstruction serviceInstruction, UserActiveDirectoryId userActiveDirectoryId) {
        this.serviceInstruction = serviceInstruction;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public ServiceInstructionCreatedEvent(ServiceInstruction serviceInstruction, ServiceInstructionStatus serviceInstructionStatus, UserActiveDirectoryId userActiveDirectoryId) {
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
}
