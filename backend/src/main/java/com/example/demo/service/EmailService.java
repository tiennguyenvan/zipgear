package com.example.demo.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailService {

    private final SendGrid sendGrid;

    public EmailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

	// temprorary disable for fast testing
    public void sendEmail(String to, String subject, String body) throws IOException {
		if (Env.SKIP_SENDING_LOGIN_EMAIL || Env.SENDGRID_API_KEY == "") {
			return;
		}
		

        // Email from = new Email(Env.SENDGRID_FROM_EMAIL);
        // Email recipient = new Email(to);
        // Content content = new Content("text/plain", body);
        // Mail mail = new Mail(from, subject, recipient, content);

        // Request request = new Request();
        // request.setMethod(Method.POST);
        // request.setEndpoint("mail/send");
        // request.setBody(mail.build());

        // Response response = sendGrid.api(request);
        // System.out.println(response.getStatusCode());
        // System.out.println(response.getBody());
        // System.out.println(response.getHeaders());
    }
}
