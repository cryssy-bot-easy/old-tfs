package com.ucpb.tfs.domain.payment;

import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Jett
 * Date: 7/19/12
 */
public interface PaymentRepository {

    public Payment get(TradeServiceId serviceId, ChargeType chargeType);

    public List<Payment> getAllPayments(TradeServiceId tradeServiceId);

    public PaymentDetail getPaymentDetail(Long id);

    public Payment getPaymentByPaymentDetailId(Long paymentDetailId);

    public void saveOrUpdate(Payment payment);

    public void saveOrUpdate(PaymentDetail paymentDetail);

    public void delete(Payment payment);

    // for UA Loan Maturity Adjustment : loads Payment
    public Payment load(TradeServiceId tradeServiceId);

    public List loadAllPayment(TradeServiceId tradeServiceId);

    public List<Payment> getPaymentBy(TradeServiceId tradeServiceId);

    public void merge(Payment payment);

    // loads ua loan payment
    public PaymentDetail getUaLoanPayment(String referenceNumber);

    public List<ChargeType> getAllPaymentChargeTypesPerTradeService(TradeServiceId tradeServiceId);

    public Boolean checkIfHasPaidPayment(TradeServiceId tradeServiceId);

    public Map<String, Object> getServiceChargeRates(TradeServiceId tradeServiceId);

    public Set<PaymentDetail> getSavedProductPayments(TradeServiceId tradeServiceId);
    
    public Boolean checkIfHasUnpaidPayment(TradeServiceId tradeServiceId);
    
    public Map<String, Object> getCDTPaymentDetails(String iedieirdNo);
}

