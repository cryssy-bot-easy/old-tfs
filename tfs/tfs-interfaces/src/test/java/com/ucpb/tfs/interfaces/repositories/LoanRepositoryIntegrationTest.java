package com.ucpb.tfs.interfaces.repositories;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.domain.Loan;


/**
 * This is a convenience class that is used to directly insert a loan request
 * to the silverlake db. THIS IS NOT A TEST CASE. DO NOT RUN THIS AS PART OF
 * THE BUILD
 */
@Ignore("DO NOT RUN THIS AS PART OF THE BUILD")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-integration-test-context.xml")
public class LoanRepositoryIntegrationTest {
	
	private static final int SEQUENCE_NO = 90870;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private LoanRepository loanRepository;
	
	
	@Test
	public void successfullyInsertLoan(){
		assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT WHERE TRSEQ = " + SEQUENCE_NO));

		Loan loan = new Loan();
		loan.setMainCifNumber("A047688");
		loan.setFacilityCode("FBF");
		loan.setFacilityId(5);
		loan.setTransactionSequenceNumber(SEQUENCE_NO);
		loan.setAccountType(" "); //pass space
		loan.setBranchNumber(929);
		loan.setReportingBranch(909);
		loan.setLoanType(" ");
		loan.setCurrencyType("USD");
		loan.setShortName(" "); //pass space
		loan.setCifNumber("A047688");
		loan.setLoanTerm(30);
		loan.setLoanTermCode("D");
		loan.setOriginalBalance(new BigDecimal("1212"));
		loan.setOriginalLoanDate(110812);
		loan.setInterestRate(new BigDecimal("5"));
		loan.setPaymentAmount(new BigDecimal("0")); //should be zero
		loan.setDrawingLimit(new BigDecimal("1212")); // should be the same as the original amount(balance)
		loan.setMaturityDate(121212);
		loan.setOfficer(" "); //pass space
		loan.setPaymentFrequency(30);
		loan.setPaymentFrequencyCode("D");
		loan.setIntPaymentFrequency(30);
		loan.setIntPaymentFrequencyCode("D");
		loan.setGlBook(" "); //pass space
		loan.setGroupCode(180);
		loan.setDocumentNumber("documentnumber");
		//importer -- pass space
		
		loan.setOrderAmount(BigDecimal.ZERO); // Order Amount is always zero
		loan.setOrderExpiryDate(0); //order expiry date is always 0
		loan.setTransactionStatus(" "); //pass blank
		loan.setUnlinkFlag(" ");
		loan.setTrustUserId("tuid");
		loan.setCreditorCode(0L);
		loan.setPaymentCode(0); //ask if this field should also always be zero

		loanRepository.insertLoan(loan,"");
		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATULNS2.LNTFINT WHERE TRSEQ = " + SEQUENCE_NO));
	
		Map<String,Object> record = jdbcTemplate.queryForMap("SELECT * FROM UCDATULNS2.LNTFINT WHERE TRSEQ = " + SEQUENCE_NO);
		assertEquals(" ",record.get("TRSTS"));
	}
	
}
