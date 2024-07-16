package com.ucpb.tfs.batch.util;

import java.io.*;

/**
 */
public final class IOUtil {

    public static void closeQuietly(Writer writer){
        if(writer != null){
            try {
                writer.close();
            } catch (IOException e) {
                //do nothing
            }
        }
    }

    public static void closeQuietly(Closeable io){
        if(io != null){
            try {
                io.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static void copyTo(InputStream is, OutputStream os) throws IOException {
        byte[] byteInput = new byte[10000];
        while(is.read(byteInput) != -1){
            os.write(byteInput);
        }
    }

}
