package com.elsofthost.syncapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "scheduler_job_info")
public class SchedulerJobInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", nullable = false)
<<<<<<< HEAD
    private Long Id;
=======
    private String jobId;
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
    
    @Column(name = "job_name")
    private String jobName;
    
    @Column(name = "job_group")
    private String jobGroup;
    
    @Column(name = "job_status")
    private String jobStatus;
    
    @Column(name = "job_class")
    private String jobClass;
    
    @Column(name = "job_expression")
    private String cronExpression;
    
    @Column(name = "job_desc")
    private String desc; 
    
    @Column(name = "interface_name")
    private String interfaceName;
    
    @Column(name = "repeat_time")
<<<<<<< HEAD
    private Integer repeatTime;
=======
    private Long repeatTime;
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
    
    @Column(name = "cron_job")
    private Boolean cronJob;
    
    
	public SchedulerJobInfo() {
		super();
	}
<<<<<<< HEAD
	
	public SchedulerJobInfo(Long jobId, String jobName, String jobGroup, String jobStatus, String jobClass,
			String cronExpression, String desc, String interfaceName, Integer repeatTime, Boolean cronJob) {
		super();
		this.Id = jobId;
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.jobStatus = jobStatus;
		this.jobClass = jobClass;
		this.cronExpression = cronExpression;
		this.desc = desc;
		this.interfaceName = interfaceName;
		this.repeatTime = repeatTime;
		this.cronJob = cronJob;
	}

	public Long getId() {
		return Id;
=======
	public String getJobId() {
		return jobId;
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
	}
	public String getJobName() {
		return jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public String getJobClass() {
		return jobClass;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public String getDesc() {
		return desc;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
<<<<<<< HEAD
	public Integer getRepeatTime() {
=======
	public Long getRepeatTime() {
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
		return repeatTime;
	}
	public Boolean getCronJob() {
		return cronJob;
	}
<<<<<<< HEAD
	public void setId(Long jobId) {
		this.Id = jobId;
=======
	public void setJobId(String jobId) {
		this.jobId = jobId;
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
<<<<<<< HEAD
	public void setRepeatTime(Integer repeatTime) {
=======
	public void setRepeatTime(Long repeatTime) {
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
		this.repeatTime = repeatTime;
	}
	public void setCronJob(Boolean cronJob) {
		this.cronJob = cronJob;
	}
	@Override
	public String toString() {
<<<<<<< HEAD
		return "SchedulerJobInfo [jobId=" + Id + ", jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobStatus="
=======
		return "SchedulerJobInfo [jobId=" + jobId + ", jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobStatus="
>>>>>>> 67df3bf126e02c14a84dd83edd9d17d6e6653c5c
				+ jobStatus + ", jobClass=" + jobClass + ", cronExpression=" + cronExpression + ", desc=" + desc
				+ ", interfaceName=" + interfaceName + ", repeatTime=" + repeatTime + ", cronJob=" + cronJob + "]";
	}
    
    
}
