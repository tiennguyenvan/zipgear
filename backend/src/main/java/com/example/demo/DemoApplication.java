package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.demo.service.GmailService;

//import com.example.demo.service.OAuth2TokenFetcher;


@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		
//		// Test fetching the access token
//        try {
//            String accessToken = OAuth2TokenFetcher.getAccessToken();
//            System.out.println("Access Token: " + accessToken);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
		
		try {
           GmailService.sendEmail("nguyentien.jobs@gmail.com", "Test Subject", "This is a test email from zipGear!");
       } catch (Exception e) {
           e.printStackTrace();
       }
		
		
	}

}
