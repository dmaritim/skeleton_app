package com.elsofthost.syncapp.config;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class SchedulerConfig {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ApplicationContext applicationContext;

//	@Autowired
//	private QuartzProperties quartzProperties;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
    @PostConstruct
    public void init() {
        logger.info("Hello world from Quartz...");
    }

//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setOverwriteExistingJobs(true);
//        factory.setDataSource(dataSource);
//        factory.setQuartzProperties(quartzProperties());
//
//        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
//
//        jobFactory.setApplicationContext(applicationContext);
//        factory.setJobFactory(jobFactory);
//
//        return factory;
//    }
	@Bean
	public SchedulerFactoryBean quartzScheduler() {
		SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();

		quartzScheduler.setDataSource(dataSource);
		quartzScheduler.setTransactionManager(transactionManager);
		quartzScheduler.setOverwriteExistingJobs(true);
		quartzScheduler.setSchedulerName("jelies-quartz-scheduler");

		// custom job factory of spring with DI support for @Autowired!
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		quartzScheduler.setJobFactory(jobFactory);

		quartzScheduler.setQuartzProperties(quartzProperties());

//		Trigger[] triggers = { procesoMQTrigger().getObject() };
//		quartzScheduler.setTriggers(triggers);

		return quartzScheduler;
	}

	@Bean
	public Properties quartzProperties() {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		Properties properties = null;
		try {
			propertiesFactoryBean.afterPropertiesSet();
			properties = propertiesFactoryBean.getObject();

		} catch (IOException e) {
			logger.warn("Cannot load quartz.properties.");
		}

		return properties;
	}
    
//	@Bean
//	public SchedulerFactoryBean schedulerFactoryBean() {
//
//		SchedulerJobFactory jobFactory = new SchedulerJobFactory();
//		jobFactory.setApplicationContext(applicationContext);
//
//		Properties properties = new Properties();
//		properties.putAll(quartzProperties.getProperties());
//		properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
//
//		SchedulerFactoryBean factory = new SchedulerFactoryBean();
//		factory.setOverwriteExistingJobs(true);
//		factory.setDataSource(dataSource);
//		factory.setQuartzProperties(properties);
//		factory.setJobFactory(jobFactory);
//		return factory;
//	}
//	@Bean
//	public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer()
//	{
//	  return new SchedulerFactoryBeanCustomizer()
//	  {
//	     @Override
//	     public void customize(SchedulerFactoryBean bean)
//	     {
//	        bean.setQuartzProperties(createQuartzProperties());
//	     }
//	  };
//	}
//
//	private Properties createQuartzProperties()
//	{
//	    // Could also load from a file
//	    Properties props = new Properties();
//	    props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
//	    return props;
//	}
	
    @Bean()
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        return scheduler;
    }
}
