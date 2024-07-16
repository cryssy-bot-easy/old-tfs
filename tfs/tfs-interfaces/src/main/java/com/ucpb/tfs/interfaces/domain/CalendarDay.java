package com.ucpb.tfs.interfaces.domain;

/**
 */
public class CalendarDay {

    private int date;

    private String holidayFlag;

    private String businessDayFlag;

    private String unitCode;


    public boolean isHoliday(){
        return "Y".equalsIgnoreCase(holidayFlag);
    }

    public boolean isNotABusinessDay(){
        return !"Y".equalsIgnoreCase(businessDayFlag);
    }

    public boolean isHolidayOrIsNotABusinessDay(){
        return isHoliday() || isNotABusinessDay();
    }

    public String getBusinessDayFlag() {
        return businessDayFlag;
    }

    public void setBusinessDayFlag(String businessDayFlag) {
        this.businessDayFlag = businessDayFlag;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getHolidayFlag() {
        return holidayFlag;
    }

    public void setHolidayFlag(String holidayFlag) {
        this.holidayFlag = holidayFlag;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
}
