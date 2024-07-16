package com.ucpb.tfs.domain.corresCharges;

import com.ucpb.tfs.domain.corresCharges.enumTypes.CorresChargeStatus;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.TradeServiceId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/30/13
 */
public class CorresChargeActual implements Serializable {

    private Long id;

    // TradeServiceId of the PAYMENT
    private TradeServiceId tradeServiceId;

    private DocumentNumber documentNumber;

    private CorresChargeStatus status;

    private BigDecimal amount;

    private Currency currency;

    private BigDecimal specialRateThirdToUsd;
    private BigDecimal specialRateUsdToPhp;
    private BigDecimal specialRateThirdToPhp;
    private BigDecimal specialRateUrr;

    private Date paidDate;

    private Date createdDate;

    private Mt202Details mt202Details;

    public CorresChargeActual() {
        this.createdDate = new Date();
        this.status = CorresChargeStatus.MARV;
    }

    public CorresChargeActual(DocumentNumber documentNumber,
                              TradeServiceId tradeServiceId,
                              BigDecimal amount,
                              Currency currency,
                              BigDecimal specialRateThirdToUsd,
                              BigDecimal specialRateUsdToPhp,
                              BigDecimal specialRateThirdToPhp,
                              BigDecimal specialRateUrr) {
        this();
        this.documentNumber = documentNumber;
        this.tradeServiceId = tradeServiceId;
        this.amount = amount;
        this.currency = currency;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public CorresChargeStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getSpecialRateThirdToUsd() {
        return specialRateThirdToUsd;
    }

    public BigDecimal getSpecialRateUsdToPhp() {
        return specialRateUsdToPhp;
    }

    public BigDecimal getSpecialRateThirdToPhp() {
        return specialRateThirdToPhp;
    }

    public BigDecimal getSpecialRateUrr() {
        return specialRateUrr;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setMt202Details(Map<String, Object> mt202Details) {
        this.mt202Details = new Mt202Details(mt202Details);
    }
}
