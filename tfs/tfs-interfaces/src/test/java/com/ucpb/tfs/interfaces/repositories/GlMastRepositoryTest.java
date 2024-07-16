package com.ucpb.tfs.interfaces.repositories;

import com.ucpb.tfs.interfaces.domain.Availment;
import com.ucpb.tfs.interfaces.domain.Facility;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class GlMastRepositoryTest {

	@Autowired
	private GlMastRepository glMastRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	//July 27, 2012
	private static final long date = 1343373194517L;
	
	@Before
	public void teardown(){
		jdbcTemplate.execute("DELETE FROM UCDATUGLD2.GLMAST");
    }
	
	
	@Test
	public void successfullyGetAllEntries(){
	
		jdbcTemplate.execute("INSERT INTO UCDATUGLD2.GLMAST (BRANCH,BOOKCD,ACCTNO,MRRACC,GMCTYP,SHORTT,TITLE,ACTYPE,GLMRVN,VALPST) VALUES (900,'RG',110110101200,0,'PHP ','COH - IN TRANSIT    ','CASH IN TRANSIT                         ','A','*NO ','*POST   ')" );
		jdbcTemplate.execute("INSERT INTO UCDATUGLD2.GLMAST (BRANCH,BOOKCD,ACCTNO,MRRACC,GMCTYP,SHORTT,TITLE,ACTYPE,GLMRVN,VALPST) VALUES (900,'FC',110110202100,0,'USD ','FX NOTES & COH-OT   ','FOREIGN NOTES & COINS ON HAND-OTHERS    ','A','*NO ','*POST   ')" );



		//72612 - july 26, 2012
		List<Map<String,?>> entries = glMastRepository.getAllEntries();
		assertNotNull(entries);
		assertEquals(2,entries.size());
	}

    @Test
    public void successfullyGetEntryByAcctCodeAndCurrency(){

        jdbcTemplate.execute("INSERT INTO UCDATUGLD2.GLMAST (BRANCH,BOOKCD,ACCTNO,MRRACC,GMCTYP,SHORTT,TITLE,ACTYPE,GLMRVN,VALPST) VALUES (900,'RG',110110101200,0,'PHP ','COH - IN TRANSIT    ','CASH IN TRANSIT                         ','A','*NO ','*POST   ')" );
        jdbcTemplate.execute("INSERT INTO UCDATUGLD2.GLMAST (BRANCH,BOOKCD,ACCTNO,MRRACC,GMCTYP,SHORTT,TITLE,ACTYPE,GLMRVN,VALPST) VALUES (900,'FC',110110202100,0,'USD ','FX NOTES & COH-OT   ','FOREIGN NOTES & COINS ON HAND-OTHERS    ','A','*NO ','*POST   ')" );



        //72612 - july 26, 2012
        List<Map<String,?>> entries = glMastRepository.getEntries("PHP","110110101200");
        assertNotNull(entries);
        assertEquals(1,entries.size());
    }

//    @Test
//    public void getLoanFacilities(){
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FE2',1,72615,'PHP',90000)" );
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','F3Z',2,72615,'PHP',23032)" );
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','F3S',3,72615,'PHP',90000)" );
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',4,72615,'PHP',23032)" );
//
//        List<Map<String,?>> facilities = facilityRepository.getFacilitiesForLoan("1234567", "072612");
//        assertNotNull(facilities);
//        assertEquals(3,facilities.size());
//    }
//
//	@Test
//	public void successfullyGetAllFacilitiesOfACifOfASpecificType(){
//
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,73013,'PHP',1000000)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','TRP',1,73013,'PHP',12345)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','TRP',1,73013,'PHP',90000)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,73013,'PHP',23032)" );
//
//
//		//72612 - july 26, 2012
//		List<Map<String,?>> facilities = facilityRepository.getFacilitiesByCifNumberAndType("1234567", "072612","TRP");
//		assertNotNull(facilities);
//		assertEquals(2,facilities.size());
//	}
//
//    @Ignore
//	@Test
//	public void doNotRetrieveExpiredFacilities(){
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72510,'PHP',1000000)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72510,'PHP',12345)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72510,'PHP',90000)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72510,'PHP',23032)" );
//
//		//72612 - july 26, 2012
//		List<Map<String,?>> facilities = facilityRepository.getFacilitiesByCifNumber("1234567", "072612");
//		assertNotNull(facilities);
//		assertTrue(facilities.isEmpty());
//	}
//
//	@Test
//	public void doNotRetrieveFacilitiesOwnedByADifferentCif(){
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72513,'PHP',1000000)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72513,'PHP',12345)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('9999999','FCN',1,72513,'PHP',90000)" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72513,'PHP',23032)" );
//
//		//72612 - july 26, 2012
//		List<Map<String,?>> facilities = facilityRepository.getFacilitiesByCifNumber("1234567", "072612");
//		assertNotNull(facilities);
//		assertEquals(3,facilities.size());
//	}
//
//	@Test
//	public void getCorrectFacilityAvailmentCount(){
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber1')" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber2')" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber3')" );
//
//		//72612 - july 26, 2012
//		assertEquals(1,facilityRepository.getFacilityAvailmentCount("DocumentNumber1"));
//	}
//
//    @Test
//    public void successfullyGetAvailment(){
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber1')");
//        Availment availment = facilityRepository.getAvailment("DocumentNumber1");
//        assertNotNull(availment.getDocumentNumber());
//
//    }
//
//    @Test
//    public void failToRetrieveNonExistingAvailment(){
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber1')" );
//        Availment availment = facilityRepository.getAvailment("kwjdakldjklaj");
//        assertNull(availment);
//    }
//
//	@Test
//	public void successfullyInsertFacilityAvailment(){
//		assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNCLST"));
//		facilityRepository.insertFacilityAvailment(buildAvailment("DocumentNumber1", "1234567", BigDecimal.valueOf(100)));
//		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNCLST"));
//	}
//
//    @Test
//    public void successfullyUpdateTheBalance(){
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO,TOSBAL) VALUES ('DocumentNumber1',100)" );
//        Availment availment = new Availment();
//        availment.setOutstandingBalance(new BigDecimal("38"));
//        availment.setDocumentNumber("DocumentNumber1");
//        availment.setStatusDescription("O");
//        assertEquals(1,facilityRepository.updateFacilityAvailmentBalance(availment));
//        Map<String,Object> row = jdbcTemplate.queryForMap("SELECT * FROM UCDATULNS2.LNCLST WHERE ACCTNO = 'DocumentNumber1'");
//        assertEquals(new BigDecimal("38.00"),row.get("TOSBAL"));
//    }
//
//
//	@Test
//	public void successfullyDeleteFacilityAvailment(){
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber1')" );
//		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNCLST"));
//		assertEquals(1,facilityRepository.deleteFacilityAvailment("DocumentNumber1"));
//		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNCLST WHERE ACCTNO = 'DocumentNumber1' AND STADSC = 'CANCELLED'"));
//	}
//
//	@Test
//	public void failToDeleteBecauseOfWrongDocumentNumber(){
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber1')" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber2')" );
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber3')" );
//
//		assertEquals(3, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNCLST"));
//		assertEquals(0,facilityRepository.deleteFacilityAvailment("DocumentNumber0"));
//	}
//
//	@Test
//	public void successfullyUpdateFacilityAvailment(){
//		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNCLST (ACCTNO) VALUES ('DocumentNumber1')" );
//		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNCLST"));
//		assertEquals(1,facilityRepository.updateFacilityAvailment(buildAvailment("DocumentNumber1", "1234567",BigDecimal.valueOf(9999))));
//		Map<String,Object> results = jdbcTemplate.queryForMap("SELECT * FROM UCDATULNS2.LNCLST WHERE ACCTNO = 'DocumentNumber1'");
//		assertEquals("DocumentNumber1", StringUtils.trim((String) results.get("ACCTNO")));
//		assertEquals("1234567",results.get("CIFNO"));
//
//	}
//
//
//    @Test
//    public void successfullyRetrieveFacilityBalanceRequest(){
//        jdbcTemplate.update("INSERT INTO UCDATULNS2.LNTFCON (AANO,FCODE,FSEQ,TRSEQ,AVLAMT,TOCUR,TRSTS, TRERR) VALUES " +
//                "('CIFNUMBER','TYP',1,12345,25000000,'R23A','Y','Successful bro!')");
//
//        Map<String,?> facilityResponse = facilityRepository.getFacilityBalance(12345);
//        assertNotNull(facilityResponse);
//    }
//
//    @Test
//    public void getAllFacilitiesOfSpecifiedTypes(){
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FFT',1,72615,'PHP',1000000)");
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FCN',1,72615,'PHP',12345)" );
//        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (\"AFCIF#\",AFFCDE,AFSEQ,AFEXP6,AFCUR,AFFAMT) VALUES ('1234567','FTF',1,72615,'PHP',90000)" );
//
//        List<Map<String,?>> facilities = facilityRepository.getFacilitiesByType("1234567","FFT","FTF");
//        assertEquals(2,facilities.size());
//        Map<String,?> firstRow = facilities.get(0);
//        assertEquals("1234567", firstRow.get("CLIENT_CIF_NUMBER"));
//        assertEquals("FFT",firstRow.get("FACILITY_TYPE"));
//
//    }
//
//    @Test
//    public void successfullyInsertFacilityBalanceQuery(){
//    	assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFCON"));
//
//    	Facility facility = new Facility();
//    	facility.setCifNumber("123456");
//    	facility.setFacilityType("FCN");
//    	facility.setCurrency("PHP");
//    	facility.setFacilityId(123);
//
//    	facilityRepository.insertFacilityBalanceQuery(facility, 1);
//
//    	Map<String,Object> availment = jdbcTemplate.queryForMap("SELECT * FROM UCDATULNS2.LNTFCON");
//    	assertNotNull(availment);
//    	assertEquals("123456",StringUtils.trim((String) availment.get("AANO")));
//    	assertEquals("FCN",availment.get("FCODE"));
//    	assertEquals(BigDecimal.valueOf(1),availment.get("TRSEQ"));
//    	assertEquals(new BigDecimal("0.00"),availment.get("AVLAMT"));
//    	assertEquals(BigDecimal.valueOf(123),availment.get("FSEQ"));
//    }
//
//    private Availment buildAvailment(String documentNumber, String cifNumber, BigDecimal amount){
//		Availment availment = new Availment();
//		availment.setCifNumber("1234567");
//		availment.setDocumentNumber("DocumentNumber1");
//		availment.setOriginalAmount(amount);
//		availment.setPhpAmount(amount);
//		availment.setProductCode("TF");
////		availment.setTransactionDate(new Date(date));
//		availment.setOutstandingBalance(amount);
//		availment.setCurrencyCode("PHP");
//		availment.setAssetLiabilityFlag("A");
//		availment.setStatusDescription("current");
//
//		//afcpno
//		availment.setFacilityReferenceNumber("7654321");
//
//		return availment;
//	}
}
