package com.ucpb.tfs.batch.report.dw;

import org.apache.commons.lang.StringUtils;

public class AllocationUnit {

    private String allocationUnitCode;
    private String completeAllocationUnitCode;

    private String ccbdBranchUnitCode;
    private String completeCcbdBranchUnitCode;

    public String getAllocationUnitCode() {
        return allocationUnitCode;
    }

    public void setAllocationUnitCode(String allocationUnitCode) {
        this.allocationUnitCode = StringUtils.right(allocationUnitCode, 3);
        this.completeAllocationUnitCode = allocationUnitCode;
    }

    public String getCcbdBranchUnitCode() {
        return ccbdBranchUnitCode;
    }

    public void setCcbdBranchUnitCode(String ccbdBranchUnitCode) {
        this.ccbdBranchUnitCode = StringUtils.right(ccbdBranchUnitCode,3);
        this.completeCcbdBranchUnitCode = ccbdBranchUnitCode;
    }

    public String getCompleteCcbdBranchUnitCode() {
        return completeCcbdBranchUnitCode;
    }

    public String getCompleteAllocationUnitCode() {
        return completeAllocationUnitCode;
    }
}
