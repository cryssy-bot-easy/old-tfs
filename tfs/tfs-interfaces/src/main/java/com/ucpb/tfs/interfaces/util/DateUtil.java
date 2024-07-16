package com.ucpb.tfs.interfaces.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateUtil {

	private DateUtil(){
		//don't instantiate
	}
	
	public static int formatToInt(String format, Date date){
		if(!StringUtils.isAlphanumeric(format)){
			throw new IllegalArgumentException("Date format must not contain special characters");
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return Integer.parseInt(simpleDateFormat.format(date));
	}

    public static int formatDateStringToInt(String targetFormat,String sourceFormat,String dateString) throws ParseException {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceFormat);
        return formatToInt(targetFormat,sourceDateFormat.parse(dateString));
    }

    public static String formatToString(String format, Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
	
	public static Date formatToDate(String format,int date) throws ParseException{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.parse(Integer.toString(date));
	}

    public static String formatDateString(String targetFormat, String dateStringFormat,String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateStringFormat);
        return formatToString(targetFormat,simpleDateFormat.parse(dateString));
    }
	
}
