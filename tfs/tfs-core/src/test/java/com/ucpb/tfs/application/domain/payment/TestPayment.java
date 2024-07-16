package com.ucpb.tfs.application.domain.payment;

import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * User: IPCVal
 * Date: 8/24/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestPayment {

    @Inject
    private PaymentRepository paymentRepository;

    @Test
    public void TestAddAndUpdateProductPayment() throws Exception {

        TradeServiceId tradeServiceId = new TradeServiceId("xxx-yyy-zzz");

        Payment payment = new Payment(tradeServiceId, ChargeType.PRODUCT);
        payment.addOrUpdateItem(PaymentInstrumentType.CASA, "10-001-000100-2", new BigDecimal("100.00"), Currency.getInstance("PHP"), null, null, null, null, null, null, null,"name");
        payment.addOrUpdateItem(PaymentInstrumentType.MD, "MD-SettlementAcctNum-1", new BigDecimal("200.00"), Currency.getInstance("PHP"), null, null, null, null, null, null, null,"name");
        payment.addOrUpdateItem(PaymentInstrumentType.AP, "AP-SettlementAcctNum-1", new BigDecimal("300.00"), Currency.getInstance("PHP"),  null, null, null, null, null, null, null,"name");

        paymentRepository.saveOrUpdate(payment);

        Payment persistedPayment = paymentRepository.get(tradeServiceId, ChargeType.PRODUCT);

        if (persistedPayment != null) {
            persistedPayment.addOrUpdateItem(PaymentInstrumentType.CASA, "10-001-000100-2", new BigDecimal("400.00"), Currency.getInstance("PHP"), null, null, null, null, null, null, null,"name");
            persistedPayment.addOrUpdateItem(PaymentInstrumentType.CHECK, "CA-08-24-2012-1", new BigDecimal("500.00"), Currency.getInstance("PHP"), null, null, null, null, null, null, null,"name");
            paymentRepository.saveOrUpdate(persistedPayment);
        }
    }

    @Test
    public void TestAddAndUpdateServiceChargesPayment() throws Exception {

        TradeServiceId tradeServiceId = new TradeServiceId("xxx-yyy-zzz");

        Payment payment = new Payment(tradeServiceId, ChargeType.SERVICE);
        payment.addOrUpdateItem(PaymentInstrumentType.CASA, "10-001-000100-3", new BigDecimal("600.00"), Currency.getInstance("PHP"), null, null, null, null, null, null, null,"name");
        payment.addOrUpdateItem(PaymentInstrumentType.MD, "MD-SettlementAcctNum-2", new BigDecimal("700.00"), Currency.getInstance("PHP"), null, null, null,  null, null, null, null,"name");
        payment.addOrUpdateItem(PaymentInstrumentType.AP, "AP-SettlementAcctNum-2", new BigDecimal("800.00"), Currency.getInstance("PHP"), null, null, null, null, null, null, null,"name");

        paymentRepository.saveOrUpdate(payment);

        Payment persistedPayment = paymentRepository.get(tradeServiceId, ChargeType.SERVICE);

        if (persistedPayment != null) {
            persistedPayment.addOrUpdateItem(PaymentInstrumentType.MD, "MD-SettlementAcctNum-2", new BigDecimal("900.00"), Currency.getInstance("PHP"), null, null, null, null, null, null, null,"name");
            paymentRepository.saveOrUpdate(persistedPayment);
        }
    }
}
