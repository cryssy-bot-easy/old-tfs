package com.ucpb.tfs.interfaces.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ucpb.tfs.interfaces.util.DateUtil;
import org.apache.commons.lang.StringUtils;

//TODO: REFACTOR THIS.
public class Availment {

	private String cifNumber;
	
	private String documentNumber;
	
	private BigDecimal originalAmount;
	
	private BigDecimal outstandingBalance;
	
//	private Date transactionDate;
	
	private String productCode;
	
	private BigDecimal phpAmount = BigDecimal.ZERO;
	
	private BigDecimal phpOutstandingBalance = BigDecimal.ZERO;
	
	private String currencyCode;
	
	private String assetLiabilityFlag;
	
	private String statusDescription;
	
	private String facilityReferenceNumber;

    private BigDecimal exchangeRate;

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

	public int getTransactionDate() {
//		if(transactionDate != null){
			return DateUtil.formatToInt("MMddyy", new Date());
//		}
//		return 0;
	}
	
	public int getTransactionTime(){
//		if(transactionDate != null){
			return DateUtil.formatToInt("kkmmss", new Date());
//		}
//		return 0;
	}

//	public void setTransactionDate(Date transactionDate) {
//		this.transactionDate = transactionDate;
//	}

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

	public String getAssetLiabilityFlag() {
		return assetLiabilityFlag;
	}

	public void setAssetLiabilityFlag(String assetLiabilityFlag) {
		this.assetLiabilityFlag = assetLiabilityFlag;
	}

	public String getStatusDescription() {
		return statusDescription != null ? statusDescription.toUpperCase() : null;
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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = StringUtils.trim(currencyCode);
	}

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
