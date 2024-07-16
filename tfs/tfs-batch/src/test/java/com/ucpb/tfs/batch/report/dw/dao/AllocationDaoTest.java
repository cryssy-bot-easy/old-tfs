package com.ucpb.tfs.batch.report.dw.dao;

import com.ucpb.tfs.batch.report.dw.AllocationFileRecord;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:repository-test-context.xml")
public class AllocationDaoTest {

    @Autowired
    private AllocationDao allocationDao;


    @Ignore
    @Test
    public void getIncomeMovement(){
        List<AllocationFileRecord> records = allocationDao.getAllocations(new Date());
        assertNotNull(records);
        assertFalse(records.isEmpty());
        assertEquals(2,records.size());
    }

    @Test
    public void getAllActiveDocumentsAgainstAcceptance(){
        List<AllocationFileRecord> records = allocationDao.getValidDocumentsAgainstAcceptance(new Date());
        assertNotNull(records);
//        assertFalse(records.isEmpty());
//        assertEquals(2,records.size());
    }

}
