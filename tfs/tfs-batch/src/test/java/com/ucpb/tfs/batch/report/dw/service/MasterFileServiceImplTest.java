package com.ucpb.tfs.batch.report.dw.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.batch.report.dw.MasterFileRecord;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:repository-test-context.xml")
public class MasterFileServiceImplTest {

	@Autowired
	@Qualifier("masterfileService")
	private MasterFileServiceImpl masterFileServiceImpl;
	
	
	@Test
	public void getAllActiveMasterFiles(){
//		List<MasterFileRecord> records = masterFileServiceImpl.getMasterFiles();
//		assertNotNull(records);
//		assertFalse(records.isEmpty());
//		System.out.println("************ TOTAL MASTER FILE RECORDS: " + records.size());
//
//		for(MasterFileRecord record : records){
//			System.out.println("DOCUMENT NUMBER " +record.getApplicationAccountId());
//		}
		
	}
	
}
