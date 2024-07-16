package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:test-context.xml")
@Ignore
public class AllocationFileGenerationJobIntegrationTest {

    @Autowired
    @Qualifier("glAllocationsReportJob")
    private AllocationFileReportGeneratorJob job;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private File outputFile = new File("tfs-batch/src/test/resources/outputReports/TFGLALLOC.txt");


    @Before
    @After
    public void cleanup(){
        outputFile.delete();
    }


    @Test
    public void successfullyGenerateReport() throws InterruptedException, IOException {
        assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.INT_ACCENTRYACTUAL"));
        job.execute();
        //give time for the report to generate
        Thread.sleep(3000);
        assertTrue(outputFile.exists());
        
        List<String> contents = FileUtil.read(outputFile);
        assertFalse(contents.isEmpty());
        for(String entry : contents){
        	System.out.println("******** " + entry);
        }
    }


}
