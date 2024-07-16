package com.ucpb.tfs.domain.sysparams;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 1/18/13
 */
public class RefBank implements Serializable {

    private String bic;
    private String branchCode;
    private String institutionName;
    private String branchInfo;
    private String city;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String location;
    private String rmaFlag;
	private String depositoryFlag;
    private String glBankCode;
    private String rbuAccount;
    private String fcduAccount;
    private Currency reimbursingCurrency;
    private Date updateDate;
    private String updatedBy;
    private String glCodeRbu;
    private String glCodeFcdu;
    private Long cbCreditorCode;
    private String deleteFlag;

    public RefBank() {
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public void setLastUpdate(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updateDate = new Date();
    }

    public Long getCbCreditorCode() {
        return cbCreditorCode;
    }

    public void setCbCreditorCode(Long cbCreditorCode) {
        this.cbCreditorCode = cbCreditorCode;
    }

    public String getGlCodeRbu() {
        return glCodeRbu;
    }

    public void setGlCodeRbu(String glCodeRbu) {
        this.glCodeRbu = glCodeRbu;
    }

    public String getGlCodeFcdu() {
        return glCodeFcdu;
    }

    public void setGlCodeFcdu(String glCodeFcdu) {
        this.glCodeFcdu = glCodeFcdu;
    }

    public String getGlCode(String accountType){
        if("FCDU".equalsIgnoreCase(accountType)){
            return  glCodeFcdu;
        } else if("FCDU".equalsIgnoreCase(accountType)){
            return glCodeRbu;
        } else {
            return glCodeFcdu; //Default GL Code
        }
    }

    public String getInstitutionName() {
        return institutionName;
    }

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchInfo() {
		return branchInfo;
	}

	public void setBranchInfo(String branchInfo) {
		this.branchInfo = branchInfo;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
    
    public String getRmaFlag() {
		return rmaFlag;
	}

	public void setRmaFlag(String rmaFlag) {
		this.rmaFlag = rmaFlag;
	}
    
}
