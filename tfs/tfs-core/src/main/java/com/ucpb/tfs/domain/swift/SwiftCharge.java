package com.ucpb.tfs.domain.swift;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: Marv
 * Date: 11/28/12
 */

public class SwiftCharge implements Serializable {
    
    private String id;
    
    private String code;
    
    private String description;
    
    private Currency currency;
    
    private BigDecimal amount;
    
    public SwiftCharge(){}
    
    public SwiftCharge(String code, String description, Currency currency, BigDecimal amount) {
        this.code = code;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setId(String id) {
        this.id = id;
    }
}
