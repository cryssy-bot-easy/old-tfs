package com.ucpb.tfs.interfaces.domain;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

public class Loan {


    public static final String UNLINK_FLAG = "U";

    public static final String WITH_CRAM_OVERRIDE = "C";

    public static final String NO_CRAM_OVERRIDE = "";

	//TODO: REFACTOR THIS LATER
	private String mainCifNumber;
	
	private String cifNumber;
	
	private String facilityCode;

    private int facilitySequence;
	
	private int facilityId;
	
	private long pnNumber = 0;
	
	private long transactionSequenceNumber;
	
	private String documentNumber;

    private String importer = " ";
	
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
	
	private String loanDate;
	
	private BigDecimal drawingLimit;
	
	private int maturityDate;
	
	private String officer = " ";
	
	private int paymentFrequency;
	
	private String paymentFrequencyCode;
	
	private int intPaymentFrequency;
	
	private String intPaymentFrequencyCode;

	private String glBook = " ";
	
	private int groupCode = 180;
	
	private BigDecimal orderAmount = BigDecimal.ZERO;
	
	private int orderExpiryDate = 0;

	private String transactionStatus = " ";
	
	private String unlinkFlag;
	
	private String trustUserId;

    private String facilityReferenceNumber;

    private Long creditorCode;
	
	private int paymentCode;

    private String agriAgraTagging;

    private String docStampCalculationFlag;

    private BigDecimal interestFreeFloatDays;

	public String getFacilityCode() {
		return facilityCode;
	}

	public void setFacilityCode(String facilityCode) {
        Assert.notNull(facilityCode,"Facility Code must not be null");
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
        Assert.notNull(documentNumber,"Document Number must not be null");
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
        Assert.notNull(currencyType,"Currency Type must not be null");
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
        Assert.notNull(loanTermCode,"Loan Term Code must not be null");
        this.loanTermCode = loanTermCode;
	}

	public BigDecimal getOriginalBalance() {
		return originalBalance;
	}

	public void setOriginalBalance(BigDecimal originalBalance) {
        Assert.notNull(originalBalance,"Original Balance must not be null");
        this.originalBalance = originalBalance;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
        Assert.notNull(interestRate,"Interest Rate must not be null");
        this.interestRate = interestRate;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public String getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(String loanDate) {
        Assert.notNull(loanDate,"Loan Date must not be null");
        this.loanDate = loanDate;
	}

	public BigDecimal getDrawingLimit() {
		return drawingLimit;
	}

	public void setDrawingLimit(BigDecimal drawingLimit) {
        Assert.notNull(drawingLimit,"Drawing Limit must not be null");
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
        Assert.notNull(officer,"Officer must not be null");
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
        Assert.notNull(paymentFrequencyCode,"Payment Frequency Code must not be null");
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
        Assert.notNull(intPaymentFrequencyCode,"Interest Payment Frequency Code must not be null");
        this.intPaymentFrequencyCode = intPaymentFrequencyCode;
	}

	public String getGlBook() {
		return glBook;
	}

	public int getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(int groupCode) {
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
        Assert.notNull(trustUserId,"Trust User ID must not be null");
        this.trustUserId = trustUserId;
	}

	public Long getCreditorCode() {
		return creditorCode;
	}

	public void setCreditorCode(Long creditorCode) {
        Assert.notNull(creditorCode,"Creditor Code must not be null");
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
        Assert.notNull(mainCifNumber,"Main CIF Number must not be null");
        this.mainCifNumber = mainCifNumber;
	}

	public String getCifNumber() {
		return cifNumber;
	}

	public void setCifNumber(String cifNumber) {
        Assert.notNull(cifNumber,"CIF Number must not be null");
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

	public long getPnNumber() {
		return pnNumber;
	}

	public void setPnNumber(long pnNumber) {
		this.pnNumber = pnNumber;
	}


    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        Assert.notNull(paymentAmount,"Payment Amount must not be null");
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

    public String getImporter() {
        return importer;
    }

    public void setImporter(String importer) {
        this.importer = importer;
    }

    public int getFacilitySequence() {
        return facilitySequence;
    }

    public void setFacilitySequence(int facilitySequence) {
        this.facilitySequence = facilitySequence;
    }

    public String getFacilityReferenceNumber() {
        return facilityReferenceNumber;
    }

    public void setFacilityReferenceNumber(String facilityReferenceNumber) {
        Assert.notNull(facilityReferenceNumber,"Facility Reference Number must not be null");
        this.facilityReferenceNumber = facilityReferenceNumber;
    }

    public boolean isReversal(){
        return "U".equalsIgnoreCase(unlinkFlag);
    }

    public String getAgriAgraTagging() {
        return agriAgraTagging;
    }

    public void setAgriAgraTagging(String agriAgraTagging) {
        this.agriAgraTagging = agriAgraTagging;
    }

    public String getDocStampCalculationFlag() {
        return docStampCalculationFlag;
    }

    public void setDocStampCalculationFlag(String docStampCalculationFlag) {
        this.docStampCalculationFlag = docStampCalculationFlag;
    }

    public BigDecimal getInterestFreeFloatDays() {
        return interestFreeFloatDays;
    }

    public void setInterestFreeFloatDays(BigDecimal interestFreeFloatDays) {
        this.interestFreeFloatDays = interestFreeFloatDays;
    }
}
