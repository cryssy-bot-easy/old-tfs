package com.ucpb.tfs.interfaces.util;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 */
public class SqlRunnerTask {

    private JdbcTemplate jdbcTemplate;
    private String sql;

    public SqlRunnerTask(JdbcTemplate jdbcTemplate, String sql){
        this.jdbcTemplate = jdbcTemplate;
        this.sql = sql;
    }

    public SqlRunnerTask(DataSource ds, String sql){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.sql = sql;
    }


    public void runSql(){
        System.out.println("RUNNING: " + sql);
        jdbcTemplate.execute(sql);
    }

}
