package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: giancarlo
 * Date: 10/2/12
 * Time: 12:44 PM
 */
public class AccountingEntryActual {
    private long id;
    private Long sequenceNumber;
    private String unitCode;
    private String respondingUnitCode;
    private String bookCode;
    private String transactionAccount;
    private String bookCurrency;
    private String originalCurrency;
    private Date effectiveDate;
    private Date postingDate;
    private String entryType;
    private String accountingCode; //GL Code
    private String particulars;
    private BigDecimal pesoAmount;
    private BigDecimal originalAmount;
    private TradeServiceId tradeServiceId;
    private String sourceBranch;
    private String respondingBranch;
    private String transactionReferenceNumber;

    private ProductId productId;
    private ServiceType serviceType;
    private AccountingEventTransactionId accountingEventTransactionId;
    private String status;

    private String gltsNumber;
    private String accType;//FROM glmast
    private String contingentFlag;
    private String ucpbProductId;
    private String withError;
    private String documentNumberStr;
    private String transactionShortName;
    private Boolean isPosted;


    public AccountingEntryActual() {
    }

    public AccountingEntryActual(
            String unitCode,
            String respondingUnitCode,
            String bookCode,
            String bookCurrency,
            String originalCurrency,
            String entryType,
            String accountingCode,
            String particulars,
            BigDecimal pesoAmount,
            BigDecimal originalAmount,
            TradeServiceId tradeServiceId,
            ProductId productId,
            ServiceType serviceType,
            AccountingEventTransactionId accountingEventTransactionId,
            Date effectiveDate,
            String gltsNumber,
            String accType,
            String contingentFlag,
            String ucpbProductId,
            String status,
            String withError,
            String  documentNumberStr) {
        this.unitCode = unitCode;
        this.respondingUnitCode = respondingUnitCode;
        this.bookCode = bookCode;
        this.bookCurrency = bookCurrency;
        this.originalCurrency = originalCurrency;
        this.entryType = entryType;
        this.accountingCode = accountingCode;
        this.particulars = particulars;
        this.pesoAmount = pesoAmount;
        this.originalAmount = originalAmount;
        this.tradeServiceId = tradeServiceId;
        this.productId = productId;
        this.serviceType = serviceType;
        this.accountingEventTransactionId = accountingEventTransactionId;
        this.effectiveDate = effectiveDate;
        this.gltsNumber = gltsNumber;
        this.accType = accType;
        this.contingentFlag = contingentFlag;
        this.ucpbProductId = ucpbProductId;
        this.status = status;
        this.withError = withError;
        this.documentNumberStr = documentNumberStr;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(String transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public String getBookCurrency() {
        return bookCurrency;
    }

    public void setBookCurrency(String bookCurrency) {
        this.bookCurrency = bookCurrency;
    }

    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public void setOriginalCurrency(String originalCurrency) {
        this.originalCurrency = originalCurrency;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public BigDecimal getPesoAmount() {
        return pesoAmount;
    }

    public void setPesoAmount(BigDecimal pesoAmount) {
        this.pesoAmount = pesoAmount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public String getRespondingBranch() {
        return respondingBranch;
    }

    public void setRespondingBranch(String respondingBranch) {
        this.respondingBranch = respondingBranch;
    }

    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public ProductId getProductId() {
        return productId;
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public AccountingEventTransactionId getAccountingEventTransactionId() {
        return accountingEventTransactionId;
    }

    public void setAccountingEventTransactionId(AccountingEventTransactionId accountingEventTransactionId) {
        this.accountingEventTransactionId = accountingEventTransactionId;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getRespondingUnitCode() {
        return respondingUnitCode;
    }

    public void setRespondingUnitCode(String respondingUnitCode) {
        this.respondingUnitCode = respondingUnitCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }

    public void setGltsNumber(String gltsNumber) {
        this.gltsNumber = gltsNumber;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getContingentFlag() {
        return contingentFlag;
    }

    public void setContingentFlag(String contingentFlag) {
        this.contingentFlag = contingentFlag;
    }

    public String getUcpbProductId() {
        return ucpbProductId;
    }

    public void setUcpbProductId(String ucpbProductId) {
        this.ucpbProductId = ucpbProductId;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }
    
    public String getWithError() {
        return withError;
    }

    public void setWithError(String withError) {
        this.withError = withError;
    }

    public String getDocumentNumberStr() {
        return documentNumberStr;
    }

    public void setDocumentNumberStr(String documentNumberStr) {
        this.documentNumberStr = documentNumberStr;
    }

    public String getTransactionShortName() {
        return transactionShortName;
    }

    public void setTransactionShortName(String transactionShortName) {
        this.transactionShortName = transactionShortName;
    }
    
    public Boolean getIsPosted() {
        return isPosted;
    }

    public void setIsPosted(Boolean isPosted) {
        this.isPosted = isPosted;
    }
}
