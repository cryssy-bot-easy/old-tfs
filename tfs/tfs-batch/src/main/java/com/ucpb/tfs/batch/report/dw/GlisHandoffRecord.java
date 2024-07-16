package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.FixedFormatPattern;
import com.ancientprogramming.fixedformat4j.annotation.Record;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Record
public class GlisHandoffRecord {
    private String companyNumber;
    private String accountNumber;
    private String transactionCode;
    private String responsibilityCenterNumber;
    private String sourceCode;
    private BigDecimal amount;
    private Date effectiveDate;

    @Field(offset = 1, length = 4)
    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    @Field(offset = 5, length = 10)
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Field(offset = 15, length = 2)
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    @Field(offset = 17, length = 10, paddingChar = '0', align = Align.RIGHT)
    public String getResponsibilityCenterNumber() {
        return responsibilityCenterNumber;
    }

    public void setResponsibilityCenterNumber(String responsibilityCenterNumber) {
        this.responsibilityCenterNumber = responsibilityCenterNumber;
    }

    @Field(offset = 27, length = 3)
    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Field(offset = 30, length = 17, paddingChar = '0', align = Align.RIGHT)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Field(offset = 47, length = 6)
    @FixedFormatPattern("MMddyy")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String exportToExcel() {
        String comma = ",";
        StringBuilder str = new StringBuilder("");
        str.append(getCompanyNumber() + comma);
        str.append(getAccountNumber() + comma);
        str.append(getTransactionCode() + comma);
        str.append(getResponsibilityCenterNumber() + comma);
        str.append(getSourceCode()  + comma);
        str.append(getAmount() + comma);
        str.append(formatDate(getEffectiveDate()) + comma);
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
