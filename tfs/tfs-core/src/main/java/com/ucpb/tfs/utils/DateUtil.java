package com.ucpb.tfs.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	private static final String SIBS_DATEFORMAT = "MMddyy";
    
    private DateUtil(){
		//do not instantiate
	}
	
	public static String getLastTwoDigitsOfYear(Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy");
		return simpleDateFormat.format(date);
	}

    public static Date convertToDate(String date) throws ParseException {
        if(!StringUtils.hasText(date)){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIBS_DATEFORMAT);
        return simpleDateFormat.parse(date);
    }

    public static int convertToDateInt(String date) throws ParseException {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(SIBS_DATEFORMAT);
        return Integer.parseInt(targetDateFormat.format(sourceDateFormat.parse(date)));
    }

    public static int convertToDateInt(Date date){
        SimpleDateFormat targetFormat = new SimpleDateFormat(SIBS_DATEFORMAT);
        return Integer.parseInt(targetFormat.format(date));
    }

    public static int convertToDateInt(String date,String sourceFormat) throws ParseException {
        SimpleDateFormat sdfSource = new SimpleDateFormat(sourceFormat);
        SimpleDateFormat targetFormat = new SimpleDateFormat(SIBS_DATEFORMAT);
        return Integer.parseInt(targetFormat.format(sdfSource.parse(date)));

    }

    public static Date convertToDate(String date, String dateFormat) throws ParseException {
        if(!StringUtils.hasText(date)){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try{
            return simpleDateFormat.parse(date);
        }catch(ParseException e){
            SimpleDateFormat otherFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            return otherFormat.parse(date);
        }
    }

    public static Date convertFromSibsDateFormat(int date) throws ParseException {
        return DateUtil.convertToDate(String.format("%1$06d",date),SIBS_DATEFORMAT);
    }

    public static String convertToDateString(Date date,String dateFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);    
        return simpleDateFormat.format(date);
    }

    public static String convertDateFormat(String dateString, String originalFormat, String targetFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(originalFormat);
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(targetFormat);
        try {
            return targetDateFormat.format(simpleDateFormat.parse(dateString));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertToTfsDateString(Date date){
        return convertToDateString(date,"MM/dd/yyyy");
    }
    
    public static String convertToTfsTimeStamp(Date date){
        return convertToDateString(date,"MM/dd/yyyy HH:mm:ss");
    }
    
    public static String convertToTimeString(Date date){
    	return convertToDateString(date,"hh:mm a");    	
    }

}
