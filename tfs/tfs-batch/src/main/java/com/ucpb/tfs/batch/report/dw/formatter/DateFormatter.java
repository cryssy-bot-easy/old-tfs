package com.ucpb.tfs.batch.report.dw.formatter;


import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.exception.FixedFormatException;
import com.ancientprogramming.fixedformat4j.format.FixedFormatter;
import com.ancientprogramming.fixedformat4j.format.FormatInstructions;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter implements FixedFormatter<Date> {

    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";

    @Override
    public Date parse(String s, FormatInstructions formatInstructions) throws FixedFormatException {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        if(StringUtils.isEmpty(s)){
            return null;
        }
        try {
            return format.parse(s);
        } catch (ParseException e) {
            throw new FixedFormatException("Failed to parse input date: " + s, e);
        }
    }

    @Override
    public String format(Date date, FormatInstructions formatInstructions) throws FixedFormatException {
        String result = "0";
        if(date != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            result = simpleDateFormat.format(date);
        }
        if(Align.RIGHT.equals(formatInstructions.getAlignment())){
            return StringUtils.leftPad(result,formatInstructions.getLength()," ");
        }
        return StringUtils.rightPad(result,formatInstructions.getLength()," ");
    }
}
