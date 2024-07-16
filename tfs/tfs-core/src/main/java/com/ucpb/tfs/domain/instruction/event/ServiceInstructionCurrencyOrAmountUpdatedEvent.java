package com.ucpb.tfs.domain.instruction.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;

/**
 * User: giancarlo
 * Date: 11/9/12
 * Time: 5:15 PM
 */
public class ServiceInstructionCurrencyOrAmountUpdatedEvent implements DomainEvent {

    private ServiceInstruction serviceInstruction;
    private UserActiveDirectoryId userActiveDirectoryId;

    public ServiceInstructionCurrencyOrAmountUpdatedEvent() {
    }

    public ServiceInstructionCurrencyOrAmountUpdatedEvent(ServiceInstruction serviceInstruction, UserActiveDirectoryId userActiveDirectoryId) {
        this.serviceInstruction = serviceInstruction;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public ServiceInstruction getServiceInstruction() {
        return serviceInstruction;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

}