package com.elsofthost.syncapp.controller;
import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.elsofthost.syncapp.entity.Message;
import com.elsofthost.syncapp.entity.SchedulerJobInfo;
import com.elsofthost.syncapp.service.JobService;


@RestController
@RequestMapping("/api")
public class JobController {
	private static final Logger log = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private JobService scheduleJobService;


	@RequestMapping(value = "/saveOrUpdate", method = { RequestMethod.GET, RequestMethod.POST })
	public Message saveOrUpdate(@RequestBody  SchedulerJobInfo job) {
		log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.saveOrupdate(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("updateCron ex:", e);
		}
		return message;
	}

	@RequestMapping("/metaData")
	public Object metaData() throws SchedulerException {
		SchedulerMetaData metaData = scheduleJobService.getMetaData();
		return metaData;
	}

	@RequestMapping("/getAllJobs")
	public Object getAllJobs() throws SchedulerException {
		List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
		return jobList;
	}

	@RequestMapping(value = "/runJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object runJob(@RequestParam  Long id) {
		log.info("params, job id = {}", id);
		Message message = Message.failure();
		try {
			scheduleJobService.startJobNow(id);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("runJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/pauseJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object pauseJob(@RequestParam  Long id) {
		log.info("Job Id = {}",id);
		Message message = Message.failure();
		try {
			scheduleJobService.pauseJob(id);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("pauseJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/resumeJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object resumeJob(@RequestParam  Long id) {
		log.info("params, job id = {}", id);
		Message message = Message.failure();
		try {
			scheduleJobService.resumeJob(id);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("resumeJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/deleteJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object deleteJob(@RequestParam  Long id) {
		log.info("params, job id = {}", id);
		Message message = Message.failure();
		try {
			scheduleJobService.deleteJob(id);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("deleteJob ex:", e);
		}
		return message;
	}
}