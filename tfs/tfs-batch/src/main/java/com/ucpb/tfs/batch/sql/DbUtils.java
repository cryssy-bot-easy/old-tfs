package com.ucpb.tfs.batch.sql;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;


public class DbUtils {

	public static BigDecimal dayOfYear(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		return BigDecimal.valueOf(cal.get(Calendar.DAY_OF_YEAR));
	}

    /**
     * Returns the year component in BigDecimal of @param timestamp
     * @param timestamp
     * @return year component in BigDecimal of @param timestamp
     */
	public static BigDecimal year(Timestamp timestamp){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		return BigDecimal.valueOf(cal.get(Calendar.YEAR));
	}

    /**
     * Returns the month component in BigDecimal of @param timestamp
     * @param timestamp
     * @return month component in BigDecimal of @param timestamp
     */
	public static BigDecimal month(Timestamp timestamp){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		return BigDecimal.valueOf(cal.get(Calendar.MONTH) + 1); // January is 0
	}

    /**
     * Returns the number of days since the start of the year.
     * @param timestamp
     * @return days past since the start of the year
     */
    public static Integer days(Timestamp timestamp){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        return Integer.valueOf(cal.get(Calendar.DAY_OF_YEAR));
    }

}
