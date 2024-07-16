package com.ucpb.tfs.domain.accounting;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: giancarlo
 * Date: 9/29/12
 * Time: 3:27 PM
 */
public class ProfitLossHolder implements Serializable {

    private long id;

    String tradeServiceId;
    String paymentDetailId;
    BigDecimal paymentAmount;
    BigDecimal paymentAmountOrig;
    BigDecimal profitLossTotal;
    BigDecimal profitUrrToPassOn;
    BigDecimal profitPassOnToSpecial;
    BigDecimal profitLossOneCent;
    BigDecimal profitLossOtherCent;

//    BigDecimal passOnRateThirdToUsd;
//    BigDecimal passOnRateUsdToPhp;
//    BigDecimal specialRateUsdToPhp;
//    BigDecimal urr;


    public ProfitLossHolder() {
    }

    public ProfitLossHolder(
            String tradeServiceId,
            String paymentDetailId,
            BigDecimal paymentAmount,
            BigDecimal paymentAmountOrig,
            BigDecimal profitLossTotal,
            BigDecimal profitUrrToPassOn,
            BigDecimal profitPassOnToSpecial,
            BigDecimal profitLossOneCent,
            BigDecimal profitLossOtherCent
            ) {

        this.tradeServiceId = tradeServiceId;
        this.paymentDetailId = paymentDetailId;
        this.paymentAmount = paymentAmount;
        this.paymentAmountOrig = paymentAmountOrig;
        this.profitLossTotal = profitLossTotal;
        this.profitUrrToPassOn = profitUrrToPassOn;
        this.profitPassOnToSpecial = profitPassOnToSpecial;
        this.profitLossOneCent = profitLossOneCent;
        this.profitLossOtherCent = profitLossOtherCent;
//        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
//        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
//        this.specialRateUsdToPhp = specialRateUsdToPhp;
//        this.urr = urr;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(String tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public String getPaymentDetailId() {
        return paymentDetailId;
    }

    public void setPaymentDetailId(String paymentDetailId) {
        this.paymentDetailId = paymentDetailId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getProfitLossTotal() {
        return profitLossTotal;
    }

    public void setProfitLossTotal(BigDecimal profitLossTotal) {
        this.profitLossTotal = profitLossTotal;
    }

    public BigDecimal getProfitUrrToPassOn() {
        return profitUrrToPassOn;
    }

    public void setProfitUrrToPassOn(BigDecimal profitUrrToPassOn) {
        this.profitUrrToPassOn = profitUrrToPassOn;
    }

    public BigDecimal getProfitPassOnToSpecial() {
        return profitPassOnToSpecial;
    }

    public void setProfitPassOnToSpecial(BigDecimal profitPassOnToSpecial) {
        this.profitPassOnToSpecial = profitPassOnToSpecial;
    }

    public BigDecimal getProfitLossOneCent() {
        return profitLossOneCent;
    }

    public void setProfitLossOneCent(BigDecimal profitLossOneCent) {
        this.profitLossOneCent = profitLossOneCent;
    }

    public BigDecimal getProfitLossOtherCent() {
        return profitLossOtherCent;
    }

    public void setProfitLossOtherCent(BigDecimal profitLossOtherCent) {
        this.profitLossOtherCent = profitLossOtherCent;
    }

//    public BigDecimal getPassOnRateThirdToUsd() {
//        return passOnRateThirdToUsd;
//    }
//
//    public void setPassOnRateThirdToUsd(BigDecimal passOnRateThirdToUsd) {
//        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
//    }
//
//    public BigDecimal getPassOnRateUsdToPhp() {
//        return passOnRateUsdToPhp;
//    }
//
//    public void setPassOnRateUsdToPhp(BigDecimal passOnRateUsdToPhp) {
//        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
//    }
//
//    public BigDecimal getSpecialRateUsdToPhp() {
//        return specialRateUsdToPhp;
//    }
//
//    public void setSpecialRateUsdToPhp(BigDecimal specialRateUsdToPhp) {
//        this.specialRateUsdToPhp = specialRateUsdToPhp;
//    }
//
//    public BigDecimal getUrr() {
//        return urr;
//    }
//
//    public void setUrr(BigDecimal urr) {
//        this.urr = urr;
//    }
}
