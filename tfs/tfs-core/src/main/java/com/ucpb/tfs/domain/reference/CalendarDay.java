package com.ucpb.tfs.domain.reference;

import java.util.Date;

/**
 */
public class CalendarDay {

    private String description;

    private int day;

    private int year;

    private Date date;

    private boolean isHoliday;

    private boolean isWorkingDay;

    public CalendarDay() {

    }

    public CalendarDay(String description, int day, int year) {
        this.description = description;
        this.day = day;
        this.year = year;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setHoliday(String holiday) {
        isHoliday = "Y".equalsIgnoreCase(holiday) ;
    }

    public boolean isWorkingDay() {
        return isWorkingDay;
    }

    public void setWorkingDay(String workingDay) {
        isWorkingDay = "Y".equalsIgnoreCase(workingDay);
    }
}
