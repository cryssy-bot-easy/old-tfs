package com.ucpb.tfs.interfaces.repositories;

import com.ucpb.tfs.interfaces.domain.CalendarDay;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 */
public interface HolidayRepository {

    public CalendarDay getCalendarDay(@Param("date") int date,@Param("branchCode") String branchCode);

    public void generateHolidays(@Param("date") Date date);

}
