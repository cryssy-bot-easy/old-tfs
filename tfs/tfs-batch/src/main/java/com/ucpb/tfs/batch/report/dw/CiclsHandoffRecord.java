package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.FixedFormatPattern;
import com.ancientprogramming.fixedformat4j.annotation.Record;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Record
public class CiclsHandoffRecord {

    private String clientName;
    private String filler;
    private String tinNumber;
    private String tranType;
    private Date processDate;
    private String ciclsProductCode;
    private BigDecimal approvedAmount;
    private BigDecimal outstandingCurrent;
    private BigDecimal outstandingPastDue;


    @Field(offset = 1, length = 9, paddingChar = '0', align = Align.LEFT)
    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    @Field(offset = 10, length = 3, paddingChar = '0')
    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    @Field(offset = 13, length = 6, paddingChar = '0', align = Align.RIGHT)
    @FixedFormatPattern("MMddyy")
    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    @Field(offset = 19, length = 3, paddingChar = ' ', align = Align.LEFT)
    public String getCiclsProductCode() {
        return ciclsProductCode;
    }

    public void setCiclsProductCode(String ciclsProductCode) {
        this.ciclsProductCode = ciclsProductCode;
    }

    @Field(offset = 22, length = 40, paddingChar = ' ', align = Align.LEFT)
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Field(offset = 62, length = 18, paddingChar = '0', align = Align.RIGHT)
    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    @Field(offset = 80, length = 18, paddingChar = '0', align = Align.RIGHT)
    public BigDecimal getOutstandingCurrent() {
        return outstandingCurrent;
    }

    public void setOutstandingCurrent(BigDecimal outstandingCurrent) {
        this.outstandingCurrent = outstandingCurrent;
    }

    @Field(offset = 98, length = 18, paddingChar = '0', align = Align.RIGHT)
    public BigDecimal getOutstandingPastDue() {
        return outstandingPastDue;
    }

    public void setOutstandingPastDue(BigDecimal outstandingPastDue) {
        this.outstandingPastDue = outstandingPastDue;
    }

    @Field(offset = 116, length = 30, paddingChar = ' ')
    public String getFiller() {
        return filler;
    }

    public void SetFiller() {
        this.filler = filler;
    }
    
    private String formatDate(Date date){
        if(date == null){
            return "0";
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMddyy");
        return dateFormatter.format(date);
    }
}