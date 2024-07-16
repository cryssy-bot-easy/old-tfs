package com.ucpb.tfs.interfaces.sql;

import com.ucpb.tfs.interfaces.util.DateUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 */
public class DbUtils {

    private static final int MAX_COLUMN_SIZE = 6;
    private static final String UCPB_DATE_FORMAT = "MMddyy";

    private DbUtils(){

    }

    public static Timestamp to_date(String dateString, String format){
        String convertedFormat = format.replaceAll("m","M");
        SimpleDateFormat dateFormat = new SimpleDateFormat(convertedFormat);
        java.util.Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse input date",e);
        }
        return new Timestamp(date.getTime());
    }

    public static String digits(BigDecimal numberToPad){
        return padLeft(numberToPad.toPlainString(), MAX_COLUMN_SIZE, '0');
    }

    public static BigDecimal getCurrentDate(){
        java.util.Date date = new java.util.Date();
        return new BigDecimal(DateUtil.formatToInt(UCPB_DATE_FORMAT,date));
    }

    private static String padLeft(String sourceString, int padLength, char padChar){
        StringBuilder result = new StringBuilder(sourceString != null ? sourceString : "");
        while(result.length() < padLength){
            result.insert(0,padChar);
        }
        return result.toString();
    }

    public static String replace(String sourceText,String replaceFrom,String replaceTo){
    	if(sourceText != null && replaceFrom != null && replaceTo != null){
    		return sourceText.replace(replaceFrom, replaceTo);    		
    	}
    	return "";
    }
}
