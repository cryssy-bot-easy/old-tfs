package com.ucpb.tfs.batch.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Wrapper bean for retrieving and invoking spring managed beans
 */
public class ApplicationContextAwareQuartzJob extends QuartzJobBean {

    private static final String APPLICATION_CONTEXT = "applicationContext";
    private String jobBeanName;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get(APPLICATION_CONTEXT);

            SpringJob springJob = (SpringJob) applicationContext.getBean(jobBeanName);

            springJob.execute();

        } catch (SchedulerException e) {
            throw new JobExecutionException("(SchedulerException) Failed to invoke bean job: " + jobBeanName,e);
        } catch (Exception e) {
            throw new JobExecutionException("(Exception) Failed to invoke bean job: " + jobBeanName,e);
        }
    }

    public void setJobBeanName(String jobBeanName) {
        this.jobBeanName = jobBeanName;
    }
}
