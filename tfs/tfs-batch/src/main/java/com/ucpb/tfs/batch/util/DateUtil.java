package com.ucpb.tfs.batch.util;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class DateUtil {

    public static String convertToDateString(Date date,String dateFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    public static int formatToInt(String format, Date date) {

        if (!StringUtils.isAlphanumeric(format)) {
            throw new IllegalArgumentException("Date format must not contain special characters");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return Integer.parseInt(simpleDateFormat.format(date));
    }
}
