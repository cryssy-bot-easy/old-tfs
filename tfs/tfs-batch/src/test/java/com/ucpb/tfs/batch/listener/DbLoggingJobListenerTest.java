package com.ucpb.tfs.batch.listener;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class DbLoggingJobListenerTest {

    @InjectMocks
    private DbLoggingJobListener listener;

    @MockitoAnnotations.Mock
    private JdbcTemplate jdbcTemplate;

    @MockitoAnnotations.Mock
    private JobExecutionContext context;

    private JobDataMap jobDataMap;

    private Trigger trigger;


    @Before
    public void setup(){
        jobDataMap = mock(JobDataMap.class);
        trigger = mock(Trigger.class);

        when(context.getTrigger()).thenReturn(trigger);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.get("jobBeanName")).thenReturn("steveJob");
        when(trigger.getName()).thenReturn("triggerName");
        //October 23, 2012 - 1350984092112
        when(trigger.getNextFireTime()).thenReturn(new Date(1350984092112L));
        when(trigger.getPreviousFireTime()).thenReturn(new Date(1350984092112L));
        when(trigger.getGroup()).thenReturn("groupName");


    }

    @Test
    public void successfullyPersistJobDetailsToHistoryBeforeJobExecution(){
        listener.jobToBeExecuted(context);
//        verify(jdbcTemplate).update("INSERT INTO JOB_HISTORY (JOB_NAME,TRIGGER_NAME,GROUP_NAME,START_TIME,PREVIOUS_FIRE_TIME,NEXT_FIRE_TIME) VALUES (?,?,?,?)",
//                new Object[] {"steveJob","triggerName","groupName",new Date(),new Date(1350984092112L),new Date(1350984092112L)});
    }

}
