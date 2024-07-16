package com.ucpb.tfs.batch.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 */
public class FileUtilTest {

    private File file;

    @Before
    public void setup() throws IOException, URISyntaxException {
        file = FileUtil.getFileFromResource("/testFile.txt");
    }

    @Test
    public void successfullyReadEntireFile() throws IOException {
        List<String> contents = FileUtil.read(file);
        assertEquals(2,contents.size());
        assertEquals("HELLO",contents.get(0));
        assertEquals("WORLD!",contents.get(1));
    }

    @Test(expected = IOException.class)
    public void exceptionOnNonExistentFile() throws IOException {
        FileUtil.read(new File("idonotexist.txt"));
    }
    
    @Test
    public void successfullyGetFileFromTheClasspath() throws URISyntaxException, FileNotFoundException{
    	File file = FileUtil.getFileFromResource("/quartz_tables_derby.sql");
    	assertTrue(file.exists());
    }
    
    @Test(expected = FileNotFoundException.class)
    public void returnNonExistentFileFromInvalidUrl() throws URISyntaxException, FileNotFoundException{
    	FileUtil.getFileFromResource("/invalidFile.txt");
    }

    @Test
    public void successfullyParseDelimitedFile() throws IOException, URISyntaxException {
        String[] result = FileUtil.read(FileUtil.getFileFromResource("/read-this.txt"),";");
        assertEquals(3,result.length);
        assertEquals("this",result[0]);
        assertEquals("is",result[1]);
        assertEquals("delimited",result[2]);
    }


}
