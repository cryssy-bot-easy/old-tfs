package com.ucpb.tfs.domain.payment.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 9/10/12
 */
public class PaymentItemPaidEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;
    private String settlementAccountNumber;
    private String referenceId;
    private PaymentDetail paymentDetail;

    public PaymentItemPaidEvent() {}


    public PaymentItemPaidEvent(TradeServiceId tradeServiceId,String settlementAccountNumber,PaymentDetail paymentDetail){
        this.tradeServiceId = tradeServiceId;
        this.settlementAccountNumber = settlementAccountNumber;
        this.paymentDetail = paymentDetail;
    }

    public PaymentItemPaidEvent(TradeServiceId tradeServiceId,String settlementAccountNumber,PaymentDetail paymentDetail,String referenceId){
        this(tradeServiceId,settlementAccountNumber,paymentDetail);
        this.referenceId = referenceId;
    }

    public PaymentItemPaidEvent(
            TradeServiceId tradeServiceId,
            PaymentInstrumentType paymentInstrumentType,
            String documentNumber,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            Currency bookingCurrency,
            BigDecimal interestRate,
            String interestTerm,
            String interestTermCode,
            String repricingTerm,
            String repricingTermCode,
            String loanTerm,
            String loanTermCode,
            Date loanMaturityDate
    ) {
        this.tradeServiceId = tradeServiceId;

        PaymentDetail paymentDetail = new PaymentDetail(
                paymentInstrumentType,
                referenceNumber,
                amount,
                currency,
                bookingCurrency,
                interestRate,
                interestTerm,
                interestTermCode,
                repricingTerm,
                repricingTermCode,
                loanTerm,
                loanTermCode,
                loanMaturityDate
        );

        this.paymentDetail = paymentDetail;
        this.settlementAccountNumber = documentNumber;
    }


    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public PaymentInstrumentType getPaymentInstrumentType() {
        return paymentDetail.getPaymentInstrumentType();
    }

    public String getSettlementAccountNumber() {
        return settlementAccountNumber;
    }

    public String getReferenceNumber() {
        return paymentDetail.getReferenceNumber();
    }

    public BigDecimal getAmount() {
        return paymentDetail.getAmount();
    }

    public Currency getCurrency() {
        return paymentDetail.getCurrency();
    }

    public Currency getBookingCurrency() {
        return paymentDetail.getBookingCurrency();
    }

    public BigDecimal getInterestRate() {
        return paymentDetail.getInterestRate();
    }

    public String getInterestTerm() {
        return paymentDetail.getInterestTerm();
    }

    public String getRepricingTerm() {
        return paymentDetail.getRepricingTerm();
    }

    public String getRepricingTermCode() {
        return paymentDetail.getRepricingTermCode();
    }

    public String getLoanTerm() {
        return paymentDetail.getLoanTerm();
    }

    public String getLoanTermCode() {
        return paymentDetail.getLoanTermCode();
    }

    public Date getLoanMaturityDate() {
        return paymentDetail.getLoanMaturityDate();
    }
    
    public String getReferenceId() {
        return referenceId;
    }

    public PaymentDetail getPaymentDetail() {
        return paymentDetail;
    }
}
