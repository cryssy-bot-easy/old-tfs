package com.ucpb.tfs.batch.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertTrue;

public class TimeIgnoringDateComparatorTest {

    private TimeIgnoringDateComparator comparator = new TimeIgnoringDateComparator();

    @Test
    public void returnNegativeIfYearIsLess(){
        //32506920599924 - Feb 7, 3000
        //949925429939 - Feb 7, 2000
        assertTrue(comparator.compare(new Date(949925429939L), new Date(32506920599924L)) < 0);
    }

    @Test
    public void returnPositiveIfYearIsGreater(){
        //32506920599924 - Feb 7, 3000
        //949925429939 - Feb 7, 2000
        assertTrue(comparator.compare(new Date(32506920599924L), new Date(949925429939L)) > 0);
    }

    @Test
    public void returnNegativeIfMonthIsLess(){
        //1357565904064 - jan 7, 2013
        //1360239561651 - feb 7, 2013 8:19pm
        assertTrue(comparator.compare(new Date(1357565904064L), new Date(1360239561651L)) < 0);

    }

    @Test
    public void returnPositiveIfMonthIsGreater(){
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.MONTH,1);
//        System.out.println(cal.getTimeInMillis());
        //1357565904064 - jan 7, 2013
        //1360239561651 - feb 7, 2013 8:19pm
        assertTrue(comparator.compare(new Date(1360239561651L), new Date(1357565904064L)) > 0);

    }

    @Test
    public void returnNegativeIfDayIsLess(){
        //1360152982622 - Feb 6, 2013
        //1360239401106 - Feb 7, 2013
        assertTrue(comparator.compare(new Date(1360152982622L), new Date(1360239401106L)) < 0);
    }

    @Test
    public void returnNegativeIfDayIsGreater(){
        //1360152982622 - Feb 6, 2013
        //1360239401106 - Feb 7, 2013
        assertTrue(comparator.compare(new Date(1360239401106L), new Date(1360152982622L)) > 0);
    }

    @Test
    public void ignoreTimeDifference(){
        assertTrue(comparator.compare(new Date(1360239561651L), new Date(1360244381037L)) == 0);
    }



}
