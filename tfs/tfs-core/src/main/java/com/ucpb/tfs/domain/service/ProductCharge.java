package com.ucpb.tfs.domain.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: Jett
 * Date: 7/12/12
 * @author Jett Gamboa
 */
public class ProductCharge implements Serializable {

    private BigDecimal amount;

    // this is the amount before it is overridden
    // todo: validate if product charge can be overridden (most likely not)
    private BigDecimal defaultAmount;

    private Currency currency;

    // pass on rate
    private BigDecimal productPassOnRateThirdToUsd;
    private BigDecimal productPassOnRateUsdToPhp;
    private BigDecimal productPassOnRateThirdToPhp;
    private BigDecimal productPassOnRateUrr;

    // special rates
    private BigDecimal productSpecialRateThirdToUsd;
    private BigDecimal productSpecialRateUsdToPhp;
    private BigDecimal productSpecialRateThirdToPhp;
    private BigDecimal productSpecialRateUrr;

    public ProductCharge() {};

    public ProductCharge(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
        this.defaultAmount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
