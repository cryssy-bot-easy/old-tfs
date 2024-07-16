package com.ucpb.tfs.core.batch.process.allocationUnitCode;

/**
 * User: IPCVal
 */
public class ServiceInstructionRecord {

    private String serviceInstructionId;

    // JSON string
    private String details;

    public String getServiceInstructionId() {
        return serviceInstructionId;
    }

    public void setServiceInstructionId(String serviceInstructionId) {
        this.serviceInstructionId = serviceInstructionId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
