package com.example.demo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.demo.service.EmailService;
import com.example.demo.service.Env;
import com.example.demo.service.Lib;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);	
		// System.out.println(Lib.ValidationCode(Env.VALIDATION_CODE_LENGTH));		
    }

    // Use CommandLineRunner to test sending the email on startup
    @Bean
    public CommandLineRunner run(ApplicationContext ctx) {
        return args -> {
			
            // EmailService emailService = ctx.getBean(EmailService.class);

            // try {
            //     emailService.sendEmail(
            //         "nguyentien.jobs@gmail.com",  // Replace with your recipient
            //         "Test Subject",
            //         "This is a test email from Spring Boot!"
            //     );
            //     System.out.println("Email sent successfully!");
            // } catch (Exception e) {
            //     System.err.println("Failed to send email: " + e.getMessage());
            // }
        };
    }
}