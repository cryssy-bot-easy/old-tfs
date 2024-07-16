package com.ucpb.tfs.interfaces.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.repositories.LoanRepository;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class LoanRepositoryTest {

	@Autowired
	@Qualifier("loanRepository")
	private LoanRepository loanRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@After
	public void teardown(){
		jdbcTemplate.execute("DELETE FROM UCDATULNS2.LNTFINT");
		jdbcTemplate.execute("DELETE FROM UCDATULNS2.LNTFEXP");
	}
	
	@Test
	public void successfullyInsertALoan(){
		assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT")); 
		Loan loan = new Loan();
		loan.setMainCifNumber("1234567");
		loan.setFacilityCode("123");
		loan.setFacilityId(1);
		loan.setTransactionSequenceNumber(1L);
		loan.setPnNumber(1234567);
		loan.setAccountType(" ");
		loan.setBranchNumber(321);
		loan.setReportingBranch(231);
		loan.setLoanType("   ");
		loan.setCurrencyType("PHP");
		loan.setShortName("SHORT");
		loan.setCifNumber("3216547");
		loan.setLoanTerm(54321);
		loan.setLoanTermCode("1");
		loan.setInterestRate(BigDecimal.valueOf(1));
		loan.setOriginalBalance(BigDecimal.valueOf(101));
		loan.setOriginalLoanDate(121212);
		loan.setOfficer("OFF");
		loan.setPaymentFrequency(1213);
		loan.setPaymentFrequencyCode("12134");
		loan.setTrustUserId("usrIdTrust");
		loan.setPaymentAmount(BigDecimal.valueOf(12));
		loan.setDrawingLimit(BigDecimal.valueOf(12000));
		loan.setGlBook(" ");
		loan.setGroupCode(123);
		loan.setPaymentFrequencyCode("M");
		loan.setIntPaymentFrequencyCode("D");
		loan.setDocumentNumber("Document1");
		loan.setOrderAmount(BigDecimal.valueOf(100));
		loan.setTransactionStatus("A");
		loan.setUnlinkFlag("N");
		loan.setCreditorCode(0L);
		loan.setPaymentCode(5);
		loan.setTrustUserId("USERID");
		assertEquals(1,loanRepository.insertLoan(loan,""));
		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT"));
		
		List<Map<String,Object>> loans = jdbcTemplate.queryForList("SELECT * FROM UCDATULNS2.LNTFINT");
		for(Map<String,Object> record : loans){
			System.out.println("**** RECORD: ");
			for(Entry<String, Object> entry : record.entrySet()){
				System.out.println(entry.getKey() + " : " + entry.getValue() );
			}
		}
	}

//    @Ignore
    @Test
    public void loanIntegrationTest(){
        Loan loan = new Loan();

        loan.setCurrencyType("PHP");
        loan.setFacilityCode("123");  //not yet implemented
        loan.setFacilityId(1234);     //facility id not yet implemented
        loan.setDocumentNumber("1");    //not yet please
        loan.setInterestRate(new BigDecimal("1"));
        loan.setIntPaymentFrequency(Integer.valueOf(12));
        loan.setIntPaymentFrequencyCode("1");
        loan.setLoanTerm(Integer.valueOf(1));
        loan.setReportingBranch(909);
        loan.setLoanTermCode("1");

        //loan type -- not needed
        //TODO: confirm if mainCifNumber is being passed
//        loan.setMainCifNumber(param.getString("mainCifNumber"));
        loan.setMainCifNumber("1");            //not yet implemented
      //add cif number
        loan.setCifNumber("1");    //not yet implemented
        loan.setMaturityDate(1);   //not yet implemented
        //3 character officer code
        //save officer code
//        loan.setOfficer(param.getString("accountOfficer"));
        loan.setOfficer("123"); //not yet implemented
//        loan.setOriginalBalance();   -- the amount of the nego (in php)
        loan.setOriginalLoanDate(1); //put in new date?
//        loan.setPaymentFrequency(param.getAsInteger("paymentTerm"));
        loan.setPaymentFrequencyCode("1");
        loan.setTrustUserId("1"); //pull from user Active directory Id
        loan.setUnlinkFlag("N");
        loan.setOriginalBalance(new BigDecimal("1"));
        loan.setCreditorCode(1L);
        loan.setMaturityDate(1);
        loan.setPaymentFrequency(12);
        loan.setPaymentFrequencyCode("1");
        loan.setIntPaymentFrequency(12);
        loan.setIntPaymentFrequencyCode("1");
        loan.setGroupCode(123);  //group code
        loan.setDocumentNumber("1213");
        loan.setTrustUserId("1212");
        loan.setCreditorCode(1213L);
        loan.setPaymentCode(0);
        //loan maturity date
        // Use DocumentNumber as the referenceNumber for loans
        //
        //loan.setCreditorCode(); -- if UA (retrieve the BSP Creditor Code based on reimbursing bank code


        loan.setDrawingLimit(BigDecimal.valueOf(1));
        loan.setPnNumber(0);
        loan.setOrderAmount(BigDecimal.valueOf(1));
        loan.setTransactionStatus("A");
        loan.setTransactionSequenceNumber(1); //insert sequence number
        loan.setBranchNumber(171); //set branch number
        loan.setReportingBranch(909); //set reporting branch
        loan.setDrawingLimit(new BigDecimal(1));


        assertEquals(1L, loanRepository.insertLoan(loan,""));
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT"));
    }

//    @Ignore
	@Test
	public void successfullyRetrieveLoanUsingLoanNumber(){
		StringBuilder sql = new StringBuilder();
//		sql.append("INSERT INTO UCDATULNS2.LNTFINT (AANO,FCODE,FSEQ,TRSEQ,ACCTNO,ACTYPE,\"BR#\",\"RBR#\",TYPE,CURTYP,");
//		sql.append("SNAME,CIFNO,TERM,TMCODE,ORGAMT,ORGDT6,RATE,PMTAMT,CFPDT,DRLIMT,MATDT6,OFFCR,FREQ,FRCODE,IPFREQ,");
//		sql.append("IPCODE,GLBOOK,\"GROUP\",TNUMBR,TIMPOR,TAMTOR,TEXP6,TRSTS,TRUNLINK,TRUSERID,CRDTCD,PMTCOD) VALUES ");
//		sql.append("('1234567','1',1,1,123456789,");
//		sql.append("'L',201,909,'  ','IB','My Short Name',");
//		sql.append("'7654321','121212','M',1200,121212,0.05,900000,");
//		sql.append("'1',60000,121212,'OCR',121212,'M',12121,");
//		sql.append("'D',' ','123',123456789,'12341',123000,123112,");
//		sql.append("'Y','N','userId',123456789123,9)");

        sql.append("INSERT INTO UCDATULNS2.LNTFINT (AANO,FCODE,FSEQ,TRSEQ,ACCTNO,ACTYPE,\"BR#\",\"RBR#\",TYPE,CURTYP,SNAME,CIFNO,TERM,TMCODE,ORGAMT,ORGDT6,RATE,PMTAMT,CFPDT,DRLIMT,MATDT6,OFFCR,FREQ,FRCODE,IPFREQ,IPCODE,GLBOOK,\"GROUP\",TNUMBR,TIMPOR,TAMTOR,TEXP6,TRSTS,TRUNLINK,TRUSERID,CRDTCD,PMTCOD) VALUES ('M049001','FCN',1,1213,30001,' ',930,909,'  ','PHP ','                    ','M049001',30,'D',1321414.00,112912,0.120000,0.00,' ',1321414.00,122912,'   ',6,'D',30,'D',' ',180,'9091993212405408    ','                              ',0.00,0,'Y','N','lmejos',0,0)");


		jdbcTemplate.update(sql.toString());

		Map<String,Object> record = jdbcTemplate.queryForMap("SELECT * FROM UCDATULNS2.LNTFINT");
		System.out.println("CRDTCD: " + record.get("CRDTCD"));
//
		Loan loan = loanRepository.getLoan(30001);
		assertNotNull(loan);
        assertEquals(30001L,loan.getPnNumber());
//		assertEquals("30001",loan.getDocumentNumber());
//		assertEquals("1234567",loan.getMainCifNumber());

	}

    @Test
    public void successfullyRetrieveLoanBySequenceNumber(){
        jdbcTemplate.update("INSERT INTO UCDATULNS2.LNTFINT (AANO,FCODE,FSEQ,TRSEQ,ACCTNO,ACTYPE,\"BR#\",\"RBR#\",TYPE,CURTYP,SNAME,CIFNO,TERM,TMCODE,ORGAMT,ORGDT6,RATE,PMTAMT,CFPDT,DRLIMT,MATDT6,OFFCR,FREQ,FRCODE,IPFREQ,IPCODE,GLBOOK,\"GROUP\",TNUMBR,TIMPOR,TAMTOR,TEXP6,TRSTS,TRUNLINK,TRUSERID,CRDTCD,PMTCOD) VALUES ('M049001','FCN',1,0,18282828,'L',930,909,'T','PHP ','                    ','M049001',30,'D',13131411.00,112812,0.310000,0.00,' ',13131411.00,122812,'   ',30,'D',30,'D',' ',180,'999991121','                              ',0.00,0,' ','N','lmejos    ',0,0)");

        Loan loan = loanRepository.getLoanBySequenceNumber(0L);
        assertEquals("M049001",loan.getCifNumber());
        assertEquals("FCN",loan.getFacilityCode());
        assertEquals(1,loan.getFacilityId());
        assertEquals(0,loan.getTransactionSequenceNumber());
        assertEquals(18282828L,loan.getPnNumber());
        assertEquals("L",loan.getAccountType());
        assertEquals(930,loan.getBranchNumber());
        assertEquals(909,loan.getReportingBranch());
        assertEquals("T ",loan.getLoanType());
    }
	
	@Test
	public void successfullyGetErrorCodeOfTheSpecifiedSequenceNumber(){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO UCDATULNS2.LNTFEXP (AANO,FCODE,FSEQ,TRSEQ,RECSEQ,ERRFLD,ERRDSC,MDAT6,MDAT7,MTIME,USRID)");
		sql.append(" VALUES ('MAINCIFNUM','FCD',222,12345,1,'ERRFIELD','DESC OF ERROR',121212,121212,121212,'USERID')");
		
		jdbcTemplate.update(sql.toString());
		sql = new StringBuilder();
		sql.append("INSERT INTO UCDATULNS2.LNTFEXP (AANO,FCODE,FSEQ,TRSEQ,RECSEQ,ERRFLD,ERRDSC,MDAT6,MDAT7,MTIME,USRID)");
		sql.append(" VALUES ('MAINCIFNUM2','FCD',222,54321,1,'ERRFIELD','DESC OF ERROR',121212,121212,121212,'USERID')");
		
		jdbcTemplate.update(sql.toString());
		assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFEXP"));
		
		List<Map<String,Object>> record = loanRepository.getLoanErrorRecord(54321);
		assertNotNull(record);
		assertFalse(record.isEmpty());
		assertEquals(new BigDecimal(54321),record.get(0).get("TRSEQ"));
	}
	
}
