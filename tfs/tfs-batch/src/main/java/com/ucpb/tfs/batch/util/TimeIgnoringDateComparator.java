package com.ucpb.tfs.batch.util;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class TimeIgnoringDateComparator implements Comparator<Date> {


    @Override
    public int compare(Date date1, Date date2) {
        Calendar calendar1 = getCalendarForDate(date1);
        Calendar calendar2 = getCalendarForDate(date2);
        if(calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)){
            return calendar1.get(Calendar.YEAR) - calendar2.get(Calendar.YEAR);
        }
        if(calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)){
            return calendar1.get(Calendar.MONTH) - calendar2.get(Calendar.MONTH);
        }
        return calendar1.get(Calendar.DAY_OF_MONTH) - calendar2.get(Calendar.DAY_OF_MONTH);
    }

    private Calendar getCalendarForDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
