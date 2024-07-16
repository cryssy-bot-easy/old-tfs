package com.ucpb.tfs.interfaces.generator;

import org.springframework.jdbc.core.JdbcTemplate;

public class SimpleIdGenerator implements IdGenerator{

	private JdbcTemplate jdbcTemplate;
	
	public SimpleIdGenerator(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public int nextId() {
		return jdbcTemplate.queryForInt("SELECT nextVal FOR TRANSACTION_SEQ AS id FROM DUAL");
	}

	public void reset() {
		//do nothing
	}

}
