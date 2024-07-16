package com.ucpb.tfs.batch.report.dw;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*  PROLOGUE:
 * 	(New)
	SCR/ER Number: SCR IBD-16-0219-01
	SCR/ER Description: Generate CIC File
	[Created by:] Jesse James Joson
	[Date Deployed:] 02/24/2016
	Program [New] Details: This file will be the model used to store details of CIC record 
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CicRecord
 */
 
/** PROLOGUE:
* 	(Revision)
	SCR/ER Number:  SCR IBD-16-0219-01
	SCR/ER Description: Generate CIC File
	[Created by:] Jesse James Joson
	[Date Deployed:]  03/30/2016
	Program [New] Details: Modify Datatype of credit limit to long to use even for large value.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CicRecord
*/

public class CicRecord {

	private String recordType;
	private String providerCode;
	private String branchCode;
	private String reportDate;
	private String cifNumber;
	private String role;
	private String docNumber;
	private String contractType;
	private String contractPhase;
	private String contractStatus;
	private String pesoCurrency;
	private String originalCurrency;
	private Date startDate;
	private Date requestDate;
	private Date expiryDate;
	private Date closeDate;
	private Date lastPaymentDate;
	private String creditCode;
	private String resolutionFlag;
	private long creditLimit;	// Change datatype from int to long
	private String productType;
	private String docType;
	private String lcType;
	private String purposeOfCredit;
	private BigDecimal outstandingBalance;
	private BigDecimal cashAmount;
	private int cashFlag;
	private String formattedDate;
	private String transactionType;	
	private String mainCifNumber;
	private String facilityType;
	private String facilityId;
	private String facilityRefNumber;
	private BigDecimal totalNegotiatedCashAmount;
	private Date AdjustDate;
	private String mainCifNumber2;
	private String facilityType2;
	private String facilityId2;
	private String facilityRefNumber2;
	private String startDateStr;
	private String expiryDateStr;
	private String closeDateStr;
	private String lastPaymentDateStr;
	private String refNumber;

	
	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	public String getStartDateStr() {
		return startDateStr;
	}
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	public String getExpiryDateStr() {
		return expiryDateStr;
	}
	public void setExpiryDateStr(String expiryDateStr) {
		this.expiryDateStr = expiryDateStr;
	}
	public String getCloseDateStr() {
		return closeDateStr;
	}
	public void setCloseDateStr(String closeDateStr) {
		this.closeDateStr = closeDateStr;
	}
	public String getLastPaymentDateStr() {
		return lastPaymentDateStr;
	}
	public void setLastPaymentDateStr(String lastPaymentDateStr) {
		this.lastPaymentDateStr = lastPaymentDateStr;
	}
	public Date getAdjustDate() {
		return AdjustDate;
	}
	public String getMainCifNumber2() {
		return mainCifNumber2;
	}
	public void setMainCifNumber2(String mainCifNumber2) {
		this.mainCifNumber2 = mainCifNumber2;
	}
	public String getFacilityType2() {
		return facilityType2;
	}
	public void setFacilityType2(String facilityType2) {
		this.facilityType2 = facilityType2;
	}
	public String getFacilityId2() {
		return facilityId2;
	}
	public void setFacilityId2(String facilityId2) {
		this.facilityId2 = facilityId2;
	}
	public String getFacilityRefNumber2() {
		return facilityRefNumber2;
	}
	public void setFacilityRefNumber2(String facilityRefNumber2) {
		this.facilityRefNumber2 = facilityRefNumber2;
	}
	public void setAdjustDate(Date adjustDate) {
		AdjustDate = adjustDate;
	}
	public String getFacilityRefNumber() {
		return facilityRefNumber;
	}
	public void setFacilityRefNumber(String facilityRefNumber) {
		this.facilityRefNumber = facilityRefNumber;
	}
	public BigDecimal getTotalNegotiatedCashAmount() {
		return totalNegotiatedCashAmount;
	}
	public void setTotalNegotiatedCashAmount(BigDecimal totalNegotiatedCashAmount) {
		this.totalNegotiatedCashAmount = totalNegotiatedCashAmount;
	}
	public String getMainCifNumber() {
		return mainCifNumber;
	}
	public void setMainCifNumber(String mainCifNumber) {
		this.mainCifNumber = mainCifNumber;
	}
	public String getFacilityType() {
		return facilityType;
	}
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String faciltyId) {
		this.facilityId = faciltyId;
	}
	public Date getExpDate() {
		return this.expiryDate;
	}	
	public void setExpDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getProviderCode() {
		return providerCode;
	}
	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getCifNumber() {
		return cifNumber;
	}
	public void setCifNumber(String cifNumber) {
		this.cifNumber = cifNumber;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getDocNumber() {
		return docNumber.replaceAll("-", "");
	}
	public String getDocumentNumber() {
		return docNumber;
	}
	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getContractPhase() {
		return contractPhase;
	}
	public void setContractPhase(String contractPhase) {
		this.contractPhase = contractPhase;
	}
	public String getContractStatus() {
		return contractStatus;
	}
	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	public String getPesoCurrency() {
		return pesoCurrency;
	}
	public void setPesoCurrency(String pesoCurrency) {
		this.pesoCurrency = pesoCurrency;
	}
	public String getOriginalCurrency() {
		return originalCurrency;
	}
	public void setOriginalCurrency(String originalCurrency) {
		this.originalCurrency = originalCurrency;
	}
	public String getStartDate() throws ParseException {
		formattedDate = formatDate(startDate);
		return formattedDate;
	}
	public Date getStartDate2(){
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getRequestDate() throws ParseException {
		formattedDate = formatDate(requestDate);
		return formattedDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public String getExpiryDate() throws ParseException {
		formattedDate = formatDate(expiryDate);
		return formattedDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getCloseDate() throws ParseException {
		formattedDate = formatDate(closeDate);
		return formattedDate;
	}
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}
	public String getLastPaymentDate() throws ParseException {
		formattedDate = formatDate(lastPaymentDate);
		return formattedDate;
	}
	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}
	public String getCreditCode() {
		return creditCode;
	}
	public void setCreditCode(String creditCode) {
		this.creditCode = creditCode;
	}
	public String getResolutionFlag() {
		return resolutionFlag;
	}
	public void setResolutionFlag(String resolutionFlag) {
		this.resolutionFlag = resolutionFlag;
	}
	// Change datatype from int to long
	public long getCreditLimit() {
		return creditLimit;
	}
	// Change datatype from int to long
	public void setCreditLimit(long creditLimit) {
		this.creditLimit = creditLimit;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getLcType() {
		return lcType;
	}
	public void setLcType(String lcType) {
		this.lcType = lcType;
	}
	public String getPurposeOfCredit() {
		return purposeOfCredit;
	}
	public void setPurposeOfCredit(String purposeOfCredit) {
		this.purposeOfCredit = purposeOfCredit;
	}
	public BigDecimal getOutstandingBalance() {
		return outstandingBalance;
	}
	public void setOutstandingBalance(BigDecimal outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}
	public BigDecimal getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}
	public int getCashFlag() {
		return cashFlag;
	}
	public void setCashFlag(int cashFlag) {
		this.cashFlag = cashFlag;
	}
	
	public String getNullDates(){
		String nullDate="";
		return nullDate;
	}
	
	public String formatDate(Date inputDate) throws ParseException{
		String strDate;
		if(inputDate==null) {
			strDate="";
		} else if(inputDate.equals(null) || inputDate.equals("null")){
			strDate="";
		} else {
			SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
			strDate = sdf2.format(inputDate);
		}
		
		return strDate;
	}
	
}
