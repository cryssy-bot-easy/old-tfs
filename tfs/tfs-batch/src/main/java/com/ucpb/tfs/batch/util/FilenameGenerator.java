package com.ucpb.tfs.batch.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class FilenameGenerator {

    public static String generate(String prefix) {

        return prefix + ".txt";
    }

    public static String generate(String prefix, String extension) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMdd");
        return prefix + "_" + simpleDateFormat.format(new Date().getTime()) + "." + extension;

        // return prefix + "_" + new Date().getTime() + "." + extension;
    }

    private static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(new Date());
    }
}
