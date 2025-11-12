package com.soa.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Email Service - Gửi email
 * 
 * GIẢI THÍCH:
 * - JavaMailSender: Spring Boot component để gửi email
 * - MimeMessage: Email message (có thể chứa HTML)
 * - Thymeleaf: Template engine để render HTML từ template
 * 
 * LUỒNG:
 * 1. Nhận thông tin (email, subject, data)
 * 2. Load template HTML
 * 3. Replace variables trong template với data
 * 4. Gửi email
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * Send HTML email using Thymeleaf template
     * 
     * @param to Recipient email
     * @param subject Email subject
     * @param templateName Thymeleaf template name (without .html)
     * @param context Variables for template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        if (!emailEnabled) {
            logger.info("Email is disabled. Would send to: {}", to);
            return;
        }

        try {
            // Create MIME message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            // Process template with context variables
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true); // true = HTML

            // Send email
            mailSender.send(message);
            
            logger.info("✅ Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            logger.error("❌ Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send simple text email (backup method)
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            logger.info("Email is disabled. Would send to: {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // false = plain text

            mailSender.send(message);
            
            logger.info("✅ Simple email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            logger.error("❌ Failed to send simple email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
