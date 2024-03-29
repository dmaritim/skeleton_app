package com.elsofthost.syncapp.service.impl;

import java.util.Date;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.elsofthost.syncapp.component.JobScheduleCreator;
import com.elsofthost.syncapp.entity.SchedulerJobInfo;
import com.elsofthost.syncapp.job.SampleCronJob;
import com.elsofthost.syncapp.job.SimpleJob;
import com.elsofthost.syncapp.repository.SchedulerRepository;
import com.elsofthost.syncapp.service.SchedulerJobService;


@Transactional
@Service
public class SchedulerJobServiceImpl implements SchedulerJobService{


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

    public boolean deleteJob(SchedulerJobInfo jobInfo) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
        	schedulerRepository.delete(getJobInfo);
        	//log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " deleted.");
            return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
        } catch (SchedulerException e) {
            //log.error("Failed to delete job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    public boolean pauseJob(SchedulerJobInfo jobInfo) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
        	getJobInfo.setJobStatus("PAUSED");
        	schedulerRepository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            //log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " paused.");
            return true;
        } catch (SchedulerException e) {
            //log.error("Failed to pause job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    public boolean resumeJob(SchedulerJobInfo jobInfo) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
        	getJobInfo.setJobStatus("RESUMED");
        	schedulerRepository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            //log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " resumed.");
            return true;
        } catch (SchedulerException e) {
            //log.error("Failed to resume job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    public boolean startJobNow(SchedulerJobInfo jobInfo) {
        try {
        	SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
        	getJobInfo.setJobStatus("SCHEDULED & STARTED");
        	schedulerRepository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            //log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled and started now.");
            return true;
        } catch (SchedulerException e) {
            //log.error("Failed to start new job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }


	public void saveOrupdate(SchedulerJobInfo scheduleJob) throws Exception {
		if (scheduleJob.getCronExpression().length() > 0) {
			scheduleJob.setJobClass(SampleCronJob.class.getName());
			scheduleJob.setCronJob(true);
		} else {
			scheduleJob.setJobClass(SimpleJob.class.getName());
			scheduleJob.setCronJob(false);
			scheduleJob.setRepeatTime((long) 1);
		}
		if (StringUtils.isEmpty(scheduleJob.getId())) {
			//log.info("Job Info: {}", scheduleJob);
			scheduleNewJob(scheduleJob);
		} else {
			updateScheduleJob(scheduleJob);
		}
		scheduleJob.setDesc("i am job number " + scheduleJob.getId());
		scheduleJob.setInterfaceName("interface_" + scheduleJob.getId());
		//log.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created.");
	}

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
					trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(),jobInfo.getJobGroup(), new Date(),
							jobInfo.getCronExpression());
				} else {
					trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
							jobInfo.getRepeatTime());
				}
				scheduler.scheduleJob(jobDetail, trigger);
				jobInfo.setJobStatus("SCHEDULED");
				schedulerRepository.save(jobInfo);
				//log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
			} else {
				//log.error("scheduleNewJobRequest.jobAlreadyExist");
			}
		} catch (ClassNotFoundException e) {
			//log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
		} catch (SchedulerException e) {
			//log.error(e.getMessage(), e);
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
			//log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " updated and scheduled.");
		} catch (SchedulerException e) {
			//log.error(e.getMessage(), e);
		}

	}
}