package com.ucpb.tfs.interfaces.domain;

import org.apache.commons.lang.StringUtils;

/**
 */
public class AllocationUnit {

    private String allocationUnitCode;

    private String ccbdBranchUnitCode;

    public String getAllocationUnitCode() {
        return allocationUnitCode;
    }

    public void setAllocationUnitCode(String allocationUnitCode) {
        this.allocationUnitCode = allocationUnitCode;
    }

    public String getCcbdBranchUnitCode() {
        return ccbdBranchUnitCode;
    }

    public void setCcbdBranchUnitCode(String ccbdBranchUnitCode) {
        this.ccbdBranchUnitCode = StringUtils.right(ccbdBranchUnitCode,3);
    }
}
