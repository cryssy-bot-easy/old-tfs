package com.ucpb.tfs.batch.listener;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:trigger-context.xml")
public class DbLoggingJobListenerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        jdbcTemplate.update("DELETE FROM JOB_HISTORY");
        jdbcTemplate.update("DELETE FROM JOB_EXCEPTIONS");
    }


    @Test
    public void logFailedTransactionToJobHistory() throws InterruptedException {
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM JOB_HISTORY"));

        //give time for the job to run
        Thread.sleep(5000);

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM JOB_HISTORY"));
        Map<String,Object> row = jdbcTemplate.queryForMap("SELECT * FROM JOB_HISTORY");
        assertNotNull(row.get("START_TIME"));
        assertNotNull(row.get("END_TIME"));
        assertNotNull(row.get("PREVIOUS_FIRE_TIME"));
        assertNotNull(row.get("NEXT_FIRE_TIME"));
//        System.out.println(row.get("ID").getClass());
        assertEquals(Integer.valueOf(1),row.get("ID"));
        assertEquals("FAILED",row.get("STATUS"));

        for(Map.Entry<String,Object> entry : row.entrySet()){
            System.out.println("********* KEY: " + entry.getKey() + " : " + entry.getValue());
        }

    }



}
