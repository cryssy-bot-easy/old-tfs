package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:test-context.xml")
public class MasterFileRecordGeneratorJobIntegrationTest {

    @Autowired
    private MasterFileReportGeneratorJob job;

    private File outputFile = new File("tfs-batch/src/test/resources/outputReports/TFGLMAST.txt");


    @Before
    @After
    public void cleanup(){
        outputFile.delete();
    }

    @Test
    public void successfullyGenerateMasterFile() throws InterruptedException, IOException {
        job.execute();
        Thread.sleep(3000);
        assertTrue(outputFile.exists());
        List<String> contents = FileUtil.read(outputFile);
        for(String entry : contents){
            System.out.println("********"+entry);
        }
    }

}
