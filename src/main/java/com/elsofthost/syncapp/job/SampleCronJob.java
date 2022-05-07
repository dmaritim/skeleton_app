package com.elsofthost.syncapp.job;

import java.util.stream.IntStream;


import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;



@DisallowConcurrentExecution
public class SampleCronJob extends QuartzJobBean {
	
	private static final Logger log = LoggerFactory.getLogger(SampleCronJob.class);
	
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
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
