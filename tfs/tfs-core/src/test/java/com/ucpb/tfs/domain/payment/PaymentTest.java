package com.ucpb.tfs.domain.payment;

import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class PaymentTest {


    private Payment payment;


    @Test
    public void successfullyPay() throws Exception {
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        PaymentDetail detail1 = new PaymentDetail(PaymentInstrumentType.CASA,
                "referenceId",
                "referenceId",
                 new BigDecimal("10"),
                Currency.getInstance("PHP"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"));
        PaymentDetail detail2 =  new PaymentDetail(PaymentInstrumentType.MD,
                "referenceId",
                "referenceId",
                new BigDecimal("10"),
                Currency.getInstance("PHP"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("10"));

        details.add(detail1);
        details.add(detail2);

        Payment payment = new Payment();
        payment.addNewPaymentDetails(details);

        assertEquals(PaymentStatus.UNPAID,payment.getStatus());
        payment.payItem(PaymentInstrumentType.CASA,"casaRef","referenceId");
        payment.payItem(PaymentInstrumentType.MD,"casaRef","referenceId");


        assertEquals(PaymentStatus.PAID, payment.getStatus());
    }

    @Test
    public void successfullyReverseExistingPaymentDetail(){
        Payment payment = new Payment();
        PaymentDetail detail = new PaymentDetail();
        detail.setId(1L);
        detail.paid();

        Set<PaymentDetail> payments = new HashSet<PaymentDetail>();
        payments.add(detail);

        payment.addNewPaymentDetails(payments);
        assertTrue(payment.reverseItemPayment(1L));
    }

    @Test
    public void successfullyReverseAlreadyUnpaidPayment(){
        Payment payment = new Payment();
        PaymentDetail detail = new PaymentDetail();
        detail.setId(1L);
        detail.unPay();

        Set<PaymentDetail> payments = new HashSet<PaymentDetail>();
        payments.add(detail);

        payment.addNewPaymentDetails(payments);
        assertTrue(payment.reverseItemPayment(1L));
    }

    @Test
    public void failToReverseExistingItemPayments(){
        Payment payment = new Payment();
        PaymentDetail detail = new PaymentDetail();
        detail.setId(1L);
        detail.unPay();

        Set<PaymentDetail> payments = new HashSet<PaymentDetail>();
        payments.add(detail);

        payment.addNewPaymentDetails(payments);
        assertFalse(payment.reverseItemPayment(4L));
    }




}
