package com.ucpb.tfs.interfaces.domain.enums;

/**
 * User: IPCVal
 * Date: 10/22/12
 */
public enum EarmarkingStatusDescription {

    CURRENT("current"),
    CLOSED("closed"),
    MATURED("matured"),
    CANCELLED("cancelled");

    private String statusDescription;

    private EarmarkingStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String toString() {
       return this.statusDescription;
    }
}
