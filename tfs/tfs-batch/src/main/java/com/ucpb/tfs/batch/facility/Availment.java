package com.ucpb.tfs.batch.facility;

import java.math.BigDecimal;
import java.util.Date;

import com.ucpb.tfs.batch.util.DateUtil;

public class Availment {

	private String cifNumber;
	
	private String documentNumber;
	
	private BigDecimal originalAmount;
	
	private BigDecimal outstandingBalance;
	
	private BigDecimal phpAmount = BigDecimal.ZERO;
	
	private BigDecimal phpOutstandingBalance = BigDecimal.ZERO;
	
	private String currencyCode;
	
	private String statusDescription;
	
	private Date transactionDate;
	
	public Availment(Date transactionDate){
		this.transactionDate = transactionDate;
	}
	
	public String getCifNumber() {
		return cifNumber;
	}

	public void setCifNumber(String cifNumber) {
		this.cifNumber = cifNumber;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber.replaceAll("-","");
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public BigDecimal getOutstandingBalance() {
		return outstandingBalance;
	}

	public void setOutstandingBalance(BigDecimal outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	public BigDecimal getPhpAmount() {
		return phpAmount;
	}

	public void setPhpAmount(BigDecimal phpAmount) {
		this.phpAmount = phpAmount;
	}

	public BigDecimal getPhpOutstandingBalance() {
		return phpOutstandingBalance;
	}

	public void setPhpOutstandingBalance(BigDecimal phpOutstandingBalance) {
		this.phpOutstandingBalance = phpOutstandingBalance;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getFacilityReferenceNumber() {
		return facilityReferenceNumber;
	}

	public void setFacilityReferenceNumber(String facilityReferenceNumber) {
		this.facilityReferenceNumber = facilityReferenceNumber;
	}

	private String facilityReferenceNumber;
	
	public int getTransactionDate() {
			return DateUtil.formatToInt("MMddyy", transactionDate);
	}
	
	public int getTransactionTime(){
			return DateUtil.formatToInt("kkmmss", transactionDate);
	}
}
