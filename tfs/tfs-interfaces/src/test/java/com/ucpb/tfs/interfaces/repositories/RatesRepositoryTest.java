package com.ucpb.tfs.interfaces.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.domain.enums.RateType;
import com.ucpb.tfs.interfaces.repositories.RatesRepository;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class RatesRepositoryTest {

	@Autowired
	private RatesRepository ratesRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Before
	public void teardown() {
		jdbcTemplate.execute("DELETE FROM UCPARUCMN2.JHFXDT");
        jdbcTemplate.execute("DELETE FROM UCPARUCMN2.JHFXPR");
	}

	@Test
	public void successfullyGetTodaysRates() {
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('GBP',1,70,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('JPY',1,0.5,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',111213,2010294)");

        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (1,'BANK NOTE BUYING RATE           ',' ',' ','          ',0,0,0)");

		assertEquals(4,
                jdbcTemplate
                        .queryForInt("SELECT COUNT(*) FROM UCPARUCMN2.JHFXDT"));
//		List<Map<String, ?>> ratesForTheDay = ratesRepository.getRates(101212);
		List<Map<String, ?>> ratesForTheDay = ratesRepository.getRates();
		assertNotNull(ratesForTheDay);
		assertEquals(3, ratesForTheDay.size());

//		List<Map<String, ?>> futureRates = ratesRepository.getRates(111213);
		List<Map<String, ?>> futureRates = ratesRepository.getRates();
		assertNotNull(futureRates);
		assertEquals(1, futureRates.size());
	}

	@Test
	public void failToRetrieveAnyRatesForInvalidDates() {
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('GBP',1,70,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('JPY',1,0.5,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',111213,2010294)");

        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (1,'BANK NOTE BUYING RATE           ',' ',' ','          ',0,0,0)");

		assertEquals(4,
                jdbcTemplate
                        .queryForInt("SELECT COUNT(*) FROM UCPARUCMN2.JHFXDT"));
//		List<Map<String, ?>> ratesForTheDay = ratesRepository.getRates(1212121212);
		List<Map<String, ?>> ratesForTheDay = ratesRepository.getRates();
		assertNotNull(ratesForTheDay);
		assertEquals(0, ratesForTheDay.size());
	}

	@Test
	public void successfullyGetRatesOfSpecifiedBaseCurrency() {
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('GBP',1,70,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('JPY',1,0.5,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',111213,2010294)");

        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (1,'BANK NOTE BUYING RATE           ',' ',' ','          ',0,0,0)");

		assertEquals(4,
                jdbcTemplate
                        .queryForInt("SELECT COUNT(*) FROM UCPARUCMN2.JHFXDT"));
		List<Map<String, ?>> phpRates = ratesRepository.getRatesByBaseCurrency(101212, "PHP");
		assertNotNull(phpRates);
		assertEquals(1, phpRates.size());
		Map<String, ?> rateDetails = phpRates.get(0);
		assertEquals("USD", rateDetails.get("JFXDCD"));
		assertEquals("PHP", rateDetails.get("JFXDBC"));

	}

	@Test
	public void successfullyGetConversionRateByType() {
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('GBP',1,70,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('JPY',1,0.5,'PHP',101212,2010294)");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXDT (JFXDCD,JFXDRN,JFXDCR,JFXDBC,JHVDT6,JHVDT7) VALUES ('USD',1,43.1,'PHP',111213,2010294)");

		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXPR (JFXSEQ,JFXRDS) VALUES (17,'LC Cash Sell Rate')");
		jdbcTemplate
				.execute("INSERT INTO UCPARUCMN2.JHFXPR (JFXSEQ,JFXRDS) VALUES (2,'Bank Note Sell/Invisibles')");

		assertEquals(4,
				jdbcTemplate
						.queryForInt("SELECT COUNT(*) FROM UCPARUCMN2.JHFXDT"));
//		Map<String, ?> rate = ratesRepository.getConversionRateByType("USD","PHP", RateType.LC_CASH_SELL_RATE, 101212);
		Map<String, ?> rate = ratesRepository.getConversionRateByType("USD","PHP", RateType.LC_CASH_SELL_RATE);
		assertNotNull(rate);
		assertEquals("USD", rate.get("JFXDCD"));
		assertEquals("PHP", rate.get("JFXDBC"));
		assertEquals(BigDecimal.valueOf(43.1), rate.get("JFXDCR"));
	}

}
