package com.ucpb.tfs.interfaces.repositories;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.domain.Availment;
import com.ucpb.tfs.interfaces.domain.Facility;

//Configured to connect to the UCPB database
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-integration-test-context.xml")
public class FacilityRepositoryIntegrationTest {

	@Autowired
	private FacilityRepository facilityRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Ignore("Schema for contingent availments is still unknown.")
	@Test
	public void successfullyInsertBalanceQuery() throws ParseException{
	 	Facility facility = new Facility();
    	facility.setCifNumber("123456");
    	facility.setFacilityType("TYPE");
    	facility.setCurrency("PHP");
    	facility.setFacilityId(123);
		
		facilityRepository.insertFacilityBalanceQuery(facility, Long.valueOf(123));
		
	}
	
	
	@Test
	public void successfullyInsertFacilityAvailment(){
		
		Availment availment = new Availment();
		availment.setCifNumber("1234567");
		availment.setDocumentNumber("document-number-1");
		availment.setOriginalAmount(BigDecimal.valueOf(99999));
		availment.setOutstandingBalance(BigDecimal.valueOf(99999));
		availment.setProductCode("GG");
//		availment.setTransactionDate(new Date());
		availment.setPhpAmount(BigDecimal.valueOf(3000000));
		availment.setPhpOutstandingBalance(BigDecimal.valueOf(3000000));
		availment.setCurrencyCode("CURR");
		availment.setAssetLiabilityFlag("A");
		availment.setStatusDescription("CURRENT");
		availment.setFacilityReferenceNumber("Facility REFNUMBER");
		
		facilityRepository.insertFacilityAvailment(availment);
		assertTrue(jdbcTemplate.queryForInt(("SELECT COUNT(*) FROM UCDATULNS1.LNCLST  WHERE ACCTNO = 'document-number-1'")) > 0);
		
	}
	
	
}
