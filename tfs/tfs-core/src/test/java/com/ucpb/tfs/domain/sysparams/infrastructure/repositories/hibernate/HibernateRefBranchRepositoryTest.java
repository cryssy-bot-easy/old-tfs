package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.Branch;
import com.ucpb.tfs.domain.reference.RefBranchRepository;
import com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate.HibernateRefBranchRepository;
import org.hibernate.SessionFactory;
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
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 */
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration
@Transactional
public class HibernateRefBranchRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {


    @Autowired
    @Qualifier("mySessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HibernateRefBranchRepository refBranchRepository;


    @Before
    public void setup(){
        jdbcTemplate.update("DELETE FROM REF_BRANCH");
    }

    @Test
    public void successfullyPersistRefBranchDetails(){
        Branch refBranch = new Branch();
        refBranch.setUnitCode("unit");
        refBranch.setBocCode("boc");
        refBranch.setCasaCreditLimit(new BigDecimal("3241.15"));

        assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM REF_BRANCH"));

        refBranchRepository.persist(refBranch);
        flush();

        Map<String,Object> branch = jdbcTemplate.queryForMap("SELECT * FROM REF_BRANCH");
        assertEquals("unit",branch.get("UNIT_CODE"));
        assertEquals("boc",branch.get("BOC_CODE"));
        assertEquals(new BigDecimal("3241.15"),branch.get("CREDIT_LIMIT"));

    }


    @Test
    public void retrieveRefBranchById(){
        jdbcTemplate.update("INSERT INTO REF_BRANCH (ID,BOC_CODE,UNIT_CODE,CREDIT_LIMIT) VALUES (1,'bocCode','unit',1241.56)");

        Branch branch = refBranchRepository.getBranchById(1L);
        assertEquals(Long.valueOf(1L),branch.getId());
        assertEquals("bocCode",branch.getBocCode());
        assertEquals("unit",branch.getUnitCode());

    }

    @Test
    public void returnNullOnInvalidBranchId(){
        jdbcTemplate.update("INSERT INTO REF_BRANCH (ID,BOC_CODE,UNIT_CODE,CREDIT_LIMIT) VALUES (134,'bocCode','unit',1241.56)");

        Branch branch = refBranchRepository.getBranchById(1L);
        assertNull(branch);
    }

    @Test
    public void retrieveBranchByBranchCode(){
        jdbcTemplate.update("INSERT INTO REF_BRANCH (ID,BOC_CODE,UNIT_CODE,CREDIT_LIMIT) VALUES (134,'bocCode','unit',1241.56)");

        Branch branch = refBranchRepository.getBranchByUnitCode("unit");
        assertNotNull(branch);
        assertEquals(Long.valueOf(134L),branch.getId());
        assertEquals("bocCode",branch.getBocCode());
        assertEquals("unit",branch.getUnitCode());
    }

    @Test
    public void returnNullOnInvalidBranchCode(){
        jdbcTemplate.update("INSERT INTO REF_BRANCH (ID,BOC_CODE,UNIT_CODE,CREDIT_LIMIT) VALUES (134,'bocCode','unit1',1241.56)");

        Branch branch = refBranchRepository.getBranchByUnitCode("unit");
        assertNull(branch);
    }


    private void flush(){
        sessionFactory.getCurrentSession().flush();
    }



}
