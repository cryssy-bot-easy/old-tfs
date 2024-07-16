package com.ucpb.tfs.domain.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: IPCVal
 * Date: 8/2/13
 */
public class OtherChargesDetail implements Serializable {

    private String id;
    private String transactionType;
    private String chargeType;
    private BigDecimal amount;
    private Currency currency;
    private String cwtFlag;

    public OtherChargesDetail() {
    }

    public OtherChargesDetail(
            String transactionType,
            String chargeType,
            BigDecimal amount,
            Currency currency,
            String cwtFlag) {
        this.transactionType = transactionType;
        this.chargeType = chargeType;
        this.currency = currency;
        this.cwtFlag = cwtFlag;

           if(this.cwtFlag!=null ){
               if(this.cwtFlag.equalsIgnoreCase("Y") || this.cwtFlag.equalsIgnoreCase("Yes") || this.cwtFlag.equalsIgnoreCase("1")  ) {
                   this.amount = amount.multiply(new BigDecimal("0.98"));
               } else {
                   this.amount = amount;
               }
           } else {
               this.amount = amount;
           }

    }

    public String getId() {
        return id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getChargeType() {
        return chargeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
    
    public String getCwtFlag() {
    	return cwtFlag;
    }
}
