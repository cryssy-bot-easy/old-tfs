package com.ucpb.tfs.interfaces.services;

import java.util.Date;
/**
 */
public interface HolidayService {


    public Boolean isHolidayOrIsNotABusinessDay(Date date, String branchCode);


    public void generateHolidays(Date date);

}
