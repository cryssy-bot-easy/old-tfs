package com.ucpb.tfs.domain.settlementaccount.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.audit.infrastructure.repositories.HibernateAccountLogRepository;
import com.ucpb.tfs.domain.settlementaccount.AccountsReceivable;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 */
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration
@Transactional
public class HibernateAccountsReceivableRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    @Qualifier("mySessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private HibernateAccountsReceivableRepository repository;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    @Test
    public void assignIdToARObjectAfterSuccessfulySave(){
        AccountsReceivable accountsReceivable = new AccountsReceivable(new SettlementAccountNumber("settlementAccountNumber"));
        assertNull(accountsReceivable.getId());
        repository.persist(accountsReceivable);
//        sessionFactory.getCurrentSession().flush();
        assertNotNull(accountsReceivable.getId());
        System.out.println("******** AR ID: " + accountsReceivable.getId());
    }

}
