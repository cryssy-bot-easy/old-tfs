package com.ucpb.tfs.domain.cdt;

import com.ucpb.tfs.domain.cdt.enums.AutoDebit;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: RefPas5Client
 */

public class RefPas5Client {

    String ccn;

    String agentBankCode;

    String tin;

    String clientName;

    String casaAccountNumber;

    String contactPerson;
    String phoneNumber;
    String email;
    
    String rmbmEmail;

    String branchEmail;
    
    BigDecimal defaultBankCharge;

    Date registrationDate;

    Boolean feeSharing;

    AutoDebit autoDebitAuthority;

    // UCPB client related fields
    String cifNumber;
    String cifName;
    String accountOfficer;
  
	String ccbdBranchUnitCode;

    String specialInstruction;

    String uploadedBy;

    String unitCode;

    String allocationUnitCode;
    
    String exceptionCode;
    
    String officerCode;
    
    // update user-updateable details
    public void updateDetails(String cifNumber, String cifName, String accountOfficer, String ccbdBranchUnitCode,
                              BigDecimal defaultBankCharge,
                              Boolean feeSharing,
                              String casaAccountNumber, AutoDebit autoDebitAuthority,
                              String contactPerson,
                              String email,
                              String phoneNumber,
                              String branchEmail,
                              String rmbmEmail) {

        this.cifNumber = cifNumber;
        this.cifName = cifName;
        this.accountOfficer = accountOfficer;
        this.ccbdBranchUnitCode = ccbdBranchUnitCode;
        this.defaultBankCharge = defaultBankCharge;
        this.feeSharing = feeSharing;
        this.casaAccountNumber = casaAccountNumber;
        this.autoDebitAuthority = autoDebitAuthority;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.branchEmail = branchEmail;
        this.rmbmEmail = rmbmEmail;
    }

    public String getTin() {
        return tin;
    }

    public Boolean containsCif() {
        if (cifNumber == null ||
            cifName == null ||
            accountOfficer == null ||
            ccbdBranchUnitCode == null) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public void setCIFDetails(Map<String, Object> cifDetails) {
//        this.cifNumber = (String) cifDetails.get("CIF_NUMBER");
//        this.cifName = (String) cifDetails.get("CIF_NAME");
//
//        this.accountOfficer = (String) cifDetails.get("OFFICER_NAME");
//
//
//        this.ccbdBranchUnitCode = (String) cifDetails.get("BRANCH_UNIT_CODE");

        // trims whitespaces
        this.cifNumber = cifDetails.get("CIF_NUMBER").toString().trim();
        this.cifName = cifDetails.get("CIF_NAME").toString().trim();

        this.accountOfficer = cifDetails.get("OFFICER_NAME").toString().trim();


        this.ccbdBranchUnitCode = cifDetails.get("BRANCH_UNIT_CODE").toString().trim();
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode= unitCode;
    }

    public String getCcbdBranchUnitCode() {
        return ccbdBranchUnitCode;
    }

    public void setCcbdBranchUnitCode(String ccbdBranchUnitCode) {
        this.ccbdBranchUnitCode = ccbdBranchUnitCode;
    }
    
    public String getEmail() {
		return email;
	}

	public String getRmbmEmail() {
		return rmbmEmail;
	}
	
    public String getBranchEmail() {
		return branchEmail;
	}
	
    public void setEmail(String email) {
		this.email = email;
	}

	public void setRmbmEmail(String rmbmEmail) {
		this.rmbmEmail = rmbmEmail;
	}
	
    public void setBranchEmail(String branchEmail) {
		this.branchEmail = branchEmail;
	}

    public void setDefaultBankCharge(BigDecimal defaultBankCharge) {
        this.defaultBankCharge = defaultBankCharge;
    }
    
    public BigDecimal getDefaultBankCharge() {
        return defaultBankCharge;
    }
    
    
	public String getAllocationUnitCode() {		
		return allocationUnitCode;		
	}		
	public void setAllocationUnitCode(String allocationUnitCode) {		
		this.allocationUnitCode = allocationUnitCode;		
	}

	public String getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public String getCifNumber() {
		return cifNumber;
	}

	public String getOfficerCode() {
		return officerCode;
	}

	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}
	
	public String getAccountOfficer() {
		return accountOfficer;
	}

	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}
	
    
}
