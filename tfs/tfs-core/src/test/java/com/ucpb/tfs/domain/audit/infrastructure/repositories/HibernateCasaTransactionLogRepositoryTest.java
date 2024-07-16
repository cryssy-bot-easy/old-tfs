package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CasaTransactionLog;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration(defaultRollback = false)
public class HibernateCasaTransactionLogRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

    @Autowired
    private HibernateCasaTransactionLogRepository repository;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Before
    public void tearDown(){
        jdbcTemplate.execute("DELETE FROM CASATRANSACTIONLOG");
    }


    @Test
    @Transactional
    @Rollback(false)
    public void successfullyPersistLog(){
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM CASATRANSACTIONLOG"));
        CasaTransactionLog log = new CasaTransactionLog();
        System.out.println("********** " + new Date().getTime());
        log.setAccountName("acctname");
        log.setAccountNumber("accountNum");
        log.setTradeServiceId(new TradeServiceId("121414-13212293"));
        log.setHostStatus("0000");
        log.setTellerId("teller");
        log.setSupId("sup1d");
        log.setCurrency(Currency.getInstance("PHP"));
        log.setTransactionAmount(new BigDecimal("121314.00"));
        //january 25, 2013 -  1359106878305L
        log.setTransactionTime(new Date(1359106878305L));

        repository.save(log);

        sessionFactory.getCurrentSession().flush();

        Map<String,Object> savedLog = jdbcTemplate.queryForMap("SELECT * FROM CASATRANSACTIONLOG");
        assertEquals("0000",savedLog.get("HOST_STATUS"));
        assertEquals("acctname",savedLog.get("ACCOUNT_NAME"));
        assertEquals("accountNum",savedLog.get("ACCOUNT_NUMBER"));
        assertEquals("teller",savedLog.get("TELLER_ID"));
        assertEquals("sup1d",savedLog.get("SUPID"));
        assertEquals("PHP",savedLog.get("CURRENCY"));
        assertEquals("121414-13212293",savedLog.get("TRADESERVICEID"));
        assertTrue(new BigDecimal("121314.00").compareTo((BigDecimal)savedLog.get("TRANSACTION_AMOUNT"))==0);
        assertEquals(new Date(1359106878305L),savedLog.get("TRANSACTION_TIME"));

    }


}
