package com.ucpb.tfs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

public class ScriptRunner {

	private static final char SQL_DELIMETER = ';';
	private JdbcTemplate jdbcTemplate;
	
	public ScriptRunner(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void runScript(String path) throws IOException{
		
		File file = new File(path);
		for(String sql : StringUtils.split(readFile(file),SQL_DELIMETER)){
			if(!StringUtils.isBlank(sql)){
				jdbcTemplate.execute(sql);
			}
		}
	}
	
	private String readFile(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		try{
			String line = reader.readLine();
			while(!StringUtils.isEmpty(line)){
				builder.append(line);
				builder.append(" ");
				line = reader.readLine();
			}
		} finally{
			reader.close();
		}
		return builder.toString();
	}
	
}
