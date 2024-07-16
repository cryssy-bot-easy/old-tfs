package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.MessageChannel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:test-context.xml")
public class FixedFileReportGeneratorJobIntegrationTest {

    @Autowired
    @Qualifier("glReportGeneratorJob")
    private FixedFileReportGeneratorJob job;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private File outputFile = new File("tfs-batch/src/test/resources/outputReports/TFGLMVMT.txt");


    @Before
    @After
    public void cleanup(){
        outputFile.delete();
    }


    @Test
    public void successfullyPerformQueryWithNoParams() throws InterruptedException {
        assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM INT_ACCENTRYACTUAL"));
        job.setQuery("SELECT * FROM INT_ACCENTRYACTUAL");
        job.execute();
        //give time for the report to generate
        Thread.sleep(10000);
        assertTrue(outputFile.exists());
    }

    @Test
    public void successfullyPerformQueryWithParams() throws IOException {
        // 1350712422736 - October 20, 2012
        Object[] args = { 1};
//        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM INT_ACCENTRYACTUAL WHERE SEQUENCENUMBER = 123456789"));
        job.setQuery("SELECT * FROM INT_ACCENTRYACTUAL where id = ?");
        job.setArgs(args);
        job.execute();
        assertTrue(outputFile.exists());
        List<String> contents = FileUtil.read(outputFile);
        assertEquals(1,contents.size());
        System.out.println("***** " + contents.get(0));
        assertTrue(StringUtils.isNotBlank(contents.get(0)));

    }

    }
