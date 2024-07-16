package com.ucpb.tfs.domain.audit;

import com.ucpb.tfs.domain.service.TradeServiceId;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class TransactionLog implements Serializable {

//	private Long id;

    private TradeServiceId tradeServiceId;

	private Date transactionDate = new Date();
	
	private String transactionReferenceNumber;
		
	private String transactionTypeCode;
	
	private String dealNumber;
	
	private String transactionSubtype;

	private String transactionMode;
	
	private BigDecimal transactionAmount;
	
	private String transactionType;

	private String direction;
	
	private String branchCode;
	
	private String accountNumber;
	
	private Currency settlementCurrency;
	
	private BigDecimal exchangeRate;
	
	private BigDecimal settlementAmount;
	
	private String purpose;
	
	private Counterparty counterparty;
	
	private Bank correspondentBank;
	
	private Institution intermediatoryInstitution;
	
	private Beneficiary beneficiary;
	
	private String productType;
	
	private ProductOwner productOwner;

	private Date inceptionDate;
	
	private Date maturityDate;
	
	private String narration;
	
	private String remarks;
	
	private String nature;
	
	private String fundsSource;
	
	private String certifiedDocuments;
	
	private Date inputDate;

    private String regularDocuments;

    private String transactionCode;
	
	private String paymentMode;

    private Date cifBirthday;
	
	//additional fields AMLA Format 1.0
    private BigDecimal amountToClaim;

	private BigDecimal noOfShares;
    
    private BigDecimal netAssetValue;
    
    private String issuerName1;
    
    private String issuerName2;
    
    private String issuerName3;
    
    private String issuerAddress1;
    
    private String issuerAddress2;
    
    private String issuerAddress3;
    
    private String beneficiaryAccountNo;
	
	private int batchFlag;
	
	public int getBatchFlag() {
		return batchFlag;
	}

	public void setBatchFlag(int batchFlag) {
		this.batchFlag = batchFlag;
	}


    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getTransactionDate() {
		return transactionDate;
	}

	public String getTransactionReferenceNumber() {
		return transactionReferenceNumber;
	}

    public String getRegularDocuments() {
        return regularDocuments;
    }

    public void setRegularDocuments(String regularDocuments) {
        this.regularDocuments = StringUtils.trimWhitespace(regularDocuments);
    }

	public void setTransactionReferenceNumber(String transactionReferenceNumber) {
		this.transactionReferenceNumber = StringUtils.trimWhitespace(transactionReferenceNumber);
	}

	public String getDealNumber() {
		return dealNumber;
	}

	public void setDealNumber(String dealNumber) {
		this.dealNumber = StringUtils.trimWhitespace(dealNumber);
	}

	public String getTransactionTypeCode() {
		return transactionTypeCode;
	}

	public void setTransactionTypeCode(String transactionTypeCode) {
		this.transactionTypeCode = transactionTypeCode;
	}

	public String getTransactionSubtype() {
		return transactionSubtype;
	}

	public void setTransactionSubtype(String transactionSubtype) {
		this.transactionSubtype = transactionSubtype;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Currency getSettlementCurrency() {
		return settlementCurrency;
	}

	public void setSettlementCurrency(Currency settlementCurrency) {
		this.settlementCurrency = settlementCurrency;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Counterparty getCounterparty() {
		return counterparty;
	}

	public void setCounterparty(Counterparty counterparty) {
		this.counterparty = counterparty;
	}

	public Institution getIntermediatoryInstitution() {
		return intermediatoryInstitution;
	}

	public void setIntermediatoryInstitution(Institution intermediatoryInstitution) {
		this.intermediatoryInstitution = intermediatoryInstitution;
	}

	public Beneficiary getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public ProductOwner getProductOwner() {
		return productOwner;
	}

	public void setProductOwner(ProductOwner productOwner) {
		this.productOwner = productOwner;
	}

	public Date getInceptionDate() {
		return inceptionDate;
	}

	public void setInceptionDate(Date inceptionDate) {
		this.inceptionDate = inceptionDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = StringUtils.trimWhitespace(narration);
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = StringUtils.trimWhitespace(remarks);
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getFundsSource() {
		return fundsSource;
	}

	public void setFundsSource(String fundsSource) {
		this.fundsSource = fundsSource;
	}

	public String getCertifiedDocuments() {
		return certifiedDocuments;
	}

	public void setCertifiedDocuments(String certifiedDocuments) {
		this.certifiedDocuments = certifiedDocuments;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}



//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}

	public Bank getCorrespondentBank() {
		return correspondentBank;
	}

	public void setCorrespondentBank(Bank correspondentBank) {
		this.correspondentBank = correspondentBank;
	}

    public Date getCifBirthday() {
        return cifBirthday;
    }

    public void setCifBirthday(Date cifBirthday) {
        this.cifBirthday = cifBirthday;
    }
	
	//additional fields AMLA Format 1.0
    public BigDecimal getAmountToClaim() {
		return this.amountToClaim;
	}

	public void setAmountToClaim(BigDecimal amountToClaim) {
		this.amountToClaim = amountToClaim;
	}

	public BigDecimal getNoOfShares() {
		return this.noOfShares;
	}

	public void setNoOfShares(BigDecimal noOfShares) {
		this.noOfShares = noOfShares;
	}

	public BigDecimal getNetAssetValue() {
		return this.netAssetValue;
	}

	public void setNetAssetValue(BigDecimal netAssetValue) {
		this.netAssetValue = netAssetValue;
	}

	public String getIssuerName1() {
		return this.issuerName1;
	}

	public void setIssuerName1(String issuerName1) {
		this.issuerName1 = issuerName1;
	}

	public String getIssuerName2() {
		return this.issuerName2;
	}

	public void setIssuerName2(String issuerName2) {
		this.issuerName2 = issuerName2;
	}

	public String getIssuerName3() {
		return this.issuerName3;
	}

	public void setIssuerName3(String issuerName3) {
		this.issuerName3 = issuerName3;
	}

	public String getIssuerAddress1() {
		return this.issuerAddress1;
	}

	public void setIssuerAddress1(String issuerAddress1) {
		this.issuerAddress1 = issuerAddress1;
	}

	public String getIssuerAddress2() {
		return this.issuerAddress2;
	}

	public void setIssuerAddress2(String issuerAddress2) {
		this.issuerAddress2 = issuerAddress2;
	}

	public String getIssuerAddress3() {
		return this.issuerAddress3;
	}

	public void setIssuerAddress3(String issuerAddress3) {
		this.issuerAddress3 = issuerAddress3;
	}

	public String getBeneficiaryAccountNo() {
		return this.beneficiaryAccountNo;
	}

	public void setBeneficiaryAccountNo(String beneficiaryAccountNo) {
		this.beneficiaryAccountNo = beneficiaryAccountNo;
	}
}
