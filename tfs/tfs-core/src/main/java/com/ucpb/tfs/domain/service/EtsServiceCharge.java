package com.ucpb.tfs.domain.service;

import com.incuventure.ddd.domain.annotations.DomainEntity;
import com.ucpb.tfs.domain.reference.ChargeId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: Giancarlo Angulo
 * Date: 01/07/14
 * @author Giancarlo Angulo
 */
@DomainEntity
public class EtsServiceCharge implements Serializable {

    private String id;

    private TradeServiceId tradeServiceId;

    private ChargeId chargeId;

    private BigDecimal amount;
    private BigDecimal originalAmount;

    // this is the default amount computed using the default charge formula
    private BigDecimal defaultAmount;
    private BigDecimal defaultPesoAmount;
    private BigDecimal nocwtAmount;

    private Currency currency;
    private Currency originalCurrency;

    private String overridenFlag;

//    // Shared by Refund and Collectible (a.k.a. Payment of Other Import Charges)
//    private String transactionType;
//    private BigDecimal newSpecialRateThirdToUsd;
//    private BigDecimal newSpecialRateThirdToPhp;
//    private BigDecimal newSpecialRateUsdToPhp;
//    private BigDecimal newUrr;


    public ChargeId getChargeId() {
        return chargeId;
    }

    public EtsServiceCharge() {
    }

    public EtsServiceCharge(TradeServiceId tradeServiceId, ChargeId chargeId, BigDecimal amount, Currency currency) {
        this.tradeServiceId = tradeServiceId;
        this.chargeId = chargeId;
        this.amount = amount;
        this.defaultAmount = amount;
        this.currency = currency;
        this.originalCurrency = currency;
        this.originalAmount = amount;
        this.overridenFlag = "N";
    }

    public EtsServiceCharge(TradeServiceId tradeServiceId, ChargeId chargeId, BigDecimal amount, Currency currency, BigDecimal originalAmount, Currency originalCurrency) {
        this.tradeServiceId = tradeServiceId;
        this.chargeId = chargeId;
        this.amount = amount;
        this.defaultAmount = amount;
        this.currency = currency;
        this.originalAmount = originalAmount;
        this.originalCurrency = originalCurrency;
        this.overridenFlag = "N";
    }

    public EtsServiceCharge(TradeServiceId tradeServiceId, ChargeId chargeId, BigDecimal amount, Currency currency, BigDecimal originalAmount, Currency originalCurrency, BigDecimal defaultAmount, String overriddenFlag, BigDecimal nocwtAmount) {
        this.tradeServiceId = tradeServiceId;
        this.chargeId = chargeId;
        this.amount = amount;
        this.defaultAmount = defaultAmount;
        this.currency = currency;
        this.originalAmount = originalAmount;
        this.originalCurrency = originalCurrency;
        this.overridenFlag = overriddenFlag;
        this.nocwtAmount = nocwtAmount;
    }

    public EtsServiceCharge(ServiceCharge serviceCharge) {
        this.tradeServiceId = serviceCharge.getTradeServiceId();
        this.chargeId = serviceCharge.getChargeId();
        this.amount = serviceCharge.getAmount();
        this.defaultAmount = serviceCharge.getDefaultAmount();
        this.currency = serviceCharge.getCurrency();
        this.originalAmount = serviceCharge.getOriginalAmount();
        this.originalCurrency = serviceCharge.getOriginalCurrency();
        this.overridenFlag = serviceCharge.getOverridenFlag();
        this.nocwtAmount = serviceCharge.getNocwtAmount();
    }


    public Boolean matches(ChargeId chargeId) {
        return this.chargeId.toString().equalsIgnoreCase(chargeId.toString()) ;
    }

    public void update(BigDecimal amount, Currency currency) {

        if(this.amount.compareTo(amount)!=0 ) {
            this.amount = amount;
        }

        // if currency changed (this should never happen), we re-set the default amount
        if(this.currency != currency) {
            this.defaultAmount = amount;
            this.currency = currency;
        }
    }

    public void update(BigDecimal amount, Currency currency, BigDecimal originalAmount, Currency originalCurrency) {

        if(this.amount != amount) {
            this.amount = amount;
        }

        if(this.originalAmount != originalAmount){
            this.originalAmount = originalAmount;
        }

        // if currency changed (this should never happen), we re-set the default amount
        if(this.currency != currency) {
            this.defaultAmount = amount;
            this.currency = currency;
        }

        if(this.originalCurrency != originalCurrency) {
            this.originalCurrency = originalCurrency;
        }
    }

    //This is peso by default
    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }


    public Currency getCurrency() {
        return currency;
    }

    public Currency getOriginalCurrency(){
        return originalCurrency;
    }

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public EtsServiceCharge duplicateServiceCharge() {
        EtsServiceCharge serviceCharge = new EtsServiceCharge();

        serviceCharge.chargeId = this.getChargeId();
        serviceCharge.amount = this.getAmount();
        serviceCharge.defaultAmount = this.getDefaultAmount();
        serviceCharge.originalAmount = this.getOriginalAmount();
        serviceCharge.currency = this.getCurrency();
        serviceCharge.originalCurrency = this.getOriginalCurrency();
        serviceCharge.overridenFlag = this.getOverridenFlag();


        return serviceCharge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public BigDecimal getDefaultPesoAmount() {
        return defaultPesoAmount;
    }

    public void setDefaultPesoAmount(BigDecimal defaultPesoAmount) {
        this.defaultPesoAmount = defaultPesoAmount;
    }

    public String getOverridenFlag() {
        return overridenFlag;
    }

    public void setOverridenFlag(String overridenFlag) {
        this.overridenFlag = overridenFlag;
    }

    public BigDecimal getNocwtAmount() {
        return nocwtAmount;
    }

    public void setNocwtAmount(BigDecimal nocwtAmount) {
        this.nocwtAmount = nocwtAmount;
    }
}
