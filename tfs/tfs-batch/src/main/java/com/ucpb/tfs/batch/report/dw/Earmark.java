package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.Record;

import java.math.BigDecimal;

/**
 */
public class Earmark {

    private String SYSCOD;
    private BigDecimal TOSBAL;
    private BigDecimal TLOSBAL;
    private BigDecimal ORGLMT;
    private BigDecimal LORGAM;

    private String CURTYP;
    private String AFCPNO;
    private String CIFNO;
    private String ACCTNO;

    public String getSYSCOD() {
        return SYSCOD;
    }

    public void setSYSCOD(String SYSCOD) {
        this.SYSCOD = SYSCOD;
    }

    public BigDecimal getTOSBAL() {
        return TOSBAL;
    }

    public void setTOSBAL(BigDecimal TOSBAL) {
        this.TOSBAL = TOSBAL;
    }

    public BigDecimal getTLOSBAL() {
        return TLOSBAL;
    }

    public void setTLOSBAL(BigDecimal TLOSBAL) {
        this.TLOSBAL = TLOSBAL;
    }

    public String getCURTYP() {
        return CURTYP;
    }

    public void setCURTYP(String CURTYP) {
        this.CURTYP = CURTYP;
    }

    public String getAFCPNO() {
        return AFCPNO;
    }

    public void setAFCPNO(String AFCPNO) {
        this.AFCPNO = AFCPNO;
    }

    public String getCIFNO() {
        return CIFNO;
    }

    public void setCIFNO(String CIFNO) {
        this.CIFNO = CIFNO;
    }

    public String getACCTNO() {
        return ACCTNO;
    }

    public void setACCTNO(String ACCTNO) {
        this.ACCTNO = ACCTNO;
    }

    public BigDecimal getLORGAM() {
        return LORGAM;
    }

    public void setLORGAM(BigDecimal LORGAM) {
        this.LORGAM = LORGAM;
    }

    public BigDecimal getORGLMT() {
        return ORGLMT;
    }

    public void setORGLMT(BigDecimal ORGLMT) {
        this.ORGLMT = ORGLMT;
    }
}