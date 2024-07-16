package com.ucpb.tfs.batch.job;

import org.junit.Before;
import org.junit.Test;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class ApplicationContextAwareQuartzJobTest {

    private ApplicationContextAwareQuartzJob job;

    private JobExecutionContext jobExecutionContext;

    private Scheduler scheduler;

    private SchedulerContext schedulerContext;

    private ApplicationContext appContext;

    private SpringJob springJob;

    @Before
    public void setup() throws SchedulerException {
        job = new ApplicationContextAwareQuartzJob();
        jobExecutionContext = mock(JobExecutionContext.class);
        scheduler = mock(Scheduler.class);
        schedulerContext = mock(SchedulerContext.class);
        appContext = mock(ApplicationContext.class);
        springJob = mock(SpringJob.class);

        when(jobExecutionContext.getScheduler()).thenReturn(scheduler);
        when(scheduler.getContext()).thenReturn(schedulerContext);
        when(schedulerContext.get("applicationContext")).thenReturn(appContext);
        when(appContext.getBean("job")).thenReturn(springJob);

    }

    @Test
    public void successfullyExecuteSpringJob() throws JobExecutionException {

        try {
            job.setJobBeanName("job");
            job.execute(jobExecutionContext);

            verify(springJob).execute();

        } catch (Exception e) {
            throw new JobExecutionException("(Exception) Failed to invoke bean job: ", e);
        }
    }

}
