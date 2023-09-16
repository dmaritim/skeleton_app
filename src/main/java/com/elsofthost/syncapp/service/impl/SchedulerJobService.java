package com.elsofthost.syncapp.service.impl;

import java.util.Date;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import com.elsofthost.syncapp.component.JobScheduleCreator;
import com.elsofthost.syncapp.entity.SchedulerJobInfo;
import com.elsofthost.syncapp.job.SampleCronJob;
import com.elsofthost.syncapp.job.SimpleJob;
import com.elsofthost.syncapp.repository.SchedulerRepository;
import com.elsofthost.syncapp.service.JobService;

@Service
public class SchedulerJobService implements JobService{

	private static final Logger log = LoggerFactory.getLogger(SchedulerJobService.class);

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private SchedulerRepository schedulerRepository;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private JobScheduleCreator scheduleCreator;

	public SchedulerMetaData getMetaData() throws SchedulerException {
		SchedulerMetaData metaData = scheduler.getMetaData();
		return metaData;
	}

	public List<SchedulerJobInfo> getAllJobList() {
		return schedulerRepository.findAll();
	}

    public boolean deleteJob(Long id) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.getById(id);
        	schedulerRepository.deleteById(id);
        	log.info(">>>>> jobName = [" + getJobInfo.getJobName() + "]" + " deleted.");
            return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(getJobInfo.getJobName(), getJobInfo.getJobGroup()));
        } catch (SchedulerException e) {
            log.error("Failed to delete job id = {}", id, e);
            return false;
        }
    }

    public boolean pauseJob(Long id) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.getById(id);
        	getJobInfo.setJobStatus("PAUSED");
        	schedulerRepository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().pauseJob(new JobKey(getJobInfo.getJobName(), getJobInfo.getJobGroup()));
            log.info(">>>>> jobName = [" + getJobInfo.getJobName() + "]" + " paused.");
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to pause job with id = {}", id, e);
            return false;
        }
    }

    public boolean resumeJob(Long id) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.getById(id);
        	getJobInfo.setJobStatus("RESUMED");
        	schedulerRepository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(getJobInfo.getJobName(), getJobInfo.getJobGroup()));
            log.info(">>>>> jobName = [" + getJobInfo.getJobName() + "]" + " resumed.");
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to resume job with id = {}", id, e);
            return false;
        }
    }

    public boolean startJobNow(Long id) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.getById(id);
        	getJobInfo.setJobStatus("SCHEDULED & STARTED");
        	schedulerRepository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(getJobInfo.getJobName(), getJobInfo.getJobGroup()));
            log.info(">>>>> jobName = [" + getJobInfo.getJobName() + "]" + " scheduled and started now.");
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to start job id = {}",id, e);
            return false;
        }
    }

	@SuppressWarnings("deprecation")
	public void saveOrupdate(SchedulerJobInfo scheduleJob) throws Exception {
		if (!StringUtils.isEmpty(scheduleJob.getCronExpression())) {
			scheduleJob.setJobClass(SampleCronJob.class.getName());
			scheduleJob.setCronJob(true);
		} else {
			scheduleJob.setJobClass(SimpleJob.class.getName());
			scheduleJob.setCronJob(false);
			scheduleJob.setRepeatTime((Long) 1);
		}
		if (StringUtils.isEmpty(scheduleJob.getId())) {
			log.info("Job Info: {}", scheduleJob);
			scheduleNewJob(scheduleJob);
		} else {
			updateScheduleJob(scheduleJob);
		}
		scheduleJob.setDesc("i am job number " + scheduleJob.getId());
		scheduleJob.setInterfaceName("interface_" + scheduleJob.getId());
		log.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created.");
	}

	@SuppressWarnings("unchecked")
	public void scheduleNewJob(SchedulerJobInfo jobInfo) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			JobDetail jobDetail = JobBuilder
					.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
					.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
			if (!scheduler.checkExists(jobDetail.getKey())) {

				jobDetail = scheduleCreator.createJob(
						(Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, context,
						jobInfo.getJobName(), jobInfo.getJobGroup());

				Trigger trigger;
				if (jobInfo.getCronJob()) {
					trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), jobInfo.getJobGroup(), new Date(), jobInfo.getCronExpression());
				} else {
					trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),jobInfo.getRepeatTime());
				}
				if (!scheduler.isShutdown()) {
					scheduler.start();
				}
				scheduler.scheduleJob(jobDetail, trigger);
				jobInfo.setJobStatus("SCHEDULED");
				schedulerRepository.save(jobInfo);
				log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
			} else {
				log.error("scheduleNewJobRequest.jobAlreadyExist");
			}
		} catch (ClassNotFoundException e) {
			log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void updateScheduleJob(SchedulerJobInfo jobInfo) {
		Trigger newTrigger;
		if (jobInfo.getCronJob()) {
			newTrigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(),jobInfo.getJobGroup(), new Date(),
					jobInfo.getCronExpression());
		} else {
			newTrigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(), jobInfo.getRepeatTime());
		}
		try {
			schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
			jobInfo.setJobStatus("EDITED & SCHEDULED");
			schedulerRepository.save(jobInfo);
			log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " updated and scheduled.");
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
		}
	}
}
