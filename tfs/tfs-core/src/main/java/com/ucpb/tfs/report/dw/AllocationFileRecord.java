package com.ucpb.tfs.report.dw;

import java.math.BigDecimal;
import java.util.Date;

/**
 */
public class AllocationFileRecord {

    private Date creationDate;

    private Date bookingDate;

    private String applicationId;

    private String glAccountId;

    private String bookCode;

    private String allocationUnit;

    private String currencyId;

    private String applicationAccountId;

    private String customerId;

    private String productId;

    private BigDecimal phpTransactionAmount;

    private BigDecimal originalTransactionAmount;

    private BigDecimal usdTransactionAmount;

    private String transactionType;

    private Date lastRepricingDate;

    private Date nextReprisingDate;

    private String contractTermDay;

    private String contractTermType;

    private String pastDueFlag;

    private BigDecimal adbAmount;

    public BigDecimal getUsdTransactionAmount() {
        return usdTransactionAmount;
    }

    public void setUsdTransactionAmount(BigDecimal usdTransactionAmount) {
        this.usdTransactionAmount = usdTransactionAmount;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getGlAccountId() {
        return glAccountId;
    }

    public void setGlAccountId(String glAccountId) {
        this.glAccountId = glAccountId;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getAllocationUnit() {
        return allocationUnit;
    }

    public void setAllocationUnit(String allocationUnit) {
        this.allocationUnit = allocationUnit;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getApplicationAccountId() {
        return applicationAccountId;
    }

    public void setApplicationAccountId(String applicationAccountId) {
        this.applicationAccountId = applicationAccountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getPhpTransactionAmount() {
        return phpTransactionAmount;
    }

    public void setPhpTransactionAmount(BigDecimal phpTransactionAmount) {
        this.phpTransactionAmount = phpTransactionAmount;
    }

    public BigDecimal getOriginalTransactionAmount() {
        return originalTransactionAmount;
    }

    public void setOriginalTransactionAmount(BigDecimal originalTransactionAmount) {
        this.originalTransactionAmount = originalTransactionAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Date getLastRepricingDate() {
        return lastRepricingDate;
    }

    public void setLastRepricingDate(Date lastRepricingDate) {
        this.lastRepricingDate = lastRepricingDate;
    }

    public Date getNextReprisingDate() {
        return nextReprisingDate;
    }

    public void setNextReprisingDate(Date nextReprisingDate) {
        this.nextReprisingDate = nextReprisingDate;
    }

    public String getContractTermDay() {
        return contractTermDay;
    }

    public void setContractTermDay(String contractTermDay) {
        this.contractTermDay = contractTermDay;
    }

    public String getContractTermType() {
        return contractTermType;
    }

    public void setContractTermType(String contractTermType) {
        this.contractTermType = contractTermType;
    }

    public String getPastDueFlag() {
        return pastDueFlag;
    }

    public void setPastDueFlag(String pastDueFlag) {
        this.pastDueFlag = pastDueFlag;
    }

    public BigDecimal getAdbAmount() {
        return adbAmount;
    }

    public void setAdbAmount(BigDecimal adbAmount) {
        this.adbAmount = adbAmount;
    }
}
