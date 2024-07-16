package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.CorrespondentBank;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;

/**
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration("classpath*:transactionlog-app-context.xml")
public class HibernateCorrespondentBankRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private HibernateCorrespondentBankRepository repository;

    @Autowired
    private JdbcTemplate template;


    @Test
    public void retrieveCorrectCorrespondentBankByBankCodeI(){
        CorrespondentBank bank = repository.getCorrespondentBankByBankCode("BANKCODE1");
        assertEquals("BANKCODE1",bank.getBankCode());
    }
}
