package com.ucpb.tfs.interfaces.repositories;

import com.ucpb.tfs.interfaces.domain.Sequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class SequenceRepositoryIntegrationTest {


    @Autowired
    private SequenceRepository sequenceRepository;

    @Autowired
    @Qualifier("tfsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    @Before
    @After
    public void setup(){
        jdbcTemplate.update("DELETE FROM SIBS_SEQUENCES");
    }

    @Test
    public void getSequenceNumber(){
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM SIBS_SEQUENCES"));
        jdbcTemplate.update("INSERT INTO SIBS_SEQUENCES (SEQUENCE, DATE_INITIALIZED,SEQUENCE_TYPE) VALUES (1,CURRENT_TIMESTAMP,'LOAN')");

        Sequence sequence =  sequenceRepository.getSequence("LOAN");
        assertNotNull(sequence);
        assertEquals(1,sequence.getSequenceNumber());
        assertNotNull(sequence.getDateInitialized());
    }

    @Test
    public void successfullyIncrementSequenceNumber(){
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM SIBS_SEQUENCES"));
        jdbcTemplate.update("INSERT INTO SIBS_SEQUENCES (SEQUENCE, DATE_INITIALIZED,SEQUENCE_TYPE) VALUES (1,CURRENT_TIMESTAMP,'LOAN')");

        Sequence sequence =  sequenceRepository.getSequence("LOAN");
        sequence.increment();
        sequenceRepository.incrementSequence("LOAN");

        assertEquals(2,jdbcTemplate.queryForInt("SELECT SEQUENCE FROM SIBS_SEQUENCES WHERE SEQUENCE_TYPE = 'LOAN'"));
    }

}
