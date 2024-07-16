package com.ucpb.tfs.interfaces.generator;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.generator.SimpleIdGenerator;

import static org.mockito.Matchers.anyString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleIdGeneratorTest {

	private SimpleIdGenerator idGenerator;
	
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup(){
		jdbcTemplate = mock(JdbcTemplate.class);
		idGenerator = new SimpleIdGenerator(jdbcTemplate);
	}
	
	@Test
	public void successfullyIncrementIds(){
		when(jdbcTemplate.queryForInt(anyString())).thenReturn(1,2,3,4,5,6,7,8,9,10);
		assertEquals(1,idGenerator.nextId());
		assertEquals(2,idGenerator.nextId());
		assertEquals(3,idGenerator.nextId());
		assertEquals(4,idGenerator.nextId());
		assertEquals(5,idGenerator.nextId());

	}
}
