package com.ucpb.tfs.util;

import java.math.BigDecimal;

/**
 */
public class SourceObject {

    private String string;

    private Integer integer;

    private BigDecimal bigDecimal;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }
}
