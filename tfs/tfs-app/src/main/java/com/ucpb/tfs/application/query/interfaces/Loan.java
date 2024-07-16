package com.ucpb.tfs.application.query.interfaces;

import java.math.BigDecimal;

public class Loan {

	
	//TODO: REFACTOR THIS LATER
	private String mainCifNumber;
	
	private String cifNumber;
	
	private String facilityCode;
	
	private int facilityId;
	
	private int pnNumber;
	
	private long transactionSequenceNumber;
	
	private String documentNumber;
	
	private String accountType = " ";

    //branch unit code
	private int branchNumber;
	
	private int reportingBranch;
	
	private String loanType = "  ";
	
	private String currencyType;
	
	private String shortName = " ";
	
	private int loanTerm;
	
	private String loanTermCode;
	
	private BigDecimal originalBalance;
	
	private int originalLoanDate;
	
	private BigDecimal interestRate;
	
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	
	private int loanDate;
	
	private BigDecimal drawingLimit;
	
	private int maturityDate;
	
	private String officer = " ";
	
	private int paymentFrequency;
	
	private String paymentFrequencyCode;
	
	private int intPaymentFrequency;
	
	private String intPaymentFrequencyCode;

	private String glBook = " ";
	
	private String groupCode = "180";
	
	private BigDecimal orderAmount = BigDecimal.ZERO;
	
	private int orderExpiryDate = 0;

	private String transactionStatus = " ";
	
	private String unlinkFlag;
	
	private String trustUserId;
	
	private Long creditorCode;
	
	private int paymentCode;

	public String getFacilityCode() {
		return facilityCode;
	}

	public void setFacilityCode(String facilityCode) {
		this.facilityCode = facilityCode;
	}

	public long getTransactionSequenceNumber() {
		return transactionSequenceNumber;
	}

	public void setTransactionSequenceNumber(long transactionSequenceNumber) {
		this.transactionSequenceNumber = transactionSequenceNumber;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public int getBranchNumber() {
		return branchNumber;
	}

	public void setBranchNumber(int branchNumber) {
		this.branchNumber = branchNumber;
	}

	public int getReportingBranch() {
		return reportingBranch;
	}

	public void setReportingBranch(int reportingBranch) {
		this.reportingBranch = reportingBranch;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getLoanTerm() {
		return loanTerm;
	}

	public void setLoanTerm(int loanTerm) {
		this.loanTerm = loanTerm;
	}

	public String getLoanTermCode() {
		return loanTermCode;
	}

	public void setLoanTermCode(String loanTermCode) {
		this.loanTermCode = loanTermCode;
	}

	public BigDecimal getOriginalBalance() {
		return originalBalance;
	}

	public void setOriginalBalance(BigDecimal originalBalance) {
		this.originalBalance = originalBalance;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public int getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(int loanDate) {
		this.loanDate = loanDate;
	}

	public BigDecimal getDrawingLimit() {
		return drawingLimit;
	}

	public void setDrawingLimit(BigDecimal drawingLimit) {
		this.drawingLimit = drawingLimit;
	}

	public int getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(int maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getOfficer() {
		return officer;
	}

	public void setOfficer(String officer) {
		this.officer = officer;
	}

	public int getPaymentFrequency() {
		return paymentFrequency;
	}

	public void setPaymentFrequency(int paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}

	public String getPaymentFrequencyCode() {
		return paymentFrequencyCode;
	}

	public void setPaymentFrequencyCode(String paymentFrequencyCode) {
		this.paymentFrequencyCode = paymentFrequencyCode;
	}

	public int getIntPaymentFrequency() {
		return intPaymentFrequency;
	}

	public void setIntPaymentFrequency(int intPaymentFrequency) {
		this.intPaymentFrequency = intPaymentFrequency;
	}

	public String getIntPaymentFrequencyCode() {
		return intPaymentFrequencyCode;
	}

	public void setIntPaymentFrequencyCode(String intPaymentFrequencyCode) {
		this.intPaymentFrequencyCode = intPaymentFrequencyCode;
	}

	public String getGlBook() {
		return glBook;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public int getOrderExpiryDate() {
		return orderExpiryDate;
	}


	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getUnlinkFlag() {
		return unlinkFlag;
	}

	public void setUnlinkFlag(String unlinkFlag) {
		this.unlinkFlag = unlinkFlag;
	}

	public String getTrustUserId() {
		return trustUserId;
	}

	public void setTrustUserId(String trustUserId) {
		this.trustUserId = trustUserId;
	}

	public Long getCreditorCode() {
		return creditorCode;
	}

	public void setCreditorCode(Long creditorCode) {
		this.creditorCode = creditorCode;
	}

	public int getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(int paymentCode) {
		this.paymentCode = paymentCode;
	}

	public String getMainCifNumber() {
		return mainCifNumber;
	}

	public void setMainCifNumber(String mainCifNumber) {
		this.mainCifNumber = mainCifNumber;
	}

	public String getCifNumber() {
		return cifNumber;
	}

	public void setCifNumber(String cifNumber) {
		this.cifNumber = cifNumber;
	}

	public int getOriginalLoanDate() {
		return originalLoanDate;
	}

	public void setOriginalLoanDate(int originalLoanDate) {
		this.originalLoanDate = originalLoanDate;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public int getPnNumber() {
		return pnNumber;
	}

	public void setPnNumber(int pnNumber) {
		this.pnNumber = pnNumber;
	}


    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setGlBook(String glBook) {
        this.glBook = glBook;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public void setOrderExpiryDate(int orderExpiryDate) {
        this.orderExpiryDate = orderExpiryDate;
    }
}
