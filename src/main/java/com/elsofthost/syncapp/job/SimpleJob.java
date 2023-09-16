package com.elsofthost.syncapp.job;
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