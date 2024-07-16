package com.ucpb.tfs.domain.audit;

import com.ucpb.tfs.domain.service.TradeServiceId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccountLog implements Serializable {
	
	private Long id;

    private TradeServiceId tradeServiceId;

    private Date dateCreated = new Date();

	private String accountNumber;
	
	private String accountType;

    private BigDecimal initialDeposit;
	
	private String accountPurpose;
	
	private Integer monthlyEstimatedTransactionCount;
	private Integer monthlyEstimatedTransactionVolume;

	private String transactionTypes;
	
	private String branchCode;
	
	private Date openingDate;
	
	private Date closingDate;
	
	private String riskScore;
	
	private BigDecimal accountBalance;
	
	private String applicationCode;
	
	private String accountCurrency;

    private String status;
	
	private String payRollTag;

    // This property is currently being used by CDT only
    private String accountTag;
	
	private int batchFlag;
	
	public int getBatchFlag() {
		return batchFlag;
	}

	public void setBatchFlag(int batchFlag) {
		this.batchFlag = batchFlag;
	}

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountPurpose() {
		return accountPurpose;
	}

	public void setAccountPurpose(String accountPurpose) {
		this.accountPurpose = accountPurpose;
	}

	public Integer getMonthlyEstimatedTransactionCount() {
		return monthlyEstimatedTransactionCount;
	}

	public void setMonthlyEstimatedTransactionCount(
			Integer monthlyEstimatedTransactionCount) {
		this.monthlyEstimatedTransactionCount = monthlyEstimatedTransactionCount;
	}

	public Integer getMonthlyEstimatedTransactionVolume() {
		return monthlyEstimatedTransactionVolume;
	}

	public void setMonthlyEstimatedTransactionVolume(
			Integer monthlyEstimatedTransactionVolume) {
		this.monthlyEstimatedTransactionVolume = monthlyEstimatedTransactionVolume;
	}

	public String getTransactionTypes() {
		return transactionTypes;
	}

	public void setTransactionTypes(String transactionTypes) {
		this.transactionTypes = transactionTypes;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public Date getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}

	public Date getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

	public String getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(String riskScore) {
		this.riskScore = riskScore;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getAccountCurrency() {
		return accountCurrency;
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	public String getPayRollTag() {
		return payRollTag;
	}

	public void setPayRollTag(String payRollTag) {
		this.payRollTag = payRollTag;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }

    public String getAccountTag() {
        return accountTag;
    }

    // Currently being used by CDT only
    public void setAccountTag(String accountTag) {
        this.accountTag = accountTag;
    }
}
