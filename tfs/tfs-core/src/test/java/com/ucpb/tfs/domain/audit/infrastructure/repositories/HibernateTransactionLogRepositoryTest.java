package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.math.BigDecimal;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.ucpb.tfs.domain.audit.TransactionLog;
import com.ucpb.tfs.util.ScriptRunner;


@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration
@Transactional
public class HibernateTransactionLogRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	@Qualifier("mySessionFactory")
	private SessionFactory sessionFactory;
	
	@Autowired
	private HibernateTransactionLogRepository auditLogRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;


	
	@Autowired
	private ScriptRunner scriptRunner;
	
	
	@Before
	public void setup(){
		jdbcTemplate.execute("DELETE FROM TRANSACTIONLOG");
	}
	
	
	@Test
	public void successfullyPersistAuditLog(){
		assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
		TransactionLog auditLog = new TransactionLog();
		auditLog.setAccountNumber("123456789");
        auditLog.setExchangeRate(new BigDecimal("42.12345678"));
		auditLogRepository.persist(auditLog);
        sessionFactory.getCurrentSession().flush();
		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG WHERE EXCHANGERATE = 42.12345678"));

    }

    @Ignore ("Removed ID property")
	@Test
	public void successfullyGetAuditLogById(){
		jdbcTemplate.execute("INSERT INTO TRANSACTIONLOG (accountNumber,txnReferenceNumber,txnDate) VALUES (9999,'12356987','TXNREFNUMBER',CURRENT_TIMESTAMP)");
		jdbcTemplate.execute("INSERT INTO TRANSACTIONLOG (accountNumber,txnReferenceNumber,txnDate) VALUES (8888,'42323232','TXNREFNUMBER2',CURRENT_TIMESTAMP)");

		assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
		TransactionLog auditLog = auditLogRepository.getAuditLog(9999L);
		assertNotNull(auditLog);
		assertEquals("12356987",auditLog.getAccountNumber());
		
	}
	@Ignore("script runner fails depends on where it is run")
	@Test
	public void successfullyMapAllDetailsToObject() throws IOException{
		scriptRunner.runScript("tfs-core/src/test/resources/ddl/insert_audit_log_test_data.sql");
		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
		TransactionLog auditLog = auditLogRepository.getAuditLog(9999L);
		assertNotNull(auditLog);

		assertEquals("TXNREFNUMBER",auditLog.getTransactionReferenceNumber());
		assertNotNull(auditLog.getTransactionDate());
		assertEquals("DEALNUMBER",auditLog.getDealNumber());
		assertEquals("TRANSTYPECODE",auditLog.getTransactionTypeCode());
		assertEquals("TRANSSUBTYPE",auditLog.getTransactionSubtype());
	
		assertEquals("TRANSMODE",auditLog.getTransactionMode());
		assertEquals(BigDecimal.valueOf(199),auditLog.getTransactionAmount());
		assertEquals("INCOMING",auditLog.getDirection().toString());
		
		assertEquals("BRANCHCODE",auditLog.getBranchCode());
		assertEquals("ACCOUNTNUM",auditLog.getAccountNumber());
		assertEquals("PHP",auditLog.getSettlementCurrency().toString());
		assertEquals(Double.valueOf(191.2131),auditLog.getExchangeRate());
		assertEquals(BigDecimal.valueOf(20112),auditLog.getSettlementAmount());
		assertEquals("PURPOSE",auditLog.getPurpose());
		
		assertEquals("CPACCOUNTNO",auditLog.getCounterparty().getAccountNo());
		assertEquals("CPNAME1",auditLog.getCounterparty().getName1());
		assertEquals("CPNAME2",auditLog.getCounterparty().getName2());
		assertEquals("CPNAME3",auditLog.getCounterparty().getName3());
		
		assertEquals("INSTITUTIONNAME",auditLog.getCounterparty().getInstitution().getName());
		assertEquals("INSCOUNTRY",auditLog.getCounterparty().getInstitution().getCountry());

		assertEquals("CORRESPONDENTNAME",auditLog.getCorrespondentBank().getName());
		assertEquals("COR_COUNTRY",auditLog.getCorrespondentBank().getCountryCode());
		assertEquals("COR_ADD1",auditLog.getCorrespondentBank().getAddress().getAddress1());
		assertEquals("COR_ADD2",auditLog.getCorrespondentBank().getAddress().getAddress2());
		assertEquals("COR_ADD3",auditLog.getCorrespondentBank().getAddress().getAddress3());
		
		assertEquals("INS_NAME",auditLog.getIntermediatoryInstitution().getName());
		assertEquals("INS_COUNTRY",auditLog.getIntermediatoryInstitution().getCountry());
//		assertEquals("INS_ADD1",auditLog.getIntermediatoryInstitution().getAddress().getAddress1());
//		assertEquals("INS_ADD2",auditLog.getIntermediatoryInstitution().getAddress().getAddress2());
//		assertEquals("INS_ADD3",auditLog.getIntermediatoryInstitution().getAddress().getAddress3());

		
		assertEquals("BENEF_NAME1",auditLog.getBeneficiary().getName1());
		assertEquals("BENEF_NAME2",auditLog.getBeneficiary().getName2());
		assertEquals("BENEF_NAME3",auditLog.getBeneficiary().getName3());
		assertEquals("BENEF_COUNTRY",auditLog.getBeneficiary().getCountry());
		assertEquals("BENEF_ADD1",auditLog.getBeneficiary().getAddress().getAddress1());
		assertEquals("BENEF_ADD2",auditLog.getBeneficiary().getAddress().getAddress2());
		assertEquals("BENEF_ADD3",auditLog.getBeneficiary().getAddress().getAddress3());

		
		assertEquals("PRODUCTTYPE",auditLog.getProductType());
		assertNotNull(auditLog.getInceptionDate());
		assertNotNull(auditLog.getMaturityDate());
		assertEquals("NARRATION",auditLog.getNarration());
		assertEquals("REMARKS",auditLog.getRemarks());
		assertEquals("NATURE",auditLog.getNature());
		assertEquals("FUNDS_SOURCE",auditLog.getFundsSource());
		assertEquals("CERTIFIED_DOCUS",auditLog.getCertifiedDocuments());	
		assertNotNull(auditLog.getInputDate());

		assertEquals("TRANSACTION_CODE",auditLog.getTransactionCode());	
		assertEquals("PAYMENT_MODE",auditLog.getPaymentMode());	

	}
	
	
	
	@Test
	public void successfullyGetAuditLogByRefNumber(){
		jdbcTemplate.execute("INSERT INTO TRANSACTIONLOG (accountNumber,txnReferenceNumber) VALUES ('12356987','REF#')");
		jdbcTemplate.execute("INSERT INTO TRANSACTIONLOG (accountNumber,txnReferenceNumber) VALUES ('89898817','REF1#')");
		jdbcTemplate.execute("INSERT INTO TRANSACTIONLOG (accountNumber,txnReferenceNumber) VALUES ('12356987','REF2#')");

		assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
		
		TransactionLog auditLog = auditLogRepository.getAuditLogByReferenceNumber("REF#");
		assertNotNull(auditLog);

		assertEquals("12356987",auditLog.getAccountNumber());
		assertEquals("REF#",auditLog.getTransactionReferenceNumber());
	}

    @Ignore("ID field has been removed")
	@Test
	public void failToRetrieveAnyAuditLogDueToInvalidId(){
		jdbcTemplate.execute("INSERT INTO TRANSACTIONLOG (accountNumber,txnReferenceNumber) VALUES ('12356987','REF#')");
		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
		
		//INVALID ID
		TransactionLog auditLog = auditLogRepository.getAuditLog(9182981928L);
		assertNull(auditLog);
	}
	
}
