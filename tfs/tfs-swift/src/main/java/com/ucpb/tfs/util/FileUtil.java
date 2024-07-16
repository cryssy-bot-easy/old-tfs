package com.ucpb.tfs.util;

import org.apache.commons.io.IOUtils;

import javax.xml.transform.Source;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 */
public class FileUtil {

    /**
     * Returns a File instance from the given file path.
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */

    public static File getFile(String filePath) throws FileNotFoundException {
        URL url = FileUtil.class.getResource(filePath);
        if (url == null) {
            throw new FileNotFoundException("file does not exist");
        }
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            //should never happen.
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the URL of the given file path as a String
     * @param filePath
     * @return
     */

    public static String getSystemId(String filePath){
        URL url = FileUtil.class.getResource(filePath);
        if(url != null){
            return url.toExternalForm();
        }
        return null;
    }

    public static InputStream getFileAsStream(String filePath){
        return FileUtil.class.getResourceAsStream(filePath);
    }

    /**
     * Reads the given file indicated by the input file path then outputs the file contents
     * as a single string.
     * @param filePath
     * @return the file contents
     * @throws IOException
     */

    public static String getFileAsString(String filePath) throws IOException {
        File file = getFile(filePath);
        return readFileAsString(file);
    }

    public static String getFileAsString(File file) throws IOException {
        return readFileAsString(file);
    }
    
    public static boolean copyFile(File source,File destination) throws IOException{
    	FileChannel input = null;
    	FileChannel output = null;
    	try{
    		input = new FileInputStream(source).getChannel();
    		output= new FileOutputStream(destination).getChannel();
    		output.transferFrom(input, 0, input.size());
    		return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}finally{
    		input.close();
    		output.close();
    	}
    }

    private static String readFileAsString(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return builder.toString();
    }


}
