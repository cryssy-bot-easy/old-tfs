package com.ucpb.tfs.domain.payment.modes;

import com.ucpb.tfs.interfaces.domain.Facility;
import com.ucpb.tfs.utils.DateUtil;

import java.text.ParseException;
import java.util.Date;

/**
 * User: Jett
 * Date: 7/12/12
 */
public class Loan extends PaymentMode {

    private Facility facility;

    private Integer paymentCode;

    private Boolean approvedByCram;

    private Date maturityDate;

    private Integer loanTerm;

    private Integer paymentTerm;

    private String paymentTermCode;

    private String loanTermCode;


    public Boolean getApprovedByCram() {
        return approvedByCram;
    }

    public void setApprovedByCram(Boolean approvedByCram) {
        this.approvedByCram = approvedByCram;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Integer getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(Integer loanTerm) {
        this.loanTerm = loanTerm;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Integer getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(Integer paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getLoanTermCode() {
        return loanTermCode;
    }

    public void setLoanTermCode(String loanTermCode) {
        this.loanTermCode = loanTermCode;
    }

    public Integer getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(Integer paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getPaymentTermCode() {
        return paymentTermCode;
    }

    public void setPaymentTermCode(String paymentTermCode) {
        this.paymentTermCode = paymentTermCode;
    }

    public void setMaturityDate(String maturityDate){
        try {
            this.maturityDate = DateUtil.convertToDate(maturityDate, "MM/dd/yyyy");
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse input date: " + maturityDate,e);
        }
    }
}
