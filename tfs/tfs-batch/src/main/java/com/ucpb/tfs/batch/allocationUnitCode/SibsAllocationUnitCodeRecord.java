package com.ucpb.tfs.batch.allocationUnitCode;

/**
 * User: IPCVal
 */
public class SibsAllocationUnitCodeRecord {

    private String branchUnitCode;
    private String allocationUnitCode;
    private String officerCode;
    private String officerName;
    private String errorCode;

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
}
