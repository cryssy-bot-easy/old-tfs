package com.ucpb.tfs.batch.report.dw;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 */
public class AllocationFileRecordTest {


    @Test
    public void getActiveDaysWithNullEndDate(){
        AllocationFileRecord allocationFileRecord = new AllocationFileRecord();
        allocationFileRecord.setOpenDate(new Date());
        assertEquals(0,allocationFileRecord.activeDays());
    }

    @Test
    public void getActiveDaysWithNextDayEndDate(){
        AllocationFileRecord allocationFileRecord = new AllocationFileRecord();
        allocationFileRecord.setOpenDate(new Date());

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE,1);
        allocationFileRecord.setDateClosed(tomorrow.getTime());

        assertEquals(1,allocationFileRecord.activeDays());
    }

    @Test
    public void getAdbAmountWithNullDateClosed(){
        AllocationFileRecord allocationFileRecord = new AllocationFileRecord();
        allocationFileRecord.setOpenDate(new Date());
        allocationFileRecord.setTotalAmount(new BigDecimal("123"));
        allocationFileRecord.setAdbFlag("Y");

        assertEquals(new BigDecimal("123"), allocationFileRecord.getAdbAmount());
    }

    @Test
    public void returnZeroAdbAmountIfAdbFlagIsNull(){
        AllocationFileRecord allocationFileRecord = new AllocationFileRecord();
        allocationFileRecord.setAdbFlag(null);
        allocationFileRecord.setTotalAmount(new BigDecimal("123"));
        allocationFileRecord.setOpenDate(new Date());


        assertEquals(BigDecimal.ZERO,allocationFileRecord.getAdbAmount());
    }


}
