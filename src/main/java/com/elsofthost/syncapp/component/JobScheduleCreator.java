package com.elsofthost.syncapp.component;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


@Component
public class JobScheduleCreator {

	private static final Logger log = LoggerFactory.getLogger(JobScheduleCreator.class);
	
    public JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable,
                               ApplicationContext context, String jobName, String jobGroup) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);

        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobName + jobGroup, jobClass.getName());
        factoryBean.setJobDataMap(jobDataMap);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    public CronTrigger createCronTrigger(String triggerName,String triggerGroup, Date startTime, String cronExpression) {
    	CronTrigger trigger = TriggerBuilder.newTrigger()
    			.withIdentity(triggerName + "Trigger",triggerGroup)
    			.startAt(startTime)
    			.withSchedule(CronScheduleBuilder
    			    .cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed()).build();
		return trigger;
    }

    public SimpleTrigger createSimpleTrigger(String triggerName, Date startTime, Integer repeatTime) {
    	SimpleTrigger trigger = TriggerBuilder.newTrigger()
			    .startAt(startTime)
			    .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(repeatTime).withMisfireHandlingInstructionFireNow())
			    .build();
    	return trigger;
    }
}
