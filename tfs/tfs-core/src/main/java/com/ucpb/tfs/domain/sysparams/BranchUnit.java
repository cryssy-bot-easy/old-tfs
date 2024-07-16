package com.ucpb.tfs.domain.sysparams;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/14/14
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class BranchUnit {

    private Long id;

    private String unitCode;

    private String branchName;
    
    private String branchAddress;
    
    private String branchType;
    
    private String swiftStatus;

    public BranchUnit() {}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchAddress() {
		return branchAddress;
	}

	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}

	public String getBranchType() {
		return branchType;
	}

	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}

	public String getSwiftStatus() {
		return swiftStatus;
	}

	public void setSwiftStatus(String swiftStatus) {
		this.swiftStatus = swiftStatus;
	}

}
