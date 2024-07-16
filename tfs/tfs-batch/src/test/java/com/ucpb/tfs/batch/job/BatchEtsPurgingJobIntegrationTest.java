package com.ucpb.tfs.batch.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:test-context.xml")
public class BatchEtsPurgingJobIntegrationTest {

    private BatchEtsPurgingJob job;

    private JdbcTemplate template;


    @Test
    public void successfullyPurgeAllUnactedEts(){



    }

}
