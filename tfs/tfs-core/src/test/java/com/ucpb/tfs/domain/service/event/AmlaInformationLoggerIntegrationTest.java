package com.ucpb.tfs.domain.service.event;

import com.ucpb.tfs.application.service.ConversionService;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.event.LCNegotiationCreatedEvent;
import com.ucpb.tfs.domain.product.event.LetterOfCreditCreatedEvent;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.interfaces.services.CustomerInformationFileService;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 */

@TransactionConfiguration
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:transactionlog-app-context.xml")
public class AmlaInformationLoggerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @MockitoAnnotations.Mock
    private CustomerInformationFileService customerInformationFileService;

    @Autowired
    private AmlaInformationLogger amlaInformationLogger;

    @MockitoAnnotations.Mock
    private ConversionService conversionService;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Before
    public void overrideConversionService() {
        MockitoAnnotations.initMocks(this);
        assertNotNull(amlaInformationLogger);
        amlaInformationLogger.setConversionService(conversionService);
        when(conversionService.getPhpConversionRate("USD")).thenReturn(new BigDecimal("41.490002"));
    }

    @Before
    public void setCifService(){
        MockitoAnnotations.initMocks(this);
        assertNotNull(amlaInformationLogger);
        when(customerInformationFileService.getBirthdayByCifNumber(anyString())).thenReturn(new Date());
        amlaInformationLogger.setCustomerInformationFileService(customerInformationFileService);
    }

    @Before
    public void setup(){
        amlaInformationLogger.setEnabled(true);
        jdbcTemplate.execute("DELETE FROM TRANSACTIONLOG");
    }

    @Test
    public void successfullySaveTransactionTypeCodeAsILCam(){
        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");
        details.put("documentSubType1","STANDBY");
        details.put("accountNumber","cifNumber");
        details.put("currency","PHP");
        details.put("negotiationCurrency","PHP");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("exporterName","Exporter Name");
        details.put("exporterAddress","Exporter Address");
        details.put("amount","12.25");
        details.put("cifNumber","1213414");
        details.put("documentType","DOMESTIC");

//        details.put("currentRate",new BigDecimal("43.0002"));
        TradeService service = new TradeService();
        service.setDetails(details);

        LetterOfCredit lc = new LetterOfCredit();
        LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(service,lc,"");
        amlaInformationLogger.logLcNegotiationEvent(event);

        sessionFactory.getCurrentSession().flush();
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG "));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");

        assertEquals("ILCAM",log.get("TRANSACTIONTYPECODE"));
    }

    @Test
    public void successfullySaveTransactionTypeCodeAsILCAC(){
        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");
        details.put("documentSubType1","CASH");
        details.put("accountNumber","cifNumber");
        details.put("currency","PHP");
        details.put("negotiationCurrency","PHP");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("exporterName","Exporter Name");
        details.put("exporterAddress","Exporter Address");
        details.put("amount","12.25");
        details.put("cifNumber","1213414");
        details.put("documentType","FOREIGN");

//        details.put("currentRate",new BigDecimal("43.0002"));
        TradeService service = new TradeService();
        service.setDetails(details);

        LetterOfCredit lc = new LetterOfCredit();
        LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(service,lc,"");
        amlaInformationLogger.logLcNegotiationEvent(event);

        sessionFactory.getCurrentSession().flush();
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG "));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");

        assertEquals("ILCAC",log.get("TRANSACTIONTYPECODE"));
    }

    @Test
    public void successfullySaveTransactionTypeCodeAsILCAD(){
        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");
        details.put("documentSubType1","STANDBY");
        details.put("accountNumber","cifNumber");
        details.put("currency","PHP");
        details.put("negotiationCurrency","PHP");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("exporterName","Exporter Name");
        details.put("exporterAddress","Exporter Address");
        details.put("amount","12.25");
        details.put("cifNumber","1213414");
        details.put("documentType","FOREIGN");

//        details.put("currentRate",new BigDecimal("43.0002"));
        TradeService service = new TradeService();
        service.setDetails(details);

        LetterOfCredit lc = new LetterOfCredit();
        LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(service,lc,"");
        amlaInformationLogger.logLcNegotiationEvent(event);

        sessionFactory.getCurrentSession().flush();
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG "));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");

        assertEquals("ILCAD",log.get("TRANSACTIONTYPECODE"));
    }

    @Test
    public void successfullySaveLCCreatedEventForPHPLC(){
        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");
        details.put("documentSubType1","STANDBY");
        details.put("accountNumber","cifNumber");
        details.put("currency","PHP");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("exporterName","Exporter Name");
        details.put("exporterAddress","Exporter Address");
        details.put("amount","12.25");
        details.put("cifNumber","1213414");
//        details.put("currentRate",new BigDecimal("43.0002"));
        TradeService service = new TradeService();
        service.setDetails(details);

        jdbcTemplate.execute("INSERT INTO PAYMENT (ID,TRADESERVICEID,CHARGETYPE,STATUS) VALUES (1, '" + service.getTradeServiceId().toString() + "','PRODUCT','UNPAID')");
        jdbcTemplate.execute("INSERT INTO PAYMENTDETAIL (ID,PAYMENTINSTRUMENTTYPE,REFERENCENUMBER,AMOUNT,CURRENCY,STATUS,BOOKINGCURRENCY,INTERESTRATE,INTERESTTERM,REPRICINGTERM,REPRICINGTERMCODE,LOANTERM,LOANTERMCODE,LOANMATURITYDATE,REFERENCEID,PAYMENTID) VALUES (1,'CASA','s',0,'PHP','UNPAID','PHP',0,'s','s','s','s','s',CURRENT_TIMESTAMP,'s',1)");

        LetterOfCredit lc = new LetterOfCredit();
        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(service,lc,"");
        amlaInformationLogger.logLcCreatedEvent(event);

        sessionFactory.getCurrentSession().flush();

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG "));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");
//        assertNotNull(log.get("TXNDATE"));
        assertNotNull(log.get("TXNREFERENCENUMBER"));
        assertEquals("NA",log.get("DEALNUMBER"));
        assertEquals("NA",log.get("TRANSACTIONSUBTYPE"));
        assertEquals("TFSS1234567",log.get("ACCOUNTNUMBER"));

//        assertEquals("C",log.get("DEBIT_CREDIT"));
        assertEquals("Exporter Name",log.get("BENEFICIARYNAME1"));
        assertEquals("Exporter Address",log.get("BENEFICIARYADDR1"));
        assertEquals("OUTGOING",log.get("DIRECTION"));
        assertEquals("TFSS1",log.get("PRODUCTTYPE"));
        assertEquals("LCOPN",log.get("TRANSACTIONCODE"));
        assertEquals("CASA",log.get("PAYMENTMODE"));
        assertNotNull(log.get("INPUTDATE"));
        assertEquals(new BigDecimal("12.25"),log.get("TRANSACTIONAMOUNT"));
    }

    @Test
    public void successfullySaveLCCreatedEventForUSDLC(){
        //exchange rate is 41.490002
        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");
        details.put("documentSubType1","STANDBY");
        details.put("accountNumber","cifNumber");
        details.put("currency","USD");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("exporterName","Exporter Name");
        details.put("exporterAddress","Exporter Address");
        details.put("amount","12.25");
//        details.put("currentRate",new BigDecimal("43.0002"));

        TradeService service = new TradeService();
        service.setDetails(details);

        jdbcTemplate.execute("INSERT INTO PAYMENT (ID,TRADESERVICEID,CHARGETYPE,STATUS) VALUES (1, '" + service.getTradeServiceId().toString() + "','PRODUCT','UNPAID')");
        jdbcTemplate.execute("INSERT INTO PAYMENTDETAIL (ID,PAYMENTINSTRUMENTTYPE,REFERENCENUMBER,AMOUNT,CURRENCY,STATUS,BOOKINGCURRENCY,INTERESTRATE,INTERESTTERM,REPRICINGTERM,REPRICINGTERMCODE,LOANTERM,LOANTERMCODE,LOANMATURITYDATE,REFERENCEID,PAYMENTID) VALUES (1,'CASA','s',0,'PHP','UNPAID','PHP',0,'s','s','s','s','s',CURRENT_TIMESTAMP,'s',1)");

        LetterOfCredit lc = new LetterOfCredit();
        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(service,lc,"");
        amlaInformationLogger.logLcCreatedEvent(event);

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG "));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");
        assertNotNull(log.get("TXNDATE"));
        assertNotNull(log.get("TXNREFERENCENUMBER"));
        assertEquals("NA",log.get("DEALNUMBER"));
        assertEquals("NA",log.get("TRANSACTIONSUBTYPE"));
        assertEquals("TFSS1234567",log.get("ACCOUNTNUMBER"));

//        assertEquals("C",log.get("DEBIT_CREDIT"));
        assertEquals("Exporter Name",log.get("BENEFICIARYNAME1"));
        assertEquals("Exporter Address",log.get("BENEFICIARYADDR1"));
        assertEquals("OUTGOING",log.get("DIRECTION"));
        assertEquals("TFSS1",log.get("PRODUCTTYPE"));
        assertEquals("LCOPN",log.get("TRANSACTIONCODE"));
        assertEquals("CASA",log.get("PAYMENTMODE"));
        assertNotNull(log.get("INPUTDATE"));
        assertEquals(new BigDecimal("508.25"),log.get("TRANSACTIONAMOUNT"));
        assertEquals(new BigDecimal("41.4900"),log.get("EXCHANGERATE"));

    }

    @Test
    public void successfullyLogLCNegotiationDetailsForPHPNego(){
        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("processDate","121212");
        details.put("documentSubType1","STANDBY");
        details.put("accountNumber","cifNumber");
        details.put("negotiationCurrency","PHP");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("negotiationAmount","123");
        details.put("amount","12.25");
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");

        TradeService service = new TradeService();
        service.setDetails(details);

        jdbcTemplate.execute("INSERT INTO PAYMENT (ID,TRADESERVICEID,CHARGETYPE,STATUS) VALUES (1, '" + service.getTradeServiceId().toString() + "','PRODUCT','UNPAID')");
        jdbcTemplate.execute("INSERT INTO PAYMENTDETAIL (ID,PAYMENTINSTRUMENTTYPE,REFERENCENUMBER,AMOUNT,CURRENCY,STATUS,BOOKINGCURRENCY,INTERESTRATE,INTERESTTERM,REPRICINGTERM,REPRICINGTERMCODE,LOANTERM,LOANTERMCODE,LOANMATURITYDATE,REFERENCEID,PAYMENTID) VALUES (1,'CASA','s',0,'PHP','UNPAID','PHP',0,'s','s','s','s','s',CURRENT_TIMESTAMP,'s',1)");

        LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(service,null,"");
        amlaInformationLogger.logLcNegotiationEvent(event);

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");
        assertNotNull(log.get("TXNDATE"));
        assertNotNull(log.get("TXNREFERENCENUMBER"));
        assertEquals("NA",log.get("DEALNUMBER"));
        assertEquals("NA",log.get("TRANSACTIONSUBTYPE"));
        assertEquals("TFSS1234567",log.get("ACCOUNTNUMBER"));

//        assertEquals("C",log.get("DEBIT_CREDIT"));
        assertEquals("OUTGOING",log.get("DIRECTION"));
        assertEquals("TFSS1",log.get("PRODUCTTYPE"));
        assertEquals("LCOPN",log.get("TRANSACTIONCODE"));
        assertEquals("CASA",log.get("PAYMENTMODE"));
        assertEquals("Beneficiary Name",log.get("BENEFICIARYNAME1"));
        assertEquals("123 Fake Street",log.get("BENEFICIARYADDR1"));


    }

    @Test
    public void logUsdLcNegotiation(){

        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("processDate","121212");
        details.put("documentSubType1","STANDBY");
        details.put("accountNumber","cifNumber");
        details.put("negotiationCurrency","USD");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("negotiationAmount","123");
        details.put("amount","12.25");
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");

        TradeService service = new TradeService();
        service.setDetails(details);

        jdbcTemplate.execute("INSERT INTO PAYMENT (ID,TRADESERVICEID,CHARGETYPE,STATUS) VALUES (1, '" + service.getTradeServiceId().toString() + "','PRODUCT','UNPAID')");
        jdbcTemplate.execute("INSERT INTO PAYMENTDETAIL (ID,PAYMENTINSTRUMENTTYPE,REFERENCENUMBER,AMOUNT,CURRENCY,STATUS,BOOKINGCURRENCY,INTERESTRATE,INTERESTTERM,REPRICINGTERM,REPRICINGTERMCODE,LOANTERM,LOANTERMCODE,LOANMATURITYDATE,REFERENCEID,PAYMENTID) VALUES (1,'CASA','s',0,'PHP','UNPAID','PHP',0,'s','s','s','s','s',CURRENT_TIMESTAMP,'s',1)");

        LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(service,null,"");
        amlaInformationLogger.logLcNegotiationEvent(event);

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");
        assertNotNull(log.get("TXNDATE"));
        assertNotNull(log.get("TXNREFERENCENUMBER"));
        assertEquals("NA",log.get("DEALNUMBER"));
        assertEquals("NA",log.get("TRANSACTIONSUBTYPE"));
        assertEquals("TFSS1234567",log.get("ACCOUNTNUMBER"));

//        assertEquals("C",log.get("DEBIT_CREDIT"));
        assertEquals("OUTGOING",log.get("DIRECTION"));
        assertEquals("TFSS1",log.get("PRODUCTTYPE"));
        assertEquals("LCOPN",log.get("TRANSACTIONCODE"));
        assertEquals("CASA",log.get("PAYMENTMODE"));
        assertEquals("Beneficiary Name",log.get("BENEFICIARYNAME1"));
        assertEquals("123 Fake Street",log.get("BENEFICIARYADDR1"));
        assertEquals(new BigDecimal("5103.27"),log.get("TRANSACTIONAMOUNT"));
        assertEquals(new BigDecimal("41.4900"),log.get("EXCHANGERATE"));
    }

    @Test
    public void logPhpLcAmendment(){

        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("processDate","121212");
        details.put("documentSubType1","STANDBY");
        details.put("accountNumber","cifNumber");
        details.put("negotiationCurrency","USD");
        details.put("beneficiary","Beneficiary Name");
        details.put("beneficiaryCustomerAddress","123 Fake Street");
        details.put("cifNumber","1234567");
        details.put("negotiationAmount","123");
        details.put("amount","12.25");
        details.put("issueDate","12/12/2012");
        details.put("expiryDate","12/12/2012");

        TradeService service = new TradeService();
        service.setDetails(details);

        jdbcTemplate.execute("INSERT INTO PAYMENT (ID,TRADESERVICEID,CHARGETYPE,STATUS) VALUES (1, '" + service.getTradeServiceId().toString() + "','PRODUCT','UNPAID')");
        jdbcTemplate.execute("INSERT INTO PAYMENTDETAIL (ID,PAYMENTINSTRUMENTTYPE,REFERENCENUMBER,AMOUNT,CURRENCY,STATUS,BOOKINGCURRENCY,INTERESTRATE,INTERESTTERM,REPRICINGTERM,REPRICINGTERMCODE,LOANTERM,LOANTERMCODE,LOANMATURITYDATE,REFERENCEID,PAYMENTID) VALUES (1,'CASA','s',0,'PHP','UNPAID','PHP',0,'s','s','s','s','s',CURRENT_TIMESTAMP,'s',1)");

        LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(service,null,"");
        amlaInformationLogger.logLcNegotiationEvent(event);

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        Map<String,Object> log = jdbcTemplate.queryForMap("SELECT * FROM TRANSACTIONLOG");
        assertNotNull(log.get("TXNDATE"));
        assertNotNull(log.get("TXNREFERENCENUMBER"));
        assertEquals("NA",log.get("DEALNUMBER"));
        assertEquals("NA",log.get("TRANSACTIONSUBTYPE"));
        assertEquals("TFSS1234567",log.get("ACCOUNTNUMBER"));

//        assertEquals("C",log.get("DEBIT_CREDIT"));
        assertEquals("OUTGOING",log.get("DIRECTION"));
        assertEquals("TFSS1",log.get("PRODUCTTYPE"));
        assertEquals("LCOPN",log.get("TRANSACTIONCODE"));
        assertEquals("CASA",log.get("PAYMENTMODE"));
        assertEquals("Beneficiary Name",log.get("BENEFICIARYNAME1"));
        assertEquals("123 Fake Street",log.get("BENEFICIARYADDR1"));
        assertEquals(new BigDecimal("5103.27"),log.get("TRANSACTIONAMOUNT"));
        assertEquals(new BigDecimal("41.4900"),log.get("EXCHANGERATE"));
    }

}
