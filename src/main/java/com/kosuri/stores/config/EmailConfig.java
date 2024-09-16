package com.kosuri.stores.config;

import com.kosuri.stores.handler.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailConfig {

    @Bean
    public EmailService emailService(JavaMailSender javaMailSender, AppProperties appProperties) {
        return new EmailService(javaMailSender, appProperties);
    }
}
