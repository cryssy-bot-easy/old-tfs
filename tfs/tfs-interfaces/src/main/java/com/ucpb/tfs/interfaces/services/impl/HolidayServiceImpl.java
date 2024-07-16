package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.domain.CalendarDay;
import com.ucpb.tfs.interfaces.repositories.HolidayRepository;
import com.ucpb.tfs.interfaces.services.HolidayService;
import com.ucpb.tfs.interfaces.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  (revision)
    [Revised by:] Cedrick Nungay
    [Date revised:] 1/24/2024
    Program [Revision] Details: Updated isHolidayOrIsNotABusinessDay, changes condition when
        no record has been retrieved the method will return false, thus the date is not a holiday
 */
public class HolidayServiceImpl implements HolidayService {

    private static final String HOLIDAY_DATE_FORMAT = "MMddyy";

    private HolidayRepository holidayRepository;

    @Override
    public Boolean isHolidayOrIsNotABusinessDay(Date date, String branchCode) {
        int dateInt = DateUtil.formatToInt(HOLIDAY_DATE_FORMAT,date);
        CalendarDay day = holidayRepository.getCalendarDay(dateInt, branchCode);
        return day != null ? Boolean.valueOf(day.isHolidayOrIsNotABusinessDay()) : false; 
    }

    @Override
    public void generateHolidays(Date date) {
        holidayRepository.generateHolidays(date);
    }

    public void setHolidayRepository(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }
}