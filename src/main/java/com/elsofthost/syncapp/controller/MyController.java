package com.elsofthost.syncapp.controller;

import java.util.Map;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.elsofthost.syncapp.entity.SchedulerJobInfo;
import com.elsofthost.syncapp.service.StudentService;
import com.elsofthost.syncapp.service.JobService;
import com.elsofthost.syncapp.service.StudentService;


@Controller
public class MyController {

	private final AtomicInteger counter = new AtomicInteger(0);

	@Autowired
	private StudentService studentService;
	@Autowired
	private JobService jobService;

	
	public MyController(StudentService studentService) {
		super();
		this.studentService = studentService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		return "index";
	}
	
	@GetMapping("/counter")
	public String myView(Map model) {
		counter.set(counter.get()+1);
		model.put("counter", counter.get());
		return "view-page";
	}

	@GetMapping("/scheduler")
	public String scheduler(Model model) {
		List<SchedulerJobInfo> jobList = jobService.getAllJobList();
		model.addAttribute("jobs", jobList);
		return "scheduler";
	}
}
