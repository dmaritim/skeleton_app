package com.elsofthost.syncapp.job;
<<<<<<< HEAD
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@DisallowConcurrentExecution
public class SimpleJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(SimpleJob.class);

    @Override
    public void execute(JobExecutionContext context) {
    	log.debug("Job: {0}", getClass());
    }
//    @Override
//    public void execute(JobExecutionContext context) {
//        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
//        String param = dataMap.getString("param");
//        log.debug(MessageFormat.format("Job: {0}; Param: {1}",
//                getClass(), param));
//    }
}
=======

import java.util.stream.IntStream;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SimpleJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //log.info("SimpleJob Start................");
        IntStream.range(0, 5).forEach(i -> {
            //log.info("Counting - {}", i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //log.error(e.getMessage(), e);
            }
        });
        //log.info("SimpleJob End................");
    }
}
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
