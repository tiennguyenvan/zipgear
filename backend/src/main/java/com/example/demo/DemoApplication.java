package com.example.demo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.Env;
import com.example.demo.service.Lib;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class DemoApplication {
	private static final Dotenv dotenv = Dotenv.load();	
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);	
		// System.out.println("SKIP_SENDING_LOGIN_EMAIL" + Boolean.parseBoolean("true"));
		// System.out.println(Env.SKIP_SENDING_LOGIN_EMAIL);
		// System.out.println(dotenv.get("VUE_APP_SKIP_SENDING_LOGIN_EMAIL"));		
		// System.out.println(dotenv.get("VUE_APP_VALIDATION_CODE_LENGTH"));		
    }

	@Autowired
    private UserRepository userRepository;


    // Use CommandLineRunner to test sending the email on startup
    @Bean
    public CommandLineRunner run(ApplicationContext ctx) {
        return args -> {

			// Example addresses
            List<String> addresses = new ArrayList<String>();			
			addresses.add("123 Main St, BC, Canada");
			addresses.add("456 Elm St, BC, Canada");
			addresses.add("789 Oak St, BC, Canada");

            // Create a new user with example addresses
            User user = new User("example@example.com", addresses);

            // Save the user to the database
            userRepository.save(user);

            System.out.println("Sample user created with addresses: " + addresses);

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