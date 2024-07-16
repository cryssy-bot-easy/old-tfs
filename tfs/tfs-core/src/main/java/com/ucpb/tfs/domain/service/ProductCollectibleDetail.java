package com.ucpb.tfs.domain.service;

import com.ucpb.tfs.domain.reference.ChargeId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: IPCVal
 * Date: 7/29/13
 */
public class ProductCollectibleDetail implements Serializable {

    private String id;

    private TradeServiceId tradeServiceId;

    private String transactionType;

    private ChargeId chargeId;

    private BigDecimal amount;
    private BigDecimal originalAmount;

    // this is the default amount computed using the default charge formula
    private BigDecimal defaultAmount;

    private Currency currency;
    private Currency originalCurrency;

    private BigDecimal newSpecialRateThirdToUsd;
    private BigDecimal newSpecialRateThirdToPhp;
    private BigDecimal newSpecialRateUsdToPhp;
    private BigDecimal newUrr;

    private BigDecimal collectibleAmountInDefaultCurrency;
    private BigDecimal collectibleAmountInOriginalCurrency;   // In PHP
    private BigDecimal newCollectibleAmountInOriginalCurrency;

    public ProductCollectibleDetail() {
    }

    public ProductCollectibleDetail(
            TradeServiceId tradeServiceId,
            String transactionType,
            ChargeId chargeId,
            BigDecimal amount,
            BigDecimal originalAmount,
            BigDecimal defaultAmount,
            Currency currency,
            Currency originalCurrency,
            BigDecimal newSpecialRateThirdToUsd,
            BigDecimal newSpecialRateThirdToPhp,
            BigDecimal newSpecialRateUsdToPhp,
            BigDecimal newUrr,
            BigDecimal collectibleAmountInDefaultCurrency,
            BigDecimal collectibleAmountInOriginalCurrency,
            BigDecimal newCollectibleAmountInOriginalCurrency) {
        this.tradeServiceId = tradeServiceId;
        this.transactionType = transactionType;
        this.chargeId = chargeId;
        this.amount = amount;
        this.originalAmount = originalAmount;
        this.defaultAmount = defaultAmount;
        this.currency = currency;
        this.originalCurrency = originalCurrency;
        this.newSpecialRateThirdToUsd = newSpecialRateThirdToUsd;
        this.newSpecialRateThirdToPhp = newSpecialRateThirdToPhp;
        this.newSpecialRateUsdToPhp = newSpecialRateUsdToPhp;
        this.newUrr = newUrr;
        this.collectibleAmountInDefaultCurrency = collectibleAmountInDefaultCurrency;
        this.collectibleAmountInOriginalCurrency = collectibleAmountInOriginalCurrency;
        this.newCollectibleAmountInOriginalCurrency = newCollectibleAmountInOriginalCurrency;
    }

    public String getId() {
        return id;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public ChargeId getChargeId() {
        return chargeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Currency getOriginalCurrency() {
        return originalCurrency;
    }

    public BigDecimal getNewSpecialRateThirdToUsd() {
        return newSpecialRateThirdToUsd;
    }

    public BigDecimal getNewSpecialRateThirdToPhp() {
        return newSpecialRateThirdToPhp;
    }

    public BigDecimal getNewSpecialRateUsdToPhp() {
        return newSpecialRateUsdToPhp;
    }

    public BigDecimal getNewUrr() {
        return newUrr;
    }

    public BigDecimal getCollectibleAmountInDefaultCurrency() {
        return collectibleAmountInDefaultCurrency;
    }

    public BigDecimal getCollectibleAmountInOriginalCurrency() {
        return collectibleAmountInOriginalCurrency;
    }

    public BigDecimal getNewCollectibleAmountInOriginalCurrency() {
        return newCollectibleAmountInOriginalCurrency;
    }
}
