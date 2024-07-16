package com.ucpb.tfs.interfaces.domain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import com.ucpb.tfs.interfaces.util.DateUtil;

public class Facility {

	private String cifNumber;
	
	private String facilityType;
	
	private int facilityId;
	
	private Date expiryDate;
	
	private String currency;
	
	private String facilityReferenceNumber;
	
	private BigDecimal limit;

	public String getCifNumber() {
		return cifNumber;
	}

	public void setCifNumber(String cifNumber) {
		this.cifNumber = cifNumber;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(int expiryDate) throws ParseException {
		this.expiryDate = DateUtil.formatToDate("MMddyy", expiryDate);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getFacilityReferenceNumber() {
		return facilityReferenceNumber;
	}

	public void setFacilityReferenceNumber(String facilityReferenceNumber) {
		this.facilityReferenceNumber = facilityReferenceNumber;
	}
	
}
