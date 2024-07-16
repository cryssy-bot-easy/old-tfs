package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.services.exception.LoanAlreadyReleasedException;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class LoanServiceImplIntegrationTest {

    @Autowired
    @Qualifier("loanService")
    private LoanServiceImpl loanService;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    @Autowired
    @Qualifier("tfsJdbcTemplate")
    private JdbcTemplate tfsJdbcTemplate;

    @Before
    @After
    public void cleanup() {
        jdbcTemplate.execute("DELETE FROM UCDATULNS2.LNTFINT");
        jdbcTemplate.execute("DELETE FROM UCDATULNS2.LNTFEXP");
        tfsJdbcTemplate.execute("DELETE FROM SIBS_SEQUENCES");
        tfsJdbcTemplate.execute("INSERT INTO SIBS_SEQUENCES (SEQUENCE,DATE_INITIALIZED,SEQUENCE_TYPE) VALUES (1,CURRENT_TIMESTAMP,'LOAN')");
    }




    @Test
    public void successfullyInsertALoan() {
        Loan loan = generateDefaultLoanObject();
        assertTrue(loanService.insertLoan(loan,false) > 0);
        assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT"));
    }

    @Test
    public void successfullyInsertMultipleLoans(){
        Loan firstLoan = generateDefaultLoanObject();
        assertEquals(1L,loanService.insertLoan(firstLoan,false));

        Loan secondLoan = generateDefaultLoanObject();
        assertEquals(2L,loanService.insertLoan(secondLoan,false));

        Loan thirdLoan = generateDefaultLoanObject();
        assertEquals(3L,loanService.insertLoan(thirdLoan,false));
    }

    @Test
    public void successfullyQueryLoan() {
//        successfullyInsertALoan();
        jdbcTemplate.update("INSERT INTO UCDATULNS2.LNTFINT (AANO,FCODE,FSEQ,TRSEQ,ACCTNO,ACTYPE,\"BR#\",\"RBR#\",TYPE,CURTYP,SNAME,CIFNO,TERM,TMCODE,ORGAMT,ORGDT6,RATE,PMTAMT,CFPDT,DRLIMT,MATDT6,OFFCR,FREQ,FRCODE,IPFREQ,IPCODE,GLBOOK,\"GROUP\",TNUMBR,TIMPOR,TAMTOR,TEXP6,TRSTS,TRUNLINK,TRUSERID,CRDTCD,PMTCOD) VALUES ('M049001             ','FCN',1,1,0,' ',930,909,'  ','PHP ','                    ','M049001',30,'D',13131411.00,112812,0.310000,0.00,' ',13131411.00,122812,'   ',30,'D',30,'D',' ',180,'999991121','                              ',0.00,0,' ','N','lmejos    ',0,0)");
        assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT WHERE TRSEQ = 1 AND TNUMBR = '999991121'"));
        Map<String, Object> loan = loanService.getLoanDetails("99999-1121");
        assertNotNull(loan);
    }

    @Test
    public void successfullyQueryLoanObject(){
        {
//        successfullyInsertALoan();
            jdbcTemplate.update("INSERT INTO UCDATULNS2.LNTFINT (AANO,FCODE,FSEQ,TRSEQ,ACCTNO,ACTYPE,\"BR#\",\"RBR#\",TYPE,CURTYP,SNAME,CIFNO,TERM,TMCODE,ORGAMT,ORGDT6,RATE,PMTAMT,CFPDT,DRLIMT,MATDT6,OFFCR,FREQ,FRCODE,IPFREQ,IPCODE,GLBOOK,\"GROUP\",TNUMBR,TIMPOR,TAMTOR,TEXP6,TRSTS,TRUNLINK,TRUSERID,CRDTCD,PMTCOD) VALUES ('M049001','FCN',1,1,0,' ',930,909,'  ','PHP ','                    ','M049001',30,'D',13131411.00,112812,0.310000,0.00,' ',13131411.00,122812,'   ',30,'D',30,'D',' ',180,'999991121','                              ',0.00,0,' ','N','lmejos    ',0,0)");
            Loan loan = loanService.getLoan(0);
            assertEquals("M049001", StringUtils.trim(loan.getMainCifNumber()));
            assertNotNull(loan);
        }
    }

    @Test
    public void successfullyGetLoanErrors(){
        jdbcTemplate.update("INSERT INTO \"UCDATULNS2\".\"LNTFEXP\" (AANO,FCODE,FSEQ,TRSEQ,RECSEQ,ERRFLD,ERRDSC,MDAT6,MDAT7,MTIME,USRID) VALUES ('CIFNUMBER','FFT',7,1,1 /*not nullable*/,'12','YOUR LOAN IS IMPOSSIBLE' /*not nullable*/,0 /*not nullable*/,0 /*not nullable*/,0 /*not nullable*/,'s' /*not nullable*/)");

        List<Map<String,Object>> errors = loanService.getLoanErrorRecord(1L);
        assertFalse(errors.isEmpty());
        Map<String,Object> error = errors.get(0);
        assertEquals("CIFNUMBER",StringUtils.trim((String)error.get("AANO")));
        assertEquals("YOUR LOAN IS IMPOSSIBLE",StringUtils.trim((String)error.get("ERRDSC")));

    }

    @Test
    public void successfullyReverseLoan() throws LoanAlreadyReleasedException, NonExistentLoanException {
        jdbcTemplate.update("INSERT INTO UCDATULNS2.LNTFINT (AANO,FCODE,FSEQ,TRSEQ,ACCTNO,ACTYPE,\"BR#\",\"RBR#\",TYPE,CURTYP,SNAME,CIFNO,TERM,TMCODE,ORGAMT,ORGDT6,RATE,PMTAMT,CFPDT,DRLIMT,MATDT6,OFFCR,FREQ,FRCODE,IPFREQ,IPCODE,GLBOOK,\"GROUP\",TNUMBR,TIMPOR,TAMTOR,TEXP6,TRSTS,TRUNLINK,TRUSERID,CRDTCD,PMTCOD) VALUES ('M049001','FCN',1,0,0,' ',930,909,'  ','PHP ','                    ','M049001',30,'D',13131411.00,112812,0.310000,0.00,' ',13131411.00,122812,'   ',30,'D',30,'D',' ',180,'999991121','                              ',0.00,0,' ','N','lmejos    ',0,0)");
        assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT WHERE TRSEQ = 0 AND TNUMBR = '999991121'"));
        long result = loanService.reverseLoan(0L,"user");
        assertEquals(1L,result);
        assertEquals(2, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT"));
        assertEquals(2L,loanService.reverseLoan(0L,"user"));

    }

    @Test
    public void successfullySendMultipleLoanReversalRequests() throws LoanAlreadyReleasedException, NonExistentLoanException {
        jdbcTemplate.update("INSERT INTO UCDATULNS2.LNTFINT (AANO,FCODE,FSEQ,TRSEQ,ACCTNO,ACTYPE,\"BR#\",\"RBR#\",TYPE,CURTYP,SNAME,CIFNO,TERM,TMCODE,ORGAMT,ORGDT6,RATE,PMTAMT,CFPDT,DRLIMT,MATDT6,OFFCR,FREQ,FRCODE,IPFREQ,IPCODE,GLBOOK,\"GROUP\",TNUMBR,TIMPOR,TAMTOR,TEXP6,TRSTS,TRUNLINK,TRUSERID,CRDTCD,PMTCOD) VALUES ('M049001','FCN',1,0,0,' ',930,909,'  ','PHP ','                    ','M049001',30,'D',13131411.00,112812,0.310000,0.00,' ',13131411.00,122812,'   ',30,'D',30,'D',' ',180,'999991121','                              ',0.00,0,' ','N','lmejos    ',0,0)");
        assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT WHERE TRSEQ = 0 AND TNUMBR = '999991121'"));
        long result = loanService.reverseLoan(0L,"user");
        assertEquals(1L,result);
        assertEquals(2, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT"));
        assertEquals(2L,loanService.reverseLoan(0L,"user"));
    }

    @Test
    public void insertAndReverseLoan() throws LoanAlreadyReleasedException, NonExistentLoanException {
        assertEquals(1,loanService.insertLoan(generateDefaultLoanObject(),false));
        assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT"));
        jdbcTemplate.update("UPDATE UCDATULNS2.LNTFINT SET ACCTNO = 343153215, TRSTS = 'Y'");
        assertEquals(2,loanService.reverseLoan(343153215L,"user"));
    }


    private Loan generateDefaultLoanObject(){
        Loan loan = new Loan();
        loan.setFacilityId(Integer.valueOf(1));
        loan.setFacilityCode("FCN");
        loan.setBranchNumber(Integer.valueOf(123));
        loan.setReportingBranch(Integer.valueOf(321));
        loan.setCurrencyType("PHP");
        loan.setDocumentNumber("99999-1121".replace("-", ""));
        loan.setInterestRate(new BigDecimal("31").divide(new BigDecimal("100")));
        loan.setIntPaymentFrequency(Integer.valueOf(30));
        loan.setIntPaymentFrequencyCode("D");
        loan.setLoanTerm(Integer.valueOf(30));
        loan.setLoanTermCode("D");
        //loan type -- not needed
        loan.setMainCifNumber("cifnum");
        loan.setCifNumber("cifnum");
        loan.setMaturityDate(121212);

        //original balance and drawing limit should be the same
        loan.setOriginalBalance(new BigDecimal(12));
        loan.setDrawingLimit(new BigDecimal(131));
        loan.setOriginalLoanDate(121212);
        loan.setPaymentFrequency(Integer.valueOf(30));
        loan.setPaymentFrequencyCode("D");
        loan.setTrustUserId("userId");
        loan.setUnlinkFlag("N");
        // Use DocumentNumber as the referenceNumber for loans

        loan.setGroupCode(180);
        loan.setTransactionStatus(" ");
        loan.setCreditorCode(0L);

        return loan;
    }


}
