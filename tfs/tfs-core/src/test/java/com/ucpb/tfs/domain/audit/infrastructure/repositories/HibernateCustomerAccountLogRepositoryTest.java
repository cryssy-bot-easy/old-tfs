package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CustomerAccount;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;

/**
 */
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration
@Transactional
public class HibernateCustomerAccountLogRepositoryTest  extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    @Qualifier("mySessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private HibernateCustomerAccountLogRepository customerAccountLogRepository;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;



    @Test
    public void successfullyPersistToDatabase(){
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM CUSTOMERACCOUNT"));
        CustomerAccount log = new CustomerAccount();
        log.setAccountNumber("acctNumber");
        log.setCustomerNumber("customerNumber");
        customerAccountLogRepository.persist(log);

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM CUSTOMERACCOUNT WHERE ACCOUNT_NUMBER = 'acctNumber' and CUSTOMER_NUMBER = 'customerNumber'"));

    }




}
