package com.ucpb.tfs.util;

import java.math.BigDecimal;

/**
 */
public class TargetObject {

    private String stringTarget;

    private Integer integerTarget;

    private BigDecimal bigDecimalTarget;


    public String getStringTarget() {
        return stringTarget;
    }

    public void setStringTarget(String stringTarget) {
        this.stringTarget = stringTarget;
    }

    public Integer getIntegerTarget() {
        return integerTarget;
    }

    public void setIntegerTarget(Integer integerTarget) {
        this.integerTarget = integerTarget;
    }

    public BigDecimal getBigDecimalTarget() {
        return bigDecimalTarget;
    }

    public void setBigDecimalTarget(BigDecimal bigDecimalTarget) {
        this.bigDecimalTarget = bigDecimalTarget;
    }
}
