package com.ucpb.tfs.domain.routing.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.DailyBalance;
import com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate.HibernateDailyBalanceRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 */
@TransactionConfiguration
@Transactional
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
public class HibernateDailyBalanceRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    @Qualifier("dailyBalanceRepository")
    private HibernateDailyBalanceRepository repository;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        jdbcTemplate.execute("DELETE FROM DAILYBALANCE");
    }

    @Test
    public void saveToRepository(){
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM DAILYBALANCE"));

        DailyBalance balance = new DailyBalance();
        balance.setBalance(new BigDecimal("63.01"));
        balance.setBalanceDate(new Date());
        balance.setDocumentNumber("docnum");

        repository.save(balance);
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM DAILYBALANCE"));


    }
}
