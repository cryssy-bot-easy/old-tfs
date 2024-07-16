package com.ucpb.tfs.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.FixedFormatPattern;
import com.ancientprogramming.fixedformat4j.annotation.Record;

import java.math.BigDecimal;
import java.util.Date;

/**
 */
@Record
public class MovementRecord {

    private static final String TRANSACTION_SYSTEM_CODE = "TD";

    private static final String BATCH_NUMBER = "852";

    private static final String TRANSACTION_COST_CENTER = "";

    private static final String TRANSACTION_PRODUCT_CODE = "";

    private String transactionBranch;

    private String bookCode;

    private String transactionAccount;

    private String currencyType;

    private Date transactionPostingDate = new Date();

    private Date transactionEffectiveDate;

    private String transactionCode;

    private BigDecimal transactionAmount;

    private BigDecimal transactionBaseAmount;

    private String sourceBranch;

    private String respondingBranch;

    private String transactionDescription;

    private String transactionReferenceNumber;

    private Integer transactionSequenceNumber;

    @Field(offset = 1, length = 3)
    public String getTransactionBranch() {
        return transactionBranch;
    }

    public void setTransactionBranch(String transactionBranch) {
        this.transactionBranch = transactionBranch;
    }

    @Field(offset = 4, length = 2)
    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    @Field(offset = 6, length = 19)
    public String getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(String transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    @Field(offset = 25, length = 3, align = Align.RIGHT, paddingChar = '0')
    public String getTransactionCostCenter() {
        return TRANSACTION_COST_CENTER;
    }

    @Field(offset = 28, length = 3, align = Align.RIGHT, paddingChar = '0')
    public String getTransactionProductCode() {
        return TRANSACTION_PRODUCT_CODE;
    }

    @Field(offset = 31, length = 4)
    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    @Field(offset = 35, length = 6)
    @FixedFormatPattern("MMddyy")
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    @Field(offset = 41, length = 6)
    @FixedFormatPattern("MMddyy")
    public Date getTransactionEffectiveDate() {
        return transactionEffectiveDate;
    }

    public void setTransactionEffectiveDate(Date transactionEffectiveDate) {
        this.transactionEffectiveDate = transactionEffectiveDate;
    }

    @Field(offset = 47, length = 1)
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    @Field(offset = 48, length = 17, align = Align.RIGHT, paddingChar = '0')
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Field(offset = 65, length = 17, align = Align.RIGHT, paddingChar = '0')
    public BigDecimal getTransactionBaseAmount() {
        return transactionBaseAmount;
    }

    public void setTransactionBaseAmount(BigDecimal transactionBaseAmount) {
        this.transactionBaseAmount = transactionBaseAmount;
    }

    @Field(offset = 82, length = 3)
    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    @Field(offset = 85, length = 3)
    public String getRespondingBranch() {
        return respondingBranch;
    }

    public void setRespondingBranch(String respondingBranch) {
        this.respondingBranch = respondingBranch;
    }

    @Field(offset = 88, length = 40)
    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    @Field(offset = 128, length = 20)
    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    @Field(offset = 148, length = 2)
    public String getTransactionSystemCode() {
        return TRANSACTION_SYSTEM_CODE;
    }

    @Field(offset = 150, length = 3)
    public String getTransactionBatchNumber() {
        return BATCH_NUMBER;
    }

    @Field(offset = 153, length = 9,align = Align.RIGHT, paddingChar = '0')
    public Integer getTransactionSequenceNumber() {
        return transactionSequenceNumber;
    }

    public void setTransactionSequenceNumber(Integer transactionSequenceNumber) {
        this.transactionSequenceNumber = transactionSequenceNumber;
    }
}
