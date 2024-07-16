package com.ucpb.tfs.batch.listener;

import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class DbLoggingJobListener implements JobListener {

    private static final String JOB_LISTENER_NAME = "Database Logging Job Listener";
    private static final String JOB_HISTORY_ID = "jobHistoryId";
    private static final String JOB_BEAN_NAME = "jobBeanName";
    private static final String INSERT_JOB_HISTORY_SQL = "INSERT INTO JOB_HISTORY (JOB_NAME,TRIGGER_NAME,GROUP_NAME,START_TIME,PREVIOUS_FIRE_TIME,NEXT_FIRE_TIME, STATUS) VALUES (?,?,?,?,?,?, 'STARTED')";
    private static final String UPDATE_JOB_HISTORY_SQL = "UPDATE JOB_HISTORY SET END_TIME = CURRENT_TIMESTAMP, STATUS = ?, MESSAGE = ? WHERE ID = ?";

    private static final String INSERT_JOB_EXCEPTION_SQL = "INSERT INTO JOB_EXCEPTIONS (JOB_NAME,TRIGGER,GROUP_NAME,ERROR_MESSAGE) VALUES (?,?,?,?)";


    private JdbcTemplate jdbcTemplate;

    public DbLoggingJobListener(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getName() {
        return JOB_LISTENER_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new LoggerPreparedStatementCreator(getJobDetails(jobExecutionContext),INSERT_JOB_HISTORY_SQL),keyHolder);
        jobExecutionContext.getMergedJobDataMap().put(JOB_HISTORY_ID,keyHolder.getKey());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
//        jdbcTemplate.update(INSERT_JOB_HISTORY_SQL,getJobDetails(jobExecutionContext));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        Object transactionId =  jobExecutionContext.getMergedJobDataMap().get(JOB_HISTORY_ID);
        String status = "COMPLETED";
        String message = "Job was successfully completed";

        if(e != null && !StringUtils.isEmpty(e.getMessage())){
            status = "FAILED";
            message = e.getMessage();
        }

        jdbcTemplate.update(UPDATE_JOB_HISTORY_SQL,status,message,transactionId);
    }

    private Object[] getJobDetails(JobExecutionContext jobExecutionContext){
        String jobBeanName = (String) jobExecutionContext.getMergedJobDataMap().get(JOB_BEAN_NAME);
        Trigger trigger = jobExecutionContext.getTrigger();

        Object[] args = {
                jobBeanName,
                trigger.getName() != null ? trigger.getName() : "",
                trigger.getGroup() != null ? trigger.getGroup() : "",
                new java.util.Date(),
                trigger.getPreviousFireTime(),
                trigger.getNextFireTime()};
        return args;
    }

    private class LoggerPreparedStatementCreator implements PreparedStatementCreator {

        private Object args[];
        private String sql;

        public LoggerPreparedStatementCreator(Object[] args, String sql){
            this.args = args;
            this.sql = sql;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"ID"});
            for(int ctr = 0; ctr < args.length; ctr++){
                ps.setObject(ctr + 1,getSqlTypeEquivalent(args[ctr]));
            }
            return ps;
        }

        private Object getSqlTypeEquivalent(Object sourceObject){
            Object sqlObject = sourceObject;
            //TODO: add more type conversions in the future?
            if(sourceObject instanceof Date){
                sqlObject = new java.sql.Date(((Date) sourceObject).getTime());
            }
            return sqlObject;
        }
    }
}
