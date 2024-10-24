package com.example.demo.service;

import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Bean
    public SendGrid sendGrid() {        
        return new SendGrid(Env.SENDGRID_API_KEY);
    }
}
