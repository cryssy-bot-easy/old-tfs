package com.ucpb.tfs.application.service;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: IPCVal
 */
 /*	PROLOGUE:
 * 	(revision)
	SCR/ER Number: 20150820-072
	SCR/ER Description: To catch duplication in CIF.
	[Revised by:] Jesse James Joson
	[Date revised:] 10/13/2015
	Program [Revision] Details: add a response whether to failed in UI or Success.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: AllocationUnitCodeService
 */
 
/*	PROLOGUE:
* 	(revision)
	SCR/ER Number: 20151020-067
	SCR/ER Description: Failed in UI due to catching of error for removeLogin().
	[Revised by:] Jesse James Joson
	[Date revised:] 11/11/2015
	Program [Revision] Details: Remove the try catch, and made the program stop if error was encounter
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: AllocationUnitCodeService
*/


/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: AllocationUnitCodeService
 */
 
@Component
public class AllocationUnitCodeService {

    @Autowired
    private Job allocationUnitCodeJob;

    @Autowired
    private JobLauncher springBatchJobLauncher;

    @Autowired
    private JobRepository springBatchJobRepository;
	
    public String response = "true";

    public String executeUpdate(String cifNumber) throws Exception {

        System.out.println("AllocationUnitCodeService.executeUpdate()!\n");

        JobParametersBuilder builder = new JobParametersBuilder();

        // Parameter unique id for re-running the same job instance
        StringBuilder idUnique = new StringBuilder(RandomStringUtils.random(10, true, true));
        idUnique.append(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        builder.addString("idUnique", idUnique.toString());

        // Parameter cifNumber
        if (cifNumber != null) {
            builder.addString("cifNumber", cifNumber);
        }

        System.out.println(new Date().toString());

        // Run the tasklet in a separate thread!
        JobExecution jobExecution = springBatchJobLauncher.run(allocationUnitCodeJob, builder.toJobParameters());

        System.out.println("STATUS = " + jobExecution.getStatus().toString());

        // You have the choice here not to make the main thread wait.
        // But as per instruction, make the main thread wait for now (sorry).

        // Periodically sleep while tasklet is running
        while (jobExecution.getStatus().isRunning()) {
			
				// Sleep every 5 seconds
	            //Thread.sleep(5000);

	            // After waking up check the status of the tasklet
	            jobExecution = springBatchJobRepository.getLastJobExecution(allocationUnitCodeJob.getName(), builder.toJobParameters());

        }
        // JobExecution jobExecution2 = springBatchJobRepository.getLastJobExecution(allocationUnitCodeJob.getName(), builder.toJobParameters());

        System.out.println("STATUS = " + jobExecution.getStatus().toString());
        System.out.println(new Date().toString());

        // The tasklet "throws" exceptions via exit status, so we "catch" this then re-throw to the main stack.
        // If you don't do this, you won't be able to catch the tasklet's exceptions via conventional try-catch-throw.
        ExitStatus exitStatus = jobExecution.getExitStatus();
        System.out.println("exitStatus.getExitCode() = " + exitStatus.getExitCode());
        System.out.println("exitStatus.getExitDescription() = " + exitStatus.getExitDescription());
        if (exitStatus.getExitCode().equals("FAILED")) {
            throw new Exception(exitStatus.getExitDescription());
        }
		
		System.out.println(response);
        return response;
    }
}
