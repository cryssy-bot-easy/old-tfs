package com.ucpb.tfs.batch.report.dw;

import java.math.BigDecimal;
import java.util.Date;

public class DailyBalanceRecord {
	
	/*	PROLOGUE:
	 * 	(revision)
		SCR/ER Number: ER# 20160505-030
		SCR/ER Description: 1.  The LC 909-03-929-16-00198-8 was amended last March 18, 2016 – only Tenor was amended from sight to usance.
		 						The AE are okay, debit the contingent for sight and credit to usance. But the DW Allocation reported the LC once 
		 						and the ADB are not reported separately  for sight and usance.
							2.  Adjustment on Standby LC tagging was not correctly reported in DW
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 05/05/2016
		Program [Revision] Details: Added variable standbyTagging together with its getter and setter.
		Date deployment: 
		Member Type: JAVA
		Project: CORE
		Project Name: DailyBalanceRecord.java	
	 */

	private String documentNumber;

    private BigDecimal balance;

    private Date balanceDate;

    private BigDecimal originalBalance;
    
    private BigDecimal revalRate;
    
    private String currency;
    
    private String documentType;

    private String lcType;
    
    private Integer cashFlag;
    
    private BigDecimal cashAmount;
    
    private BigDecimal totalNegotiatedCashAmount;
    
    private String productType;
    
    private String productId;
    
    private String standbyTagging;
    
	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Date getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(Date balanceDate) {
		this.balanceDate = balanceDate;
	}

	public BigDecimal getOriginalBalance() {
		return originalBalance;
	}

	public void setOriginalBalance(BigDecimal originalBalance) {
		this.originalBalance = originalBalance;
	}

	public BigDecimal getRevalRate() {
		return revalRate;
	}

	public void setRevalRate(BigDecimal revalRate) {
		this.revalRate = revalRate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getLcType() {
		return lcType;
	}

	public void setLcType(String lcType) {
		this.lcType = lcType;
	}

	public Integer getCashFlag() {
		return cashFlag;
	}

	public void setCashFlag(Integer cashFlag) {
		this.cashFlag = cashFlag;
	}

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	public BigDecimal getTotalNegotiatedCashAmount() {
		return totalNegotiatedCashAmount;
	}

	public void setTotalNegotiatedCashAmount(BigDecimal totalNegotiatedCashAmount) {
		this.totalNegotiatedCashAmount = totalNegotiatedCashAmount;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getStandbyTagging() {
		return standbyTagging;
	}

	public void setStandbyTagging(String standbyTagging) {
		this.standbyTagging = standbyTagging;
	}
	
}
