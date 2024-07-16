package com.ucpb.tfs.util;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
public class FileUtilTest {


    @Test
    public void successfullyLoadFile() throws FileNotFoundException {
        File file = FileUtil.getFile("/testfile.txt");
        assertTrue(file.exists());
    }

    @Test
    public void successfullyLoadFileSystemId(){
        String systemId = FileUtil.getSystemId("/testfile.txt");
        System.out.println("************* " + systemId);
        assertNotNull(systemId);
    }

    @Test
    public void successfullyLoadFileAsInputStream() throws IOException {
        InputStream input = null;
        try{
            input = FileUtil.getFileAsStream("/testfile.txt");
            assertNotNull(input);
            String contents = IOUtils.toString(input);
            assertEquals("HELLO WORLD!",contents);
        }finally{
            IOUtils.closeQuietly(input);
        }
    }


}
