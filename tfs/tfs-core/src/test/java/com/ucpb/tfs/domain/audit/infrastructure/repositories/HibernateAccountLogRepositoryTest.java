package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

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

import com.ucpb.tfs.domain.audit.AccountLog;
import com.ucpb.tfs.util.ScriptRunner;

@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration
@Transactional
public class HibernateAccountLogRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	@Qualifier("mySessionFactory")
	private SessionFactory sessionFactory;
	
	@Autowired
	private HibernateAccountLogRepository accountLogRepository;
	
	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ScriptRunner scriptRunner;
	
//	@Before
	public void setup(){
		jdbcTemplate.execute("DELETE FROM ACCOUNTLOG");
	}
	
	@Test
	public void successfullyPersistAccountLog(){
		assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM ACCOUNTLOG"));
		AccountLog log = new AccountLog();
		log.setAccountNumber("accountNumber1");
		log.setOpeningDate(new Date());
		log.setClosingDate(new Date());
		log.setBranchCode("BRANCHCODE");
		
		accountLogRepository.persist(log);
		
		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM ACCOUNTLOG WHERE " +
				"accountNumber = 'accountNumber1' and branchCode = 'BRANCHCODE'"));
	}
	
	@Test
	public void successfullyGetAccountLogById(){
		jdbcTemplate.execute("INSERT INTO ACCOUNTLOG (ID,accountNumber,accountType,accountPurpose,branchCode,openingDate,closingDate,monthlyEstimatedTransactionCount,monthlyEstimatedTransactionVolume) VALUES (99,'accountNumber1','accountType','purpose','code123',CURRENT_TIMESTAMP ,CURRENT_TIMESTAMP ,50,75)");
		jdbcTemplate.execute("INSERT INTO ACCOUNTLOG (ID,accountNumber,accountType,accountPurpose,branchCode,openingDate,closingDate,monthlyEstimatedTransactionCount,monthlyEstimatedTransactionVolume) VALUES (88,'accountNumber2','accountType','purpose','code231',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,50,75)");

		assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM ACCOUNTLOG"));
		AccountLog log = accountLogRepository.getAccountLogById(Long.valueOf(99));
		assertNotNull(log);
		assertEquals("accountNumber1",log.getAccountNumber());
		assertEquals("accountType",log.getAccountType());
		assertEquals("purpose",log.getAccountPurpose());
		assertEquals("code123",log.getBranchCode());
		assertNotNull(log.getOpeningDate());
		assertNotNull(log.getClosingDate());
	}
	
	@Test
	public void successfullyGetAccountLogByAccountNumber(){
		jdbcTemplate.execute("INSERT INTO ACCOUNTLOG (ID,accountNumber,accountType,accountPurpose,branchCode,openingDate,closingDate,monthlyEstimatedTransactionCount,monthlyEstimatedTransactionVolume) VALUES (99,'accountNumber1','accountType','purpose','code123',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,50,75)");
		jdbcTemplate.execute("INSERT INTO ACCOUNTLOG (ID,accountNumber,accountType,accountPurpose,branchCode,openingDate,closingDate,monthlyEstimatedTransactionCount,monthlyEstimatedTransactionVolume) VALUES (88,'accountNumber2','accountType','purpose','code231',CURRENT_TIMESTAMP ,CURRENT_TIMESTAMP ,50,75)");
		assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM ACCOUNTLOG"));
		
		AccountLog log = accountLogRepository.getAccountLogByAccountNumber("accountNumber1");
		assertNotNull(log);
		assertEquals("accountNumber1",log.getAccountNumber());
		assertEquals("accountType",log.getAccountType());
		assertEquals("purpose",log.getAccountPurpose());
		assertEquals("code123",log.getBranchCode());
		assertNotNull(log.getOpeningDate());
		assertNotNull(log.getClosingDate());
	}
	
	@Ignore("TODO: fix sql rest for dates")
	@Test
	public void successfullyGetAccountLogsByOpeningDate(){
		jdbcTemplate.execute("INSERT INTO ACCOUNTLOG (ID,accountNumber,accountType,accountPurpose,branchCode,openingDate,closingDate,monthlyEstimatedTransactionCount,monthlyEstimatedTransactionVolume) VALUES (99,'accountNumber1','accountType','purpose','code123',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,50,75)");
		jdbcTemplate.execute("INSERT INTO ACCOUNTLOG (ID,accountNumber,accountType,accountPurpose,branchCode,openingDate,closingDate,monthlyEstimatedTransactionCount,monthlyEstimatedTransactionVolume) VALUES (88,'accountNumber2','accountType','purpose','code231',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,50,75)");
		assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM ACCOUNTLOG"));
		
		List<AccountLog> logs = accountLogRepository.getAccountLogsByOpeningDate(new Date());
		assertNotNull(logs);
		assertEquals(2,logs.size());
	}
	
}
