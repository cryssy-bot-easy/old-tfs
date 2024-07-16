package com.ucpb.tfs.application.query.interfaces;

import java.util.Date;

/**
 */
public interface HolidayService {


    public boolean isHolidayOrIsNotABusinessDay(Date date, String branchCode);


}
