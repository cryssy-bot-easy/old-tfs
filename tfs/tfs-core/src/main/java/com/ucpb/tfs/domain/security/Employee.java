package com.ucpb.tfs.domain.security;

import com.ucpb.tfs.domain.security.enums.HeadType;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/* 	PROLOGUE:
 *	(revision)
	SCR/ER Number: 20160321-081  
	SCR/ER Description: No "Create Transaction" under NON-LC for IBDMCB user.
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 06/08/2016
	Program [Revision] Details: The User Maintenance was revised by making the position required for users with unit code 909 and 910
								and the level is automatically updated in SEC_EMPLOYEE based from the position of the user.
	Date deployment: 06/17/2016
	Member Type: JAVA
	Project: CORE
	Project Name: Employee.java
 */

@Audited
public class Employee implements Serializable {

    private UserId userId;

    private String firstName;
    private String lastName;
    private String fullName;

    public String getFullName() {
		return fullName;
	}

	private String position;

    private String tellerId;

    private String email;

    private String unitCode;
    private String fullUnitCode;

    private Boolean postingAuthority;
    private BigDecimal postingLimit;
    private Integer level;

    private Boolean suspended;

    private String regionUnitCode;
    private HeadType headType;

    private BigDecimal casaLimit;

    private Designation designation;
    
    private Timestamp lastLogin;

    private String updatedByUserId;
    private String updatedByFullName;

    private Boolean receiveEmail;
    
    public Boolean getReceiveEmail() {
		return receiveEmail;
	}

	public void setReceiveEmail(Boolean receiveEmail) {
		this.receiveEmail = receiveEmail;
	}

	public Employee() {
    }

    public Timestamp getLastLogin(){
    	return lastLogin;
    }
    public void setLastLogin(Timestamp lastLogin){
    	this.lastLogin = lastLogin;
    }
    public Boolean getPostingAuthority() {
        return postingAuthority;
    }

    public BigDecimal getPostingLimit() {
        return postingLimit;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
		this.level = level;
	}

	public Employee(UserId userId, String firstName, String lastName, String unitCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.unitCode = unitCode;
        this.userId = userId;
    }

    public Employee(UserId userId, String fullname, String unitCode) {

        String[] split = fullname.split(" ");

        this.firstName = "";
        this.lastName = split[split.length-1];

        for(int x=0; x < split.length-1; x++) {
            this.firstName = this.firstName + " " + split[x];
        }

        this.firstName = this.firstName.trim();

        this.fullName = fullname;
        // this.firstName = firstName;
        // this.lastName = lastName;
//        this.unitCode = unitCode;

        this.fullUnitCode = unitCode;

        if (this.fullUnitCode != null && StringUtils.isNotBlank(this.fullUnitCode)) {
            this.unitCode = this.fullUnitCode.substring(2, this.fullUnitCode.length());
        }

        this.userId = userId;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
   

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setPostingAuthority(Boolean postingAuthority) {
        this.postingAuthority = postingAuthority;
    }

    public void setPostingLimit(BigDecimal postingLimit) {
        this.postingLimit = postingLimit;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTellerId() {
        return tellerId;
    }

    public String getTruncatedTellerId() {
        if (getTellerId() != null) {
            int length = getTellerId().length();
            if (length > 3) {
                return "0" + getTellerId().substring(length - 3);
            }
        }

        return getTellerId();
    }

    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void suspendEmployee(String isSuspended) {
        this.suspended = Boolean.FALSE;

        if (isSuspended.equals("Y")) {
            this.suspended = Boolean.TRUE;
        }
    }

    public Boolean isSuspended() {
        if (suspended == Boolean.TRUE) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public String getFullUnitCode() {
        return fullUnitCode;
    }

    public void tagAsRegionHead() {
        this.headType = HeadType.REGION;
    }

    public void tagAsDepartmentHead() {
        this.headType = HeadType.DEPARTMENT;
    }

    public Boolean isRegionHead() {
        if (headType.equals(HeadType.REGION)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public Boolean isDepartmentHead() {
        if (headType.equals(HeadType.DEPARTMENT)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public void setRegionUnitCode(String regionUnitCode) {
        this.regionUnitCode = regionUnitCode;
    }

    public String getRegionUnitCode() {
        return regionUnitCode;
    }

    public void setCasaLimit(BigDecimal casaLimit) {
        this.casaLimit = casaLimit;
    }

    public BigDecimal getCasaLimit() {
        return casaLimit;
    }

    public String getEmail() {
        return email;
    }

    public void setUnitCodes(String unitCode) {
        this.fullUnitCode = unitCode;
        System.out.println("setUnitCodes");
        System.out.println(this.fullUnitCode);
        if (this.fullUnitCode != null && StringUtils.isNotBlank(this.fullUnitCode)) {
            this.unitCode = this.fullUnitCode.substring(2, this.fullUnitCode.length());
        }
    }

    public void assignDesignation(Designation designation) {
        this.designation = designation;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }
    
    public void setFirstName(String firstName) {
    	this.firstName = firstName;
    }

    public void setUpdatedByUserId(String updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    public void setUpdatedByFullName(String updatedByFullName) {
        this.updatedByFullName = updatedByFullName;
    }
}
