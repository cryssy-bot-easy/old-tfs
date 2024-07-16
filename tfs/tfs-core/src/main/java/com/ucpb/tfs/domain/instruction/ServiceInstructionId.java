package com.ucpb.tfs.domain.instruction;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 8/3/12
 */
public class ServiceInstructionId implements Serializable {

    private String serviceInstructionId;

    public ServiceInstructionId() {}

    public ServiceInstructionId(final String serviceInstructionId) {
        Validate.notNull(serviceInstructionId);
        this.serviceInstructionId = serviceInstructionId;
    }

    @Override
    public String toString() {
        return serviceInstructionId;
    }

    public String getServiceInstructionId() {
        return serviceInstructionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceInstructionId that = (ServiceInstructionId) o;

        if (!serviceInstructionId.equals(that.serviceInstructionId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return serviceInstructionId.hashCode();
    }
}
