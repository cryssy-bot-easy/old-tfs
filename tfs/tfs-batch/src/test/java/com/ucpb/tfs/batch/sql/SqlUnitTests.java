package com.ucpb.tfs.batch.sql;

import static org.junit.Assert.assertTrue;

import java.io.File;

import com.ucpb.tfs.batch.util.SqlRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlUnitTests {
	
	
	@Autowired
	private SqlRunner sqlRunner;

    @Autowired
    private JdbcTemplate template;
	
	
	public void validGlAllocationsQuery(){
		File file = new File("classpath*:sql/glAllocationsQuery.sql");
		assertTrue(file.exists());
	}
	

}
