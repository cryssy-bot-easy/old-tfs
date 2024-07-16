package com.ucpb.tfs.application.commandHandler;

import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.infrastructure.repositories.hibernate.HibernatePaymentRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration("classpath:hibernate-repository-app-context.xml")
public class SaveLcPaymentCommandHandlerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {


    @Autowired
    @Qualifier("paymentRepository")
    private HibernatePaymentRepository paymentRepo;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    public void something(){
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) from PAYMENT"));
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) from PAYMENTDETAIL"));

        Payment payment = new Payment(new TradeServiceId("f284bf1a-20ce-48d1-92fb-02ca46c700cb"), ChargeType.PRODUCT);
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        PaymentDetail detail = new PaymentDetail(PaymentInstrumentType.IB_LOAN,
                "referenceNUmber", BigDecimal.TEN, Currency.getInstance("PHP"),Currency.getInstance("PHP"),BigDecimal.ONE,"M",
                "D","D","D","D","D",new Date(),Integer.valueOf("1"),
                BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,Integer.valueOf(1),"FTF","REFNUMBER");

        details.add(detail);
        payment.addNewPaymentDetails(details);
        paymentRepo.saveOrUpdate(payment);

        assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) from PAYMENTDETAIL"));

    }

    @Test
    public void saveNonIbLoan(){
        Payment payment = new Payment(new TradeServiceId("f284bf1a-20ce-48d1-92fb-02ca46c700cb"), ChargeType.PRODUCT);
        Set<PaymentDetail> details = new HashSet<PaymentDetail>();
        PaymentDetail detail = new PaymentDetail(PaymentInstrumentType.CASA,
                "referenceNUmber", BigDecimal.TEN, Currency.getInstance("PHP"),
                BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN,BigDecimal.TEN);

        details.add(detail);
        payment.addNewPaymentDetails(details);
        paymentRepo.saveOrUpdate(payment);
    }
}
