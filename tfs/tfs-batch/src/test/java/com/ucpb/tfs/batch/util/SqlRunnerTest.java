package com.ucpb.tfs.batch.util;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 */
public class SqlRunnerTest {

    private SqlRunner sqlRunner;
    private JdbcTemplate jdbcTemplate;


    @Before
    public void setup(){
        jdbcTemplate = mock(JdbcTemplate.class);
        sqlRunner = new SqlRunner(jdbcTemplate);
    }

    @Test
    public void runThreeSqlQueries() throws IOException, URISyntaxException {
        File file = FileUtil.getFileFromResource("/mockddl/hasThreeValidSql.sql");
        sqlRunner.run(file);
        verify(jdbcTemplate).execute("this");
        verify(jdbcTemplate).execute("is");
        verify(jdbcTemplate).execute("valid");

    }

    @Test
    public void doNotRunAnythingFromEmptyFile() throws IOException, URISyntaxException {
        File file = FileUtil.getFileFromResource("/mockddl/empty.sql");
        sqlRunner.run(file);
        verify(jdbcTemplate,never()).execute(anyString());
    }

    @Test
    public void ignoreWhitespacesBetweenStatements() throws IOException, URISyntaxException {
        File file = FileUtil.getFileFromResource("/mockddl/hasWhitespaces.sql");
        sqlRunner.run(file);
        verify(jdbcTemplate).execute("this");
        verify(jdbcTemplate).execute("whitespace");
    }


}
