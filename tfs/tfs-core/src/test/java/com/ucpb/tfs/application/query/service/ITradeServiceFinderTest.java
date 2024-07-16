package com.ucpb.tfs.application.query.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
@ContextConfiguration("classpath:finder-app-context.xml")
@TransactionConfiguration
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class ITradeServiceFinderTest {

    private ITradeServiceFinder ITradeServiceFinder;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        ITradeServiceFinder = (ITradeServiceFinder) applicationContext.getBean("ITradeServiceFinder");
    }

    @Test
    public void successfullyQueryAllApprovedAmendments(){
        assertNotNull(ITradeServiceFinder);
        jdbcTemplate.update("INSERT INTO TRADESERVICE (TRADESERVICEID,SERVICEINSTRUCTIONID,TRADESERVICEREFERENCENUMBER,DOCUMENTNUMBER,STATUS,PROCESSID,SERVICETYPE) VALUES ('e7b3e6f8-a1ca-497c-b567-a3f2bcb93559','932-12-22258',null,'909-01-932-12-709721','APPROVED',null,'AMENDMENT')");

        jdbcTemplate.update("INSERT INTO TRADESERVICE (TRADESERVICEID,SERVICEINSTRUCTIONID,TRADESERVICEREFERENCENUMBER,DOCUMENTNUMBER,STATUS,PROCESSID,SERVICETYPE) VALUES ('e7b3e6f8-a1ca-497c-b567-a3f2bcb93876','932-12-22258',null,'909-01-932-12-709721','PENDING',null,'OPENING')");

        assertEquals(1,ITradeServiceFinder.getApprovedAmmendments("909-01-932-12-709721"));
    }

    @Test
    public void doNotReturnUnapprovedAmendments(){
        jdbcTemplate.update("INSERT INTO TRADESERVICE (TRADESERVICEID,SERVICEINSTRUCTIONID,TRADESERVICEREFERENCENUMBER,DOCUMENTNUMBER,STATUS,PROCESSID,SERVICETYPE) VALUES ('e7b3e6f8-a1ca-497c-b567-a3f2bcb93559','932-12-22258',null,'909-01-932-12-709721','PENDING',null,'AMENDMENT')");
        assertEquals(0,ITradeServiceFinder.getApprovedAmmendments("909-01-932-12-709721"));

    }
}
