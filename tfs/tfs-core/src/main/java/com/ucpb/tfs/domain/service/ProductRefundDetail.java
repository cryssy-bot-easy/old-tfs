package com.ucpb.tfs.domain.service;

import com.ucpb.tfs.domain.reference.ChargeId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: IPCVal
 * Date: 7/29/13
 */
public class ProductRefundDetail implements Serializable {

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

    private BigDecimal refundAmountInDefaultCurrency;
    private BigDecimal refundAmountInOriginalCurrency;   // In PHP

    private BigDecimal newRefundAmountInOriginalCurrency;

    public ProductRefundDetail() {
    }

    public ProductRefundDetail(
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
            BigDecimal refundAmountInDefaultCurrency,
            BigDecimal refundAmountInOriginalCurrency,
            BigDecimal newRefundAmountInOriginalCurrency) {
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
        this.refundAmountInDefaultCurrency = refundAmountInDefaultCurrency;
        this.refundAmountInOriginalCurrency = refundAmountInOriginalCurrency;
        this.newRefundAmountInOriginalCurrency = newRefundAmountInOriginalCurrency;
    }
}
