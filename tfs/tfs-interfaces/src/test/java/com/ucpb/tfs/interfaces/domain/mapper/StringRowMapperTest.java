package com.ucpb.tfs.interfaces.domain.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ucpb.tfs.interfaces.domain.mapper.StringRowMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StringRowMapperTest {

	private StringRowMapper stringRowMapper;
	
	private ResultSet resultSet;
	
	@Before
	public void setup(){
		List<String> columnMapping = new ArrayList<String>();
		columnMapping.add("ID");
		columnMapping.add("TXNDATE");
		columnMapping.add("TXNREFERENCENUMBER");
		columnMapping.add("DIRECTION");

		stringRowMapper = new StringRowMapper(columnMapping);
		resultSet = mock(ResultSet.class);
	}
	
	@Test
	public void successfullyConvertResultSetToString() throws SQLException{
		when(resultSet.getString("ID")).thenReturn("ID1234");
		when(resultSet.getString("TXNDATE")).thenReturn("12-12-2012");
		when(resultSet.getString("TXNREFERENCENUMBER")).thenReturn("REF1234567");
		when(resultSet.getString("DIRECTION")).thenReturn("O");
		
		String response = stringRowMapper.mapRow(resultSet, 0);
		
		assertNotNull(response);
		assertEquals(response,"ID1234,12-12-2012,REF1234567,O");
	}
	
	@Test
	public void successfullyConvertToStringWithEmptyRows() throws SQLException{
		when(resultSet.getString("ID")).thenReturn("ID1234");
		when(resultSet.getString("TXNDATE")).thenReturn("");
		when(resultSet.getString("TXNREFERENCENUMBER")).thenReturn("");
		when(resultSet.getString("DIRECTION")).thenReturn("");
		
		String response = stringRowMapper.mapRow(resultSet, 0);
		
		assertNotNull(response);
		assertEquals(response,"ID1234,,,");
	}
	
	
}
