package com.ucpb.tfs.batch.job;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

/**
 */
public class SqlRunnerJob implements SpringJob{

    private final String sql;

    private final JdbcTemplate jdbcTemplate;

    public SqlRunnerJob(String sql, JdbcTemplate jdbcTemplate){
        System.out.println("SqlRunnerJob");
        this.sql = StringUtils.trim(sql);
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void execute() {
        System.out.println("SQL RUNNER JOB:"+sql);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void execute(String reportDate) {
        //TODO
        System.out.println("SQL RUNNER JOB:"+sql);
        jdbcTemplate.execute(sql);
    }
}
