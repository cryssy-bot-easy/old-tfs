package com.ucpb.tfs.batch.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;

/**
 */
public class SqlRunner {

    private static final String DEFAULT_DELIMITER = ";";
    private JdbcTemplate jdbcTemplate;


    public SqlRunner(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void run(File file, String delimeter) throws IOException {
        for(String sql : FileUtil.read(file, delimeter)){
            String toRun = StringUtils.trim(sql);
            if(!StringUtils.isEmpty(toRun)){
                jdbcTemplate.execute(toRun);
            }
        }
    }

    public void run(File file) throws IOException {
        run(file,DEFAULT_DELIMITER);
    }




    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
