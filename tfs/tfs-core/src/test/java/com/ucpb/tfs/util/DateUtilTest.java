package com.ucpb.tfs.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;

import com.ucpb.tfs.utils.DateUtil;

public class DateUtilTest {

	
	@Test
	public void successfullyGetLastTwoDigitsOfYear(){
		//1345091684376 - August 16, 2012
		assertEquals("12",DateUtil.getLastTwoDigitsOfYear(new Date(1345091684376L)));
	}

    @Test
    public void loans() throws ParseException {
//        int days = Days.daysBetween(new DateMidnight(DateUtil.convertFromSibsDateFormat(32813)), new DateMidnight(Calendar.getInstance())).getDays();
        int days = Days.daysBetween(new DateMidnight(),new DateMidnight(DateUtil.convertFromSibsDateFormat(32813))).getDays();
        assertEquals(13,days);
    }

    @Test
    public void successfullyConvertDateFormat(){
        assertEquals("01/02/1987",DateUtil.convertDateFormat("01021987","MMddyyyy","MM/dd/yyyy"));
    }


    @Test
    public void getIntegerRepresentationOfDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(2012,Calendar.NOVEMBER,28);
        assertEquals(112812,DateUtil.convertToDateInt(cal.getTime()));
    }

    @Test
    public void formatToDateString(){
        //1343469458253 = July 28, 2012
        assertEquals("07/28/2012",DateUtil.convertToDateString(new Date(1343469458253L),"MM/dd/yyyy"));
    }

    @Test
    public void formatToTfsDate(){
        //1343469458253 = July 28, 2012
        assertEquals("07/28/2012",DateUtil.convertToTfsDateString(new Date(1343469458253L)));
    }

    @Test
    public void successfullyConvertFromSibsDateFormatSingleDigitMonth() throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date date = DateUtil.convertFromSibsDateFormat(20212);
        cal.setTime(date);
        //in java - the months start with 0 (january)
        assertEquals(1,cal.get(Calendar.MONTH));
        assertEquals(2,cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(2012,cal.get(Calendar.YEAR));
    }

    @Test
    public void successfullyConvertFromSibsDateFormatDoubleDigitMonth() throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date date = DateUtil.convertFromSibsDateFormat(100212);
        cal.setTime(date);
        //in java - the months start with 0 (january)
        assertEquals(9,cal.get(Calendar.MONTH));
        assertEquals(2,cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(2012,cal.get(Calendar.YEAR));
    }
}
