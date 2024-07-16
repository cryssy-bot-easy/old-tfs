package com.ucpb.tfs.domain.payment.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.service.TradeServiceId;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 9/10/12
 */
public class PaymentItemPaymentReversedEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;
    private PaymentInstrumentType paymentInstrumentType;
    private String settlementAccountNumber;
    private String referenceNumber;
    private BigDecimal amount;
    private Currency currency;
    private Currency bookingCurrency;
    private BigDecimal interestRate;
    private String interestTerm;
    private String repricingTerm;
    private String repricingTermCode;
    private String loanTerm;
    private String loanTermCode;
    private Date loanMaturityDate;
    private Boolean isReversal;
    private TradeServiceId reversalTradeServiceId;

    private String referenceId;

    public PaymentItemPaymentReversedEvent() {}

    public PaymentItemPaymentReversedEvent(TradeServiceId tradeServiceId,
                                           PaymentInstrumentType paymentInstrumentType,
                                           String settlementAccountNumber,
                                           String referenceNumber,
                                           BigDecimal amount,
                                           Currency currency,
                                           Currency bookingCurrency,
                                           BigDecimal interestRate,
                                           String interestTerm,
                                           String repricingTerm,
                                           String repricingTermCode,
                                           String loanTerm,
                                           String loanTermCode,
                                           Date loanMaturityDate) {

        this.tradeServiceId = tradeServiceId;
        this.paymentInstrumentType = paymentInstrumentType;
        this.settlementAccountNumber = settlementAccountNumber;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.isReversal = false;
    }

    public PaymentItemPaymentReversedEvent(TradeServiceId tradeServiceId,
                                           PaymentInstrumentType paymentInstrumentType,
                                           String settlementAccountNumber,
                                           String referenceNumber,
                                           BigDecimal amount,
                                           Currency currency,
                                           Currency bookingCurrency,
                                           BigDecimal interestRate,
                                           String interestTerm,
                                           String repricingTerm,
                                           String repricingTermCode,
                                           String loanTerm,
                                           String loanTermCode,
                                           Date loanMaturityDate,
                                           String referenceId) {

        this.tradeServiceId = tradeServiceId;
        this.paymentInstrumentType = paymentInstrumentType;
        this.settlementAccountNumber = settlementAccountNumber;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.isReversal = false;

        this.referenceId = referenceId;
    }

    public PaymentItemPaymentReversedEvent(TradeServiceId tradeServiceId, PaymentDetail paymentDetail, TradeServiceId reversalTradeServiceId){

        this.tradeServiceId = tradeServiceId;
        this.paymentInstrumentType = paymentDetail.getPaymentInstrumentType();
        this.settlementAccountNumber = paymentDetail.getReferenceNumber();
        this.referenceNumber = paymentDetail.getReferenceNumber();
        this.amount = paymentDetail.getAmount();
        this.currency = paymentDetail.getCurrency();
        this.bookingCurrency = paymentDetail.getBookingCurrency();
        this.interestRate = paymentDetail.getInterestRate();
        this.interestTerm = paymentDetail.getInterestTerm();
        this.repricingTerm = paymentDetail.getRepricingTerm();
        this.repricingTermCode = paymentDetail.getRepricingTermCode();
        this.loanTerm = paymentDetail.getLoanTerm();
        this.loanTermCode = paymentDetail.getLoanTermCode();
        this.loanMaturityDate = paymentDetail.getLoanMaturityDate();
        this.referenceId = paymentDetail.getReferenceId();
        this.reversalTradeServiceId = reversalTradeServiceId;
        this.isReversal = reversalTradeServiceId != null;

    }

    public PaymentItemPaymentReversedEvent(TradeServiceId tradeServiceId, PaymentDetail paymentDetail, TradeServiceId reversalTradeServiceId, String settlementAccountNumber){

        this.tradeServiceId = tradeServiceId;
        this.paymentInstrumentType = paymentDetail.getPaymentInstrumentType();
        this.settlementAccountNumber = settlementAccountNumber; //paymentDetail.getReferenceNumber();
        this.referenceNumber = paymentDetail.getReferenceNumber();
        this.amount = paymentDetail.getAmount();
        this.currency = paymentDetail.getCurrency();
        this.bookingCurrency = paymentDetail.getBookingCurrency();
        this.interestRate = paymentDetail.getInterestRate();
        this.interestTerm = paymentDetail.getInterestTerm();
        this.repricingTerm = paymentDetail.getRepricingTerm();
        this.repricingTermCode = paymentDetail.getRepricingTermCode();
        this.loanTerm = paymentDetail.getLoanTerm();
        this.loanTermCode = paymentDetail.getLoanTermCode();
        this.loanMaturityDate = paymentDetail.getLoanMaturityDate();
        this.referenceId = paymentDetail.getReferenceId();
        this.reversalTradeServiceId = reversalTradeServiceId;
        this.isReversal = reversalTradeServiceId != null;

    }



    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public PaymentInstrumentType getPaymentInstrumentType() {
        return paymentInstrumentType;
    }

    public String getSettlementAccountNumber() {
        return settlementAccountNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Currency getBookingCurrency() {
        return bookingCurrency;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public String getInterestTerm() {
        return interestTerm;
    }

    public String getRepricingTerm() {
        return repricingTerm;
    }

    public String getRepricingTermCode() {
        return repricingTermCode;
    }

    public String getLoanTerm() {
        return loanTerm;
    }

    public String getLoanTermCode() {
        return loanTermCode;
    }

    public Date getLoanMaturityDate() {
        return loanMaturityDate;
    }

    public Boolean getReversal() {
        return isReversal;
    }

    public void setReversal(Boolean reversal) {
        isReversal = reversal;
    }

    public TradeServiceId getReversalTradeServiceId() {
        return reversalTradeServiceId;
    }

    public void setReversalTradeServiceId(TradeServiceId reversalTradeServiceId) {
        this.reversalTradeServiceId = reversalTradeServiceId;
    }

    public String getReferenceId() {
        return referenceId;
    }
}
