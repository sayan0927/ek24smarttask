package com.example.smart_task_management.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * A utility class for sending simple email notifications using the Spring framework's
 * {@link JavaMailSender}. This class encapsulates the email sending process.
 */
@Component
public class Mailer {

    // Injecting the JavaMailSender to send emails
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a simple email to the specified recipient with a given subject and body.
     *
     * This method creates a {@link SimpleMailMessage} object, sets the recipient, subject, and
     * body, and then sends the email using the {@link JavaMailSender} instance.
     *
     * @param recipient the email address of the recipient
     * @param subject the subject of the email
     * @param body the content or body of the email
     * @throws MailException if sending the email fails (e.g., due to connection issues or invalid configuration)
     */
    public void sendEmail(String recipient, String subject, String body) throws MailException {
        // Create a new email message
        SimpleMailMessage message = new SimpleMailMessage();

        // Set email properties
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);

        // Send the email
        mailSender.send(message);
    }
}
