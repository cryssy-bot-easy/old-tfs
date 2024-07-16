package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.FixedFormatPattern;
import com.ancientprogramming.fixedformat4j.annotation.Record;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlisHandoffRecon {
    private String companyNumber;
    private String bookCurrency;
    private String bookCode;
    private String lbpAccountingCode;
    private String lbpParticulars;
    private String ucpbAccountingCode;
    private String ucpbParticulars;
    private String transactionCode;
    private String responsibilityCenterNumber;
    private String sourceCode;
    private BigDecimal amount;
    private BigDecimal pesoAmount;
    private Date effectiveDate;
    private String referenceId;
    private String transactionDescription;
    private String remarks;
    private String accentryId;
    private String isApproved;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getBookCurrency() {
        return bookCurrency;
    }

    public void setBookCurrency(String bookCurrency) {
        this.bookCurrency = bookCurrency;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getLbpAccountingCode() {
        return lbpAccountingCode;
    }

    public void setLbpAccountingCode(String lbpAccountingCode) {
        this.lbpAccountingCode = lbpAccountingCode;
    }

    public String getLbpParticulars() {
        return lbpParticulars;
    }

    public void setLbpParticulars(String lbpParticulars) {
        this.lbpParticulars = lbpParticulars;
    }

    public String getUcpbAccountingCode() {
        return ucpbAccountingCode;
    }

    public void setUcpbAccountingCode(String ucpbAccountingCode) {
        this.ucpbAccountingCode = ucpbAccountingCode;
    }

    public String getUcpbParticulars() {
        return ucpbParticulars;
    }

    public void setUcpbParticulars(String ucpbParticulars) {
        this.ucpbParticulars = ucpbParticulars;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getResponsibilityCenterNumber() {
        return responsibilityCenterNumber;
    }

    public void setResponsibilityCenterNumber(String responsibilityCenterNumber) {
        this.responsibilityCenterNumber = responsibilityCenterNumber;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPesoAmount() {
        return pesoAmount;
    }

    public void setPesoAmount(BigDecimal pesoAmount) {
        this.pesoAmount = pesoAmount;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAccentryId() {
        return accentryId;
    }

    public void setAccentryId(String accentryId) {
        this.accentryId = accentryId;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public String exportToExcel() {
        String comma = ",";
        StringBuilder str = new StringBuilder("");
        str.append(getCompanyNumber() + comma);
        str.append(getBookCurrency() + comma);
        str.append(getBookCode() + comma);
        str.append(getUcpbAccountingCode() + comma);
        str.append(getUcpbParticulars() + comma);
        str.append(getLbpAccountingCode() + comma);
        str.append(getLbpParticulars() + comma);
        str.append(getTransactionCode() + comma);
        str.append(getResponsibilityCenterNumber() + comma);
        str.append(getSourceCode()  + comma);
        str.append(getAmount() + comma);
        str.append(getPesoAmount() + comma);
        str.append(formatDate(getEffectiveDate()) + comma);
        str.append(getReferenceId() + comma);
        str.append(getTransactionDescription() + comma);
        str.append(getRemarks() + comma);
        str.append(getAccentryId() + comma);
        str.append(getIsApproved().equalsIgnoreCase("Y") ? "Yes" : "No" + comma);
        return str.toString();
    }
    
    private String formatDate(Date date){
        if(date == null){
            return "0";
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMddyy");
        return dateFormatter.format(date);
    }
}
