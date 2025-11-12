package com.soa.otp.controller;

import com.soa.otp.service.OtpService;
import com.soa.otp.service.EmailService;
import com.soa.otp.templates.EmailTemplates;
import com.soa.otp.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private static final String SUPPORT_EMAIL = "ibankingsupport@gmail.com";

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    /**
     * Generate OTP for payment transaction and send to customer email
     * POST /api/otp/generate
     * Body: { "studentId": "523H1001", "customerId": 3, "customerEmail": "user@example.com", "customerName": "John Doe" }
     * Note: studentId is only used for email display, not stored in database
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(@RequestBody Map<String, String> request) {
        try {
            // Get customerId from request body (for microservices inter-service calls)
            String customerIdStr = request.get("customerId");
            Long customerId = customerIdStr != null ? Long.valueOf(customerIdStr) : SecurityUtil.getCurrentCustomerId();
            
            String studentId = request.get("studentId"); // Only for email display
            String customerEmail = request.get("customerEmail");
            String customerName = request.get("customerName");

            if (customerId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Customer ID is required"
                ));
            }

            if (studentId == null || studentId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Student ID is required"
                ));
            }

            if (customerEmail == null || customerEmail.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Customer email is required"
                ));
            }

            String code = otpService.generateOtp(customerId); // Only use customerId for DB

            // Send OTP email
            try {
                final String subject = "MÃ OTP THANH TOÁN HỌC PHÍ";
                String html = EmailTemplates.otpTemplate()
                        .replace("[[fullName]]", customerName != null ? customerName : "Khách hàng")
                        .replace("[[otp]]", code)
                        .replace("[[studentId]]", studentId)
                        .replace("[[supportEmail]]", SUPPORT_EMAIL);

                emailService.sendHtmlEmail(customerEmail, subject, html);
            } catch (Exception mailEx) {
                System.err.println("Failed to send OTP email: " + mailEx.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to send OTP email: " + mailEx.getMessage()
                ));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "OTP sent successfully to " + customerEmail,
                "expiresInMinutes", 5
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to generate OTP: " + e.getMessage()
            ));
        }
    }

    /**
     * Verify OTP code
     * POST /api/otp/verify
     * Body: { "customerId": 3, "code": "123456" }
     * Note: studentId is not needed for verification
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        try {
            // Get customerId from request body (for microservices inter-service calls)
            String customerIdStr = request.get("customerId");
            Long customerId = customerIdStr != null ? Long.valueOf(customerIdStr) : SecurityUtil.getCurrentCustomerId();
            
            String code = request.get("code");

            if (customerId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Customer ID is required"
                ));
            }

            if (code == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "OTP code is required"
                ));
            }

            boolean isValid = otpService.verifyAndConsumeOtp(customerId, code);

            if (isValid) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP verified successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Invalid or expired OTP"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to verify OTP: " + e.getMessage()
            ));
        }
    }
}
