package com.elsofthost.syncapp.job;

import java.util.stream.IntStream;


import org.quartz.DisallowConcurrentExecution;
<<<<<<< HEAD
import org.quartz.Job;
=======
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<<<<<<< HEAD

@DisallowConcurrentExecution
public class SampleCronJob implements Job {
=======
import org.springframework.scheduling.quartz.QuartzJobBean;



@DisallowConcurrentExecution
public class SampleCronJob extends QuartzJobBean {
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
	
	private static final Logger log = LoggerFactory.getLogger(SampleCronJob.class);
	
    @Override
<<<<<<< HEAD
    public void execute(JobExecutionContext context) throws JobExecutionException {
=======
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
        log.info("SampleCronJob Start................");
        IntStream.range(0, 10).forEach(i -> {
            log.info("Counting - {}", i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
        log.info("SampleCronJob End................");
    }
}
