package com.ucpb.tfs.batch.report.dw.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.batch.report.dw.Appraisal;


@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:repository-test-context.xml")
public class silverlakeLocalDaoTest {

	@Autowired
	private SilverlakeLocalDao silverlakeLocalDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private JdbcTemplate sibsJdbcTemplate;
	
	@Test
	public void getValidIndustryCodeFromExistingCif(){
		assertEquals(3,sibsJdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNMAST"));
		assertEquals("751130",silverlakeLocalDao.getIndustryCode("P033211"));
		
	}
	
	@Test
	public void getEarmarkingStatusFromExistingAvailment(){
		sibsJdbcTemplate.execute("insert into ucdatulns2.lnclst (acctno,STADSC) values ('document-number1','CANCELLED')");
		sibsJdbcTemplate.execute("insert into ucdatulns2.lnclst (acctno,STADSC) values ('document-number2','ACTIVE')");
		assertEquals("CANCELLED",StringUtils.trim(silverlakeLocalDao.getEarmarkingAccountStatus("document-number1")));
	}
	
	
	@Test
	public void validAvailmentDetailsFromExistingUser(){
		Appraisal appraisal = silverlakeLocalDao.getAppraisalDetails("L031004");
		assertNotNull(appraisal);
		assertEquals(new BigDecimal("480000000.00"),appraisal.getAppraisedValue());
		System.out.println("********** appraisal date: " + appraisal.getAppraisalDate());
		assertNotNull(appraisal.getAppraisalDate());
		assertEquals("024",appraisal.getSecurityCode());
	}
	
	
	
}
