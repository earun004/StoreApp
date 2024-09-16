package com.kosuri.stores.handler;

import com.kosuri.stores.config.AppProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


public class EmailService {

	private final JavaMailSender javaMailSender;

	private final AppProperties appProperties;
	public EmailService(JavaMailSender javaMailSender, AppProperties appProperties) {
		this.javaMailSender = javaMailSender;
		this.appProperties = appProperties;
	}

	public boolean sendEmailMessage(String email, String mailMessage, String subject) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject(subject);
		message.setFrom(appProperties.getMailUsername());
		message.setText(mailMessage);
		javaMailSender.send(message);
		return true;
	}

}