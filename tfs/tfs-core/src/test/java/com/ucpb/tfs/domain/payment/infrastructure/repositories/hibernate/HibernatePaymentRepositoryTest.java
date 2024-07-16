package com.ucpb.tfs.domain.payment.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.*;

/**
 */
@TransactionConfiguration
@Transactional
@ContextConfiguration("classpath:repository/hibernate-context.xml")
public class HibernatePaymentRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {


    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HibernatePaymentRepository paymentRepository;


    @Test
    public void saveNewPaymentToTheDatabase(){
        Payment payment = new Payment();
        paymentRepository.saveOrUpdate(payment);
        sessionFactory.getCurrentSession().flush();

        assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM PAYMENT"));
    }

    @Test
    public void saveNewPaymentDetail(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");

        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setFacilityId(1);
        paymentDetail.setFacilityReferenceNumber("FACILITYREFNUM");
        paymentDetail.setFacilityType("FCN");
        paymentDetail.setLoanMaturityDate(new Date());
        paymentDetail.setPaymentCode(Integer.valueOf(1));
        paymentDetail.setPaymentTerm(Integer.valueOf(1));
        paymentDetail.setSequenceNumber(1212L);

        paymentRepository.saveOrUpdate(paymentDetail);
        sessionFactory.getCurrentSession().flush();

        Map<String,Object> result = jdbcTemplate.queryForMap("SELECT * FROM PAYMENTDETAIL");
        assertEquals("FACILITYREFNUM",result.get("FACILITYREFERENCENUMBER"));
        assertEquals("FCN",result.get("FACILITYTYPE"));

    }

    @Test
    public void getPaymentDetailById(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("INSERT INTO PAYMENTDETAIL (ID,PAYMENTINSTRUMENTTYPE) VALUES (134,'CASH')");

        PaymentDetail paymentDetail = paymentRepository.getPaymentDetail(134L);
        assertNotNull(paymentDetail);
    }

    @Test
    public void retrievePaymentUsingPaymentDetailId(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");

        jdbcTemplate.update("INSERT INTO PAYMENT (ID,TRADESERVICEID) VALUES (9,'tradeserviceId')");
        jdbcTemplate.update("INSERT INTO PAYMENTDETAIL (ID,PAYMENTINSTRUMENTTYPE,PAYMENTID,AMOUNT,STATUS) VALUES (134,'CASH',9,123,'UNPAID')");

        Payment payment = paymentRepository.getPaymentByPaymentDetailId(134L);
        assertNotNull(payment);
        PaymentDetail paymentDetail = payment.getPaymentDetail(134L);
        assertNotNull(paymentDetail);
        assertEquals(new BigDecimal("123"),paymentDetail.getAmount());
        assertEquals(PaymentStatus.UNPAID,paymentDetail.getStatus());
        payment.payItem(134L);
        assertEquals(PaymentStatus.PAID,paymentDetail.getStatus());
    }

    @Test
    public void getAllRelatedPayments(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");

        jdbcTemplate.update("INSERT INTO PAYMENT (ID,TRADESERVICEID,STATUS) VALUES (9,'tradeserviceId','PAID')");
        jdbcTemplate.update("INSERT INTO PAYMENT (ID,TRADESERVICEID,STATUS) VALUES (10,'tradeserviceId','UNPAID')");
        jdbcTemplate.update("INSERT INTO PAYMENT (ID,TRADESERVICEID) VALUES (11,'differentTradeService')");
        jdbcTemplate.update("INSERT INTO PAYMENT (ID,TRADESERVICEID) VALUES (12,'differentTradeService2')");

        List<Payment> payments = paymentRepository.getAllPayments(new TradeServiceId("tradeserviceId"));
        assertEquals(2,payments.size());
        assertEquals(PaymentStatus.PAID,payments.get(0).getStatus());
        assertEquals(PaymentStatus.UNPAID,payments.get(1).getStatus());

    }

    @Test
    public void returnEmptyListForNoPayments(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");
        List<Payment> payments = paymentRepository.getAllPayments(new TradeServiceId("tradeserviceId"));
        assertTrue(payments.isEmpty());
    }

    @Test
    public void updateExistingPaymentDetail(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");

        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setFacilityId(1);
        paymentDetail.setFacilityReferenceNumber("FACILITYREFNUM");
        paymentDetail.setFacilityType("FCN");
        paymentDetail.setLoanMaturityDate(new Date());
        paymentDetail.setPaymentCode(Integer.valueOf(1));
        paymentDetail.setPaymentTerm(Integer.valueOf(1));
        paymentDetail.setSequenceNumber(1212L);

        paymentRepository.saveOrUpdate(paymentDetail);
        sessionFactory.getCurrentSession().flush();

        Map<String,Object> result = jdbcTemplate.queryForMap("SELECT * FROM PAYMENTDETAIL");
        assertEquals("FACILITYREFNUM",result.get("FACILITYREFERENCENUMBER"));
        assertEquals("FCN",result.get("FACILITYTYPE"));
        assertNotNull(result.get("ID"));

        paymentDetail.setFacilityReferenceNumber("NEW REF NUMBER");
        paymentDetail.setFacilityType("GGG");

        paymentRepository.saveOrUpdate(paymentDetail);
        sessionFactory.getCurrentSession().flush();

        Map<String,Object> result2 = jdbcTemplate.queryForMap("SELECT * FROM PAYMENTDETAIL");
        assertEquals("NEW REF NUMBER",result2.get("FACILITYREFERENCENUMBER"));
        assertEquals("GGG",result2.get("FACILITYTYPE"));
    }

    @Test
    public void saveBothPaymentAndPaymentDetail(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");

        Payment payment = new Payment(new TradeServiceId("tradeServiceId"), ChargeType.PRODUCT);
        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setFacilityType("FCN");
        paymentDetail.setFacilityReferenceNumber("REFERENCE NUMBER");
        paymentDetail.setFacilityId(Integer.valueOf(1));
        paymentDetail.setPaymentCode(Integer.valueOf(4));
        paymentDetail.setPaymentTerm(Integer.valueOf(14));

        Set<PaymentDetail> paymentDetailSet = new HashSet<PaymentDetail>();
        paymentDetailSet.add(paymentDetail);

        payment.addNewPaymentDetails(paymentDetailSet);

        paymentRepository.saveOrUpdate(payment);
        sessionFactory.getCurrentSession().flush();

        Payment retrievedPayment = paymentRepository.get(new TradeServiceId("tradeServiceId"),ChargeType.PRODUCT);

        assertNotNull(retrievedPayment);
        assertNotNull(retrievedPayment.getDetails());
        assertEquals(1,retrievedPayment.getDetails().size());

    }

    @Test
    public void successfullyGetPaymentUsingPaymentId(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");

        Payment payment = new Payment(new TradeServiceId("tradeServiceId"), ChargeType.PRODUCT);
        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setFacilityType("FCN");
        paymentDetail.setFacilityReferenceNumber("REFERENCE NUMBER");
        paymentDetail.setFacilityId(Integer.valueOf(1));
        paymentDetail.setPaymentCode(Integer.valueOf(4));
        paymentDetail.setPaymentTerm(Integer.valueOf(14));

        Set<PaymentDetail> paymentDetailSet = new HashSet<PaymentDetail>();
        paymentDetailSet.add(paymentDetail);

        payment.addNewPaymentDetails(paymentDetailSet);

        paymentRepository.saveOrUpdate(payment);
        sessionFactory.getCurrentSession().flush();

        long paymentDetailId = jdbcTemplate.queryForLong("SELECT ID FROM PAYMENTDETAIL");

        assertFalse(paymentDetailId == 0);

        Payment queriedPayment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        assertNotNull(queriedPayment);
    }

    @Test
    public void returnNoPaymentOnInvalidPaymentDetailId(){
        jdbcTemplate.update("DELETE FROM PAYMENTDETAIL");
        jdbcTemplate.update("DELETE FROM PAYMENT");

        Payment payment = new Payment(new TradeServiceId("tradeServiceId"), ChargeType.PRODUCT);
        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setFacilityType("FCN");
        paymentDetail.setFacilityReferenceNumber("REFERENCE NUMBER");
        paymentDetail.setFacilityId(Integer.valueOf(1));
        paymentDetail.setPaymentCode(Integer.valueOf(4));
        paymentDetail.setPaymentTerm(Integer.valueOf(14));

        Set<PaymentDetail> paymentDetailSet = new HashSet<PaymentDetail>();
        paymentDetailSet.add(paymentDetail);

        payment.addNewPaymentDetails(paymentDetailSet);

        paymentRepository.saveOrUpdate(payment);
        sessionFactory.getCurrentSession().flush();

        Payment queriedPayment = paymentRepository.getPaymentByPaymentDetailId(249174812748912L);
        assertNull(queriedPayment);
    }

}
