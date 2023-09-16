package com.elsofthost.syncapp.controller;

import java.util.Map;
<<<<<<< HEAD
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
=======
import java.util.concurrent.atomic.AtomicInteger;

>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

<<<<<<< HEAD
import com.elsofthost.syncapp.entity.SchedulerJobInfo;
import com.elsofthost.syncapp.service.StudentService;
import com.elsofthost.syncapp.service.JobService;
=======
import com.elsofthost.syncapp.service.StudentService;

>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
@Controller
public class MyController {

	private final AtomicInteger counter = new AtomicInteger(0);
<<<<<<< HEAD
	@Autowired
	private StudentService studentService;
	@Autowired
	private JobService jobService;
=======
	
	private StudentService studentService;
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
	
	
	
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
<<<<<<< HEAD
	@GetMapping("/scheduler")
	public String scheduler(Model model) {
		List<SchedulerJobInfo> jobList = jobService.getAllJobList();
		model.addAttribute("jobs", jobList);
		return "scheduler";
	}
=======
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
}
