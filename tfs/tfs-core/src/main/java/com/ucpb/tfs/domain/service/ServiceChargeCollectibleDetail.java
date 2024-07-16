package com.ucpb.tfs.domain.service;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: IPCVal
 * Date: 7/30/13
 */
public class ServiceChargeCollectibleDetail implements Serializable {

    private BigDecimal collectibleAmountInDefaultCurrency;
    private BigDecimal collectibleAmountInOriginalCurrency;  // In PHP
    private BigDecimal newCollectibleAmountInOriginalCurrency;

    public ServiceChargeCollectibleDetail() {
    }

    public ServiceChargeCollectibleDetail(
            BigDecimal collectibleAmountInDefaultCurrency,
            BigDecimal collectibleAmountInOriginalCurrency,
            BigDecimal newCollectibleAmountInOriginalCurrency) {
        this.collectibleAmountInDefaultCurrency = collectibleAmountInDefaultCurrency;
        this.collectibleAmountInOriginalCurrency = collectibleAmountInOriginalCurrency;
        this.newCollectibleAmountInOriginalCurrency = newCollectibleAmountInOriginalCurrency;
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
