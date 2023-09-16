package com.elsofthost.syncapp.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class EmailJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

//    @Autowired
//    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;
    
    @Value("${spring.mail.username}")
    private String userName;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String smtp;
    @Value("${spring.mail.port}")
    private String port;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        logger.info("Executing Job with key {}", JobExecutionException.getJobDetail().getKey());

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipientEmail = jobDataMap.getString("email");
        sendMail(mailProperties.getUsername(), recipientEmail, subject, body);        	
    }

    private void sendMail(String fromEmail, String toEmail, String subject, String body) {
        try {
            logger.info("Sending Email to {}", toEmail);
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(smtp);
            mailSender.setPort(587);
            mailSender.setUsername(userName);
            mailSender.setPassword(password);
             
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
             
            mailSender.setJavaMailProperties(properties);
            
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            mailSender.send(message);
            logger.error("Have send email to {}", toEmail);
        } catch (MessagingException ex) {
            logger.error("Failed to send email to {}", toEmail);
        }
    }

//	@Override
//	public void execute(JobExecutionContext context) throws JobExecutionException {
//		// TODO Auto-generated method stub
//		
//	}
}
