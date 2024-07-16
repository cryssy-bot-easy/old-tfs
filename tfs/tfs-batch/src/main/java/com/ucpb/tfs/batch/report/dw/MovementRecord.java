package com.ucpb.tfs.batch.report.dw;

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

//    private static final String TRANSACTION_SYSTEM_CODE = "TD";

//    private static final String BATCH_NUMBER = "852";

//    private static final String TRANSACTION_COST_CENTER = "";

//    private static final String TRANSACTION_PRODUCT_CODE = "";

    private String transactionBranch;

    private String bookCode;

    //gl accounting code
    private String transactionAccount;

    private String transactionCostCenter;

    private String transactionProductCode;

    private String currencyType;

    private Date transactionPostingDate = new Date();

    private Date transactionEffectiveDate;

    private EntryType transactionCode;

    private BigDecimal transactionAmount;

    private BigDecimal transactionBaseAmount;

    private String sourceBranch;

    private String respondingBranch;

    private String transactionDescription;

    private String transactionReferenceNumber;

    private String transactionSystemCode;

    private String transactionBatchNumber;

    private Long transactionSequenceNumber;

    private String productId;

    private String serviceType;

    private String etsNumber;

    private String tradeServiceReferenceNumber;

    private String documentNumber;

    private String cifName;

    private String transactionShortName;

    private String documentClass;

    private String documentType;

    @Field(offset = 1, length = 3, paddingChar = '0', align = Align.RIGHT)
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

    @Field(offset = 6, length = 19, paddingChar = '0', align = Align.RIGHT)
    public String getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(String transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public void setTransactionCostCenter(String transactionCostCenter) {
        this.transactionCostCenter = transactionCostCenter;
    }

    @Field(offset = 25, length = 3, align = Align.RIGHT, paddingChar = '0')
    public String getTransactionCostCenter() {
        return transactionCostCenter;
    }

    public void setTransactionProductCode(String transactionProductCode) {
        this.transactionProductCode = transactionProductCode;
    }

    @Field(offset = 28, length = 3, align = Align.RIGHT, paddingChar = '0')
    public String getTransactionProductCode() {
        return transactionProductCode;
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
        return transactionCode != null ? transactionCode.getCode() : null;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = EntryType.getEntryType(transactionCode);
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

    @Field(offset = 82, length = 3, paddingChar = '0', align = Align.RIGHT)
    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    @Field(offset = 85, length = 3, paddingChar = '0', align = Align.RIGHT)
    public String getRespondingBranch() {
        return respondingBranch;
    }

    public void setRespondingBranch(String respondingBranch) {
        this.respondingBranch = respondingBranch;
    }

    @Field(offset = 88, length = 40)
    public String getTransactionDescription() {

//        String particulars = (etsNumber != null ? etsNumber : tradeServiceReferenceNumber) + " " + productId + " " + transactionDescription;
        String particulars = (getEtsNumber() != null ? getEtsNumber(): getTransactionReferenceNumber()) + " " + getCifName() + " " + getDocumentType()+""+ getDocumentClass()+" "+ getServiceTypeShortName();
        // String particulars = "aBcDEFg /h-i|)(j*&$%#k123==+";

        // Remove all special characters
        // particulars = particulars.replaceAll("[^\\p{L}\\p{Nd}]", "");
        particulars = particulars.replaceAll("[^\\p{L}\\p{N}\\s]", "");

        return particulars;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    @Field(offset = 128, length = 20)
    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber != null ? transactionReferenceNumber.replaceAll("-","") : transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public void setTransactionSystemCode(String transactionSystemCode) {
        this.transactionSystemCode = transactionSystemCode;
    }

    @Field(offset = 148, length = 2)
    public String getTransactionSystemCode() {
        return transactionSystemCode;
    }

    public void setTransactionBatchNumber(String transactionBatchNumber) {
        this.transactionBatchNumber = transactionBatchNumber;
    }

    @Field(offset = 150, length = 3)
    public String getTransactionBatchNumber() {
        return transactionBatchNumber;
    }

    @Field(offset = 153, length = 9,align = Align.RIGHT, paddingChar = '0')
    public Long getTransactionSequenceNumber() {
        return transactionSequenceNumber;
    }

    public void setTransactionSequenceNumber(Long transactionSequenceNumber) {
        this.transactionSequenceNumber = transactionSequenceNumber;
    }

    public String getEtsNumber() {
//        return etsNumber.replace("-","");
        return etsNumber;
    }

    public void setEtsNumber(String etsNumber) {
        this.etsNumber = etsNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceTypeShortName(){
        if(serviceType!=null){
            return serviceType.substring(0,4);
        }
        return "";
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        if (productId != null && !productId.isEmpty()) {
            this.productId = productId;
        } else {
            this.productId = "";
        }
    }

    public String getTradeServiceReferenceNumber() {
        return tradeServiceReferenceNumber;
    }

    public void setTradeServiceReferenceNumber(String tradeServiceReferenceNumber) {
        this.tradeServiceReferenceNumber = tradeServiceReferenceNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getCifName() {
        if(cifName!=null){
            if(cifName.length()>4){
                return cifName.substring(0,4);
            }
        }
        return "";
    }

    public void setCifName(String cifName) {
        this.cifName = cifName;
    }

    public String getTransactionShortName() {
//        return transactionShortName;

        if(transactionShortName!=null){
            if(transactionShortName.length()>4){
                return transactionShortName.substring(0,4);
            }
        }
        return "";
    }

    public void setTransactionShortName(String transactionShortName) {
        this.transactionShortName = transactionShortName;
    }

    public String getDocumentClass() {
        if(documentClass!=null){
            if(documentClass.length()>3){
                return documentClass.substring(0,4);
            }
        }
        return "";
    }

    public void setDocumentClass(String documentClass) {
        this.documentClass = documentClass;
    }

    public String getDocumentType() {
        if("FOREIGN".equalsIgnoreCase(documentType)|| "FX".equalsIgnoreCase(documentType)){
                   return "FX";
        }
        if("DOMESTIC".equalsIgnoreCase(documentType)||"DM".equalsIgnoreCase(documentType)){
            return "DM";
        }
        return "";
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
