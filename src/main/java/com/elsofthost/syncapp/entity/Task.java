package com.elsofthost.syncapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "class_name")
	private String className;
	
	@Column(name = "description")
	private String description;
	
    @Column(name = "cron_job")
    private Boolean cronJob;

	public Task() {
		super();
	}

	public Task(Long id, String name, String className, String description, Boolean cronJob) {
		this.id = id;
		this.name = name;
		this.className = className;
		this.description = description;
		this.cronJob = cronJob;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getCronJob() {
		return cronJob;
	}

	public void setCronJob(Boolean cronJob) {
		this.cronJob = cronJob;
	}
}
