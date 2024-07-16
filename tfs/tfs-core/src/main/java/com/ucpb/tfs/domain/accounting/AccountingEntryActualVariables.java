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
public class AccountingEntryActualVariables {
    private long id;
    private Long sequenceNumber;
    private String bookCode;
    private String transactionAccount;
    private String bookCurrency;
    private Date effectiveDate;
    private Date postingDate;
    private String entryType;
    private BigDecimal amount;
    private String transactionReferenceNumber;
    private String amountCode;
    private String paymentId;

    private TradeServiceId tradeServiceId;

    private ProductId productId;
    private ServiceType serviceType;
    private AccountingEventTransactionId accountingEventTransactionId;

    public AccountingEntryActualVariables() {
    }


    public AccountingEntryActualVariables(
                                           String bookCode,
                                           String bookCurrency,
                                           String entryType,
                                           BigDecimal amount,
                                           TradeServiceId tradeServiceId,
                                           ProductId productId,
                                           ServiceType serviceType,
                                           AccountingEventTransactionId accountingEventTransactionId,
                                           String amountCode,
                                           String paymentId) {
        this.bookCode = bookCode;
        this.bookCurrency = bookCurrency;
        this.postingDate = new Date();
        this.entryType = entryType;
        this.amount = amount;
        this.tradeServiceId = tradeServiceId;
        this.productId = productId;
        this.serviceType = serviceType;
        this.accountingEventTransactionId = accountingEventTransactionId;
        this.amountCode = amountCode;
        this.paymentId = paymentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
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

    public String getAmountCode() {
        return amountCode;
    }

    public void setAmountCode(String amountCode) {
        this.amountCode = amountCode;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
