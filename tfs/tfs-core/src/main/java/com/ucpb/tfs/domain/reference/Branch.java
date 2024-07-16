package com.ucpb.tfs.domain.reference;

import java.math.BigDecimal;

/**
 */
public class Branch {

    private Long id;

    private String unitCode;

    private String bocCode;

    private BigDecimal casaCreditLimit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBocCode() {
        return bocCode;
    }

    public void setBocCode(String bocCode) {
        this.bocCode = bocCode;
    }

    public BigDecimal getCasaCreditLimit() {
        return casaCreditLimit;
    }

    public void setCasaCreditLimit(BigDecimal casaCreditLimit) {
        this.casaCreditLimit = casaCreditLimit;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
}
