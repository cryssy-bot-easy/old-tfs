package com.ucpb.tfs.domain.service;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: IPCVal
 * Date: 7/30/13
 */
public class ServiceChargeRefundDetail implements Serializable {

    private BigDecimal refundAmountInDefaultCurrency;
    private BigDecimal refundAmountInOriginalCurrency;  // In PHP
    private BigDecimal newRefundAmountInOriginalCurrency;

    public ServiceChargeRefundDetail() {
    }

    public ServiceChargeRefundDetail(
            BigDecimal refundAmountInDefaultCurrency,
            BigDecimal refundAmountInOriginalCurrency,
            BigDecimal newRefundAmountInOriginalCurrency) {
        this.refundAmountInDefaultCurrency = refundAmountInDefaultCurrency;
        this.refundAmountInOriginalCurrency = refundAmountInOriginalCurrency;
        this.newRefundAmountInOriginalCurrency = newRefundAmountInOriginalCurrency;
    }

    public BigDecimal getRefundAmountInDefaultCurrency() {
        return refundAmountInDefaultCurrency;
    }

    public BigDecimal getRefundAmountInOriginalCurrency() {
        return refundAmountInOriginalCurrency;
    }

    public BigDecimal getNewRefundAmountInOriginalCurrency() {
        return newRefundAmountInOriginalCurrency;
    }
}
