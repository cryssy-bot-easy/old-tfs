package com.ucpb.tfs.domain.instruction.utils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.ucpb.tfs.utils.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@ContextConfiguration("classpath:*generator-test-context.xml")
@PrepareForTest( { DateUtil.class })
public class EtsNumberGeneratorTest {

	private EtsNumberGenerator etsNumberGenerator;
	
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup(){
		// August 16, 2012 -  new Date(1345091684376L)
		Date date = new Date(1345091684376L);
		
		jdbcTemplate = mock(JdbcTemplate.class);
		etsNumberGenerator = new EtsNumberGenerator(jdbcTemplate);
		when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn("1","2","3","4");
        when(jdbcTemplate.queryForInt(anyString())).thenReturn(1,2,3,4);
        PowerMockito.mockStatic(DateUtil.class);
	    when(DateUtil.getLastTwoDigitsOfYear(any(Date.class))).thenReturn("12");
	}
	
	@Test
	public void successfullyGenerateUniqueEtsNumbers(){
		String etsNumber1 = etsNumberGenerator.generateServiceInstructionId("53");
		String etsNumber2 = etsNumberGenerator.generateServiceInstructionId("54");
		assertFalse(etsNumber1.equals(etsNumber2));
		assertEquals("53-12-00001",etsNumber1);
		assertEquals("54-12-00002",etsNumber2);
	}
	
}
