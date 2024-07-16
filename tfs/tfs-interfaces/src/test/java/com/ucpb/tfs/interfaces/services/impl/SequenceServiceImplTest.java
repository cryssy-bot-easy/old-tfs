package com.ucpb.tfs.interfaces.services.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class SequenceServiceImplTest {

    @Autowired
    private SequenceServiceImpl sequenceService;

    @Autowired
    private JdbcTemplate tfsJdbcTemplate;


    @Before
    @After
    public void cleanup(){
        tfsJdbcTemplate.execute("DELETE FROM SIBS_SEQUENCES");
        tfsJdbcTemplate.update("INSERT INTO SIBS_SEQUENCES (SEQUENCE,DATE_INITIALIZED,SEQUENCE_TYPE) VALUES (0,CURRENT_TIMESTAMP,'LOAN')");
        tfsJdbcTemplate.update("INSERT INTO SIBS_SEQUENCES (SEQUENCE,DATE_INITIALIZED,SEQUENCE_TYPE) VALUES (0,CURRENT_TIMESTAMP,'BALANCE')");

    }

    @Test
    public void retrieveAndIncrementLoanSequence(){
        assertEquals(0,sequenceService.getLoanSequence());
        assertEquals(1,tfsJdbcTemplate.queryForInt("SELECT SEQUENCE FROM SIBS_SEQUENCES WHERE SEQUENCE_TYPE = 'LOAN'"));
        assertEquals(1,sequenceService.getLoanSequence());
        assertEquals(2,tfsJdbcTemplate.queryForInt("SELECT SEQUENCE FROM SIBS_SEQUENCES WHERE SEQUENCE_TYPE = 'LOAN'"));
    }

    @Test
    public void retrieveAndIncrementBalanceSequence(){
        assertEquals(0,sequenceService.getFacilityBalanceSequence());
        assertEquals(1,tfsJdbcTemplate.queryForInt("SELECT SEQUENCE FROM SIBS_SEQUENCES WHERE SEQUENCE_TYPE = 'BALANCE'"));
        assertEquals(1,sequenceService.getFacilityBalanceSequence());
        assertEquals(2,tfsJdbcTemplate.queryForInt("SELECT SEQUENCE FROM SIBS_SEQUENCES WHERE SEQUENCE_TYPE = 'BALANCE'"));
    }



}
