package com.ucpb.tfs.domain.instruction.event;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;

/**
 */
public class ServiceInstructionTaggedEvent extends ServiceInstructionUpdatedEvent {

    public ServiceInstructionTaggedEvent(){
        //default constructor
    }

    public ServiceInstructionTaggedEvent(ServiceInstruction serviceInstruction, UserActiveDirectoryId userActiveDirectoryId) {
        super(serviceInstruction,userActiveDirectoryId);
    }

    public ServiceInstructionTaggedEvent(ServiceInstruction serviceInstruction, ServiceInstructionStatus serviceInstructionStatus, UserActiveDirectoryId userActiveDirectoryId) {
        super(serviceInstruction,serviceInstructionStatus,userActiveDirectoryId);
    }


}
