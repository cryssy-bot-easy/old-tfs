package com.ucpb.tfs.core.batch.process.allocationUnitCode;

/**
 * User: IPCVal
 */
public class TradeServiceRecord {

    private String tradeServiceId;
    private String serviceInstructionId;

    private String documentClass;
    private String serviceType;

    private String cifNumber;
    private String branchUnitCode;
    private String allocationUnitCode;
    private String officerCode;
    private String officerName;
    private String errorCode;

    // JSON string
    private String details;

    public String getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(String tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public String getServiceInstructionId() {
        return serviceInstructionId;
    }

    public void setServiceInstructionId(String serviceInstructionId) {
        this.serviceInstructionId = serviceInstructionId;
    }

    public String getDocumentClass() {
        return documentClass;
    }

    public void setDocumentClass(String documentClass) {
        this.documentClass = documentClass;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCifNumber() {
        return cifNumber;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber;
    }

    public String getBranchUnitCode() {
        return branchUnitCode;
    }

    public void setBranchUnitCode(String branchUnitCode) {
        this.branchUnitCode = branchUnitCode;
    }

    public String getAllocationUnitCode() {
        return allocationUnitCode;
    }

    public void setAllocationUnitCode(String allocationUnitCode) {
        this.allocationUnitCode = allocationUnitCode;
    }

    public String getOfficerCode() {
        return officerCode;
    }

    public void setOfficerCode(String officerCode) {
        this.officerCode = officerCode;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
