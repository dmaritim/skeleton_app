package com.elsofthost.syncapp.service;

import java.util.List;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;

import com.elsofthost.syncapp.entity.SchedulerJobInfo;

public interface JobService {
	SchedulerMetaData getMetaData() throws SchedulerException;
	List<SchedulerJobInfo> getAllJobList();
	boolean deleteJob(Long id);
    boolean pauseJob(Long id);
    boolean resumeJob(Long id);
    boolean startJobNow(Long id);
	void saveOrupdate(SchedulerJobInfo scheduleJob) throws Exception;
	void scheduleNewJob(SchedulerJobInfo jobInfo);
	void updateScheduleJob(SchedulerJobInfo jobInfo);
}
