package com.ucpb.tfs.batch.util;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 */
public class MockObject implements Serializable {

    private String firstValue;

    private BigDecimal amount;


    public String getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(String firstValue) {
        this.firstValue = firstValue;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
