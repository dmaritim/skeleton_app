package com.elsofthost.syncapp.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;

import com.elsofthost.syncapp.entity.SchedulerJobInfo;

public interface SchedulerJobService {

	SchedulerMetaData getMetaData() throws SchedulerException;

	List<SchedulerJobInfo> getAllJobList();

    boolean deleteJob(SchedulerJobInfo jobInfo);

    boolean pauseJob(SchedulerJobInfo jobInfo);

    boolean resumeJob(SchedulerJobInfo jobInfo);

    boolean startJobNow(SchedulerJobInfo jobInfo);

	void saveOrupdate(SchedulerJobInfo scheduleJob) throws Exception;

	void scheduleNewJob(SchedulerJobInfo jobInfo);

	void updateScheduleJob(SchedulerJobInfo jobInfo);
}
