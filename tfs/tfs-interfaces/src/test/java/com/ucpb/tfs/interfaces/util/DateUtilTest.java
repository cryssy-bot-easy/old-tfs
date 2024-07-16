package com.ucpb.tfs.interfaces.util;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.ucpb.tfs.interfaces.util.DateUtil;

public class DateUtilTest {

	//1343469458253 = July 28, 2012

   @Test
   public void leapYearTest() throws ParseException {
       assertTrue(isLeapYear(2012));
       SimpleDateFormat df = new SimpleDateFormat("yyyyDDD");
       Date date = df.parse("2012060");
       Calendar leapYear = Calendar.getInstance();
       leapYear.setTime(date);
       assertEquals(29, leapYear.get(Calendar.DATE));
       assertEquals(1,leapYear.get(Calendar.MONTH));
       assertEquals(60,leapYear.get(Calendar.DAY_OF_YEAR));

   }

    @Test
    public void isNotLeapYear() throws ParseException {
        assertFalse(isLeapYear(2013));
        SimpleDateFormat df = new SimpleDateFormat("yyyyDDD");
        Date date = df.parse("2013060");
        Calendar leapYear = Calendar.getInstance();
        leapYear.setTime(date);
        assertEquals(1,leapYear.get(Calendar.DATE));
        assertEquals(2,leapYear.get(Calendar.MONTH));
        assertEquals(60,leapYear.get(Calendar.DAY_OF_YEAR));
    }

    private boolean isLeapYear(int year){
        return ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
    }
	
	@Test
	public void convertSingleDigitMonthToInteger(){
		assertEquals(72812,DateUtil.formatToInt("MMddyy", new Date(1343469458253L)));
	}

    @Test
    public void convertValidDateStringToInt() throws ParseException {
        assertEquals(120113,DateUtil.formatDateStringToInt("MMddyy","MM/dd/yyyy","12/01/13"));
    }

    @Test(expected = ParseException.class)
    public void parseExceptionOnInvalidDateString() throws ParseException {
        DateUtil.formatDateStringToInt("MMddyy", "MM/dd/yyyy", "1213//01411/13134");
    }
	
	@Test(expected=IllegalArgumentException.class)
	public void throwsExceptionOnNonAlphanumericDateFormat(){
		assertEquals(72812,DateUtil.formatToInt("MM-dd-yy", new Date(1343469458253L)));
	}

    @Test
    public void convertSingleDigitMonthToString(){
        assertEquals("072812",DateUtil.formatToString("MMddyy", new Date(1343469458253L)));

    }

	@Test
	public void convertSingleDigitDateToInteger(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2012);
		
		assertEquals(120112,DateUtil.formatToInt("MMddyy", calendar.getTime()));
	}
	
	@Test
	public void convertSingleDigitMonthAndDateToInt(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2012);
		
		assertEquals(10112,DateUtil.formatToInt("MMddyy", calendar.getTime()));
	}

}
