package com.elsofthost.syncapp.controller;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elsofthost.syncapp.entity.Message;
import com.elsofthost.syncapp.entity.SchedulerJobInfo;
import com.elsofthost.syncapp.entity.Student;
import com.elsofthost.syncapp.service.SchedulerJobService;


@Controller
public class SchedulerController {

	@Autowired
	private SchedulerJobService scheduleJobService;
	
	
	
	@GetMapping("/scheduler")
	public String index(Model model){
		List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
		model.addAttribute("jobs", jobList);
		return "scheduler";
	}
	
	@GetMapping("/scheduler/new")
	public String create(Model model) {
		// create student object to hold student form data
		SchedulerJobInfo schedulerJobInfo = new SchedulerJobInfo();
		model.addAttribute("schedulerJob", schedulerJobInfo);
		return "create_job";
	}
	
	
	@PostMapping("/scheduler")
	public String saveScheduleJob(@ModelAttribute("student") SchedulerJobInfo schedulerJobInfo) {
		Message message = Message.failure();	
		try {
				scheduleJobService.saveOrupdate(schedulerJobInfo);
				message = Message.success();
			} catch (Exception e) {
				message.setMsg(e.getMessage());
			}
		return "redirect:/scheduler";
	}
	
//	@GetMapping("/scheduler/edit/{id}")
//	public String editStudentForm(@PathVariable Long id, Model model) {
//		model.addAttribute("schedulerJob", scheduleJobService.getSchedulerJobInfoByJobId(id));
//		return "edit_job";
//	}
	
	@RequestMapping(value = "/scheduler/saveOrUpdate", method = { RequestMethod.GET, RequestMethod.POST })
	public Object saveOrUpdate(SchedulerJobInfo job) {
		//log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.saveOrupdate(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			//log.error("updateCron ex:", e);
		}
		return message;
	}

	@RequestMapping("/scheduler/metaData")
	public Object metaData() throws SchedulerException {
		SchedulerMetaData metaData = scheduleJobService.getMetaData();
		return metaData;
	}

	@RequestMapping("/getAllJobs")
	public Object getAllJobs() throws SchedulerException {
		List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
		return jobList;
	}

	@RequestMapping(value = "/scheduler/runJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object runJob(SchedulerJobInfo job) {
		//log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.startJobNow(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			//log.error("runJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/scheduler/pauseJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object pauseJob(SchedulerJobInfo job) {
		//log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.pauseJob(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			//log.error("pauseJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/scheduler/resumeJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object resumeJob(SchedulerJobInfo job) {
		//log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.resumeJob(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			//log.error("resumeJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/scheduler/deleteJob", method = { RequestMethod.GET, RequestMethod.POST })
	public Object deleteJob(SchedulerJobInfo job) {
		//log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.deleteJob(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			//log.error("deleteJob ex:", e);
		}
		return message;
	}
	
}
