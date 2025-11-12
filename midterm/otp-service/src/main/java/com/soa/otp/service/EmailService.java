package com.soa.otp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send HTML email
     * @param toEmail recipient email
     * @param subject email subject
     * @param html HTML content
     */
    public void sendHtmlEmail(String toEmail, String subject, String html) {
        sendHtmlEmail(toEmail, subject, html, null);
    }

    /**
     * Send HTML email with optional Reply-To
     * @param toEmail recipient email
     * @param subject email subject
     * @param html HTML content
     * @param replyTo optional reply-to address
     */
    public void sendHtmlEmail(String toEmail, String subject, String html, @Nullable String replyTo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_NO, "UTF-8");

            helper.setFrom("lananhoutlier@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML content
            
            if (replyTo != null && !replyTo.isBlank()) {
                helper.setReplyTo(replyTo);
            }
            
            mailSender.send(message);
            System.out.println("HTML mail sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage(), e);
        }
    }
}
