package com.elsofthost.syncapp.controller;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.elsofthost.syncapp.service.StudentService;

@Controller
public class MyController {

	private final AtomicInteger counter = new AtomicInteger(0);
	
	private StudentService studentService;
	
	
	
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
}
