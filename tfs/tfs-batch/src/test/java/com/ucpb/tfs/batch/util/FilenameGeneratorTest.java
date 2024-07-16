package com.ucpb.tfs.batch.util;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 */
public class FilenameGeneratorTest {

    @Test
    public void successfullyGenerateFilename(){
        String filename = FilenameGenerator.generate("TFT","csv");
        System.out.println("** FILENAME : " + filename);
        assertTrue(filename.matches("(TFT_)[\\d]*(\\.csv)"));

    }

}
