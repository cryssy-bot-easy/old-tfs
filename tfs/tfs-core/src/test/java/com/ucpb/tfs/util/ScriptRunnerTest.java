package com.ucpb.tfs.util;

import static org.junit.Assert.assertEquals;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

@Ignore("Ignoring because of workspace problems. TODO: fix paths input")
public class ScriptRunnerTest {

	private MockJdbcTemplate jdbcTemplate;
	private ScriptRunner scriptRunner;
	
	class MockJdbcTemplate extends JdbcTemplate {
		int executed = 0;
		@Override
		public void execute(String sql){
			executed++;
		}
	}
	
	@Before
	public void setup(){
		jdbcTemplate = new MockJdbcTemplate();
		scriptRunner = new ScriptRunner(jdbcTemplate);
	}
	
	@Test
	public void successfullyRunThreeStatements() throws IOException{
		scriptRunner.runScript("tfs-core/src/test/resources/ddl/testsql.sql");
		assertEquals(3,jdbcTemplate.executed);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void failOnNonExistentFile() throws IOException{
		scriptRunner.runScript("idonotexist.sql");
	}
	
//	@Test(expected=DataAccessResourceFailureException.class)
//	public void forceDataAccessException() throws IOException{
//		scriptRunner = new ScriptRunner(new JdbcTemplate(){
//			@Override
//			public void execute(String sql){
//				throw new DataAccessResourceFailureException("failed to access resource");
//			}
//		});
//		
//		scriptRunner.runScript("src/test/resources/com/smarthub/mpg/agentregistration/testsql.sql");
//	}
	
	@Test
	public void dontInvokeJdbcTemplateOnEmptySqlFile() throws IOException{
		scriptRunner.runScript("tfs-core/src/test/resources/ddl/empty.sql");
		assertEquals(0,jdbcTemplate.executed);

	}
	
	@Test
	public void ignoreEmptyStatements() throws IOException{
		scriptRunner.runScript("tfs-core/src/test/resources/ddl/containsEmptySql.sql");
		assertEquals(2,jdbcTemplate.executed);
	}
	
	@Test
	public void ignoreWhitespaces() throws IOException{
		scriptRunner.runScript("tfs-core/src/test/resources/ddl/whitespaces.sql");
		assertEquals(0,jdbcTemplate.executed);
	}
	
}
