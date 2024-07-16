package com.ucpb.tfs.batch.util;

import java.io.*;

/**
 */
public class CopyUtil {


    public static <T> T copy(T source) throws CopyFailedException {

        T obj = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();

            in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            obj = (T)in.readObject();
        }
        catch(IOException e) {
            throw new CopyFailedException("Failed to copy source object",e);
        } catch (ClassNotFoundException e) {
            throw new CopyFailedException("Failed to copy source object",e);
        }finally {
            IOUtil.closeQuietly(out);
            IOUtil.closeQuietly(in);
        }
        return obj;
    }

}
