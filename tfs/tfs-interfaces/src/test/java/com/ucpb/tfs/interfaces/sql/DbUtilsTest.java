package com.ucpb.tfs.interfaces.sql;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 */
public class DbUtilsTest {

    @Test
    public void successfullyConvertDateString(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,Calendar.SEPTEMBER);
        calendar.set(Calendar.DAY_OF_MONTH,30);
        calendar.set(Calendar.YEAR,2012);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        assertEquals(calendar.getTime().getTime(), DbUtils.to_date("093012", "mmddyy").getTime());
    }

    @Test
    public void successfullyConvertDigits(){
        assertEquals("093012",DbUtils.digits(new BigDecimal("93012")));
    }

}
