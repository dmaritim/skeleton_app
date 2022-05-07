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
    private String jobId;
    
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
    private Long repeatTime;
    
    @Column(name = "cron_job")
    private Boolean cronJob;
    
    
	public SchedulerJobInfo() {
		super();
	}
	public String getJobId() {
		return jobId;
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
	public Long getRepeatTime() {
		return repeatTime;
	}
	public Boolean getCronJob() {
		return cronJob;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
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
	public void setRepeatTime(Long repeatTime) {
		this.repeatTime = repeatTime;
	}
	public void setCronJob(Boolean cronJob) {
		this.cronJob = cronJob;
	}
	@Override
	public String toString() {
		return "SchedulerJobInfo [jobId=" + jobId + ", jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobStatus="
				+ jobStatus + ", jobClass=" + jobClass + ", cronExpression=" + cronExpression + ", desc=" + desc
				+ ", interfaceName=" + interfaceName + ", repeatTime=" + repeatTime + ", cronJob=" + cronJob + "]";
	}
    
    
}
