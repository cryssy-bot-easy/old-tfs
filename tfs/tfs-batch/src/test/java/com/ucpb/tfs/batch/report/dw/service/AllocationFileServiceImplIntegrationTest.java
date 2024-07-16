package com.ucpb.tfs.batch.report.dw.service;

import com.ucpb.tfs.batch.report.dw.AllocationFileRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:test-context.xml")
public class AllocationFileServiceImplIntegrationTest {

    @Autowired
    private AllocationFileService allocationFileService;


    @Test
    public void successfullyGetAllAllocations(){
        List<AllocationFileRecord> records = allocationFileService.getProductAllocations(new Date(),"561501030000","10903");
        assertNotNull(records);
        assertFalse(records.isEmpty());

        assertEquals(4,records.size());
        assertEquals("D039586",records.get(0).getCustomerId());
        assertEquals("S040447",records.get(2).getCustomerId());
        assertEquals(new BigDecimal("33333333"),records.get(0).getOriginalTransactionAmount());
        assertEquals(new BigDecimal("-33333333"),records.get(1).getOriginalTransactionAmount());
        assertEquals(new BigDecimal("222222222"),records.get(2).getOriginalTransactionAmount());
        assertEquals(new BigDecimal("-222222222"),records.get(3).getOriginalTransactionAmount());


    }



}
