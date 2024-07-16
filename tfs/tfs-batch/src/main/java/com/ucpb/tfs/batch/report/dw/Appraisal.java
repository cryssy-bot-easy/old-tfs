package com.ucpb.tfs.batch.report.dw;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class Appraisal {

    private Date appraisalDate;

    private BigDecimal appraisedValue;
    
    private String securityCode;

    public Date getAppraisalDate() {
        return appraisalDate;
    }

    public void setAppraisalDate(Integer appraisalDate){
    	this.appraisalDate = toDate(appraisalDate);
    }

    public BigDecimal getAppraisedValue() {
        return appraisedValue;
    }

    public void setAppraisedValue(BigDecimal appraisedValue) {
        this.appraisedValue = appraisedValue;
    }

    private Date toDate(Integer dateInt){
        SimpleDateFormat format = new SimpleDateFormat("MMddyy");
        try {
            return format.parse(StringUtils.leftPad(dateInt.toString(),6,"0"));
        } catch (ParseException e) {
            throw new RuntimeException("Input date format is invalid",e);
        }
    }

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
}
