package com.ucpb.tfs.batch.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class FileUtil {

    public static List<String> read(File file) throws IOException {
        List<String> contents = new ArrayList<String>();
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(file));
            String currentLine;
            while((currentLine = reader.readLine()) != null){
                contents.add(currentLine);
            }
        } finally {
            IOUtil.closeQuietly(reader);
        }
        return contents;
    }

    public static String[] read(File file, String delimiter) throws IOException {
        StringBuilder builder = new StringBuilder();
        for(String contents : read(file)){
            builder.append(contents);
        }
        return builder.toString().split(delimiter);
    }
    
    public static File getFileFromResource(String resourceName) throws URISyntaxException, FileNotFoundException{
    	URL url = FileUtil.class.getResource(resourceName);
    	if(url == null){
    		throw new FileNotFoundException("file does not exist");
    	}
    	return new File(url.toURI());
    }
}
