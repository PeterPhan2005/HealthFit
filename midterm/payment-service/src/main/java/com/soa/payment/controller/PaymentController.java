package com.soa.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soa.payment.client.OtpClient;
import com.soa.payment.entity.Transaction;
import com.soa.payment.service.PaymentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    private final OtpClient otpClient;
    
    /**
     * Confirm payment - verify OTP and complete transaction
     * POST /api/payments/confirm
     * Body: { "customerId": 3, "studentId": "523H1001", "amount": 5000000, "otp": "123456", "semesterFeeId": 1 }
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> request) {
        try {
            Long customerId = Long.valueOf(request.get("customerId").toString());
            String studentId = request.get("studentId").toString();
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String otp = request.get("otp").toString();
            Long semesterFeeId = request.get("semesterFeeId") != null 
                ? Long.valueOf(request.get("semesterFeeId").toString()) 
                : null;
            
            // Verify OTP via OTP-SERVICE
            Map<String, String> verifyRequest = new HashMap<>();
            verifyRequest.put("customerId", customerId.toString());
            verifyRequest.put("code", otp);
            
            Map<String, Object> verifyResponse = otpClient.verifyOtp(verifyRequest);
            
            if (!Boolean.TRUE.equals(verifyResponse.get("success"))) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid or expired OTP"
                ));
            }
            
            // OTP verified - process payment
            Transaction transaction = paymentService.processPaymentWithSemesterFee(
                customerId, studentId, amount, semesterFeeId
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful",
                "transactionId", transaction.getId(),
                "status", transaction.getStatus()
            ));
        } catch (FeignException.Conflict e) {
            // HTTP 409 from student-service (already paid or concurrent update)
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> errorBody = mapper.readValue(e.contentUTF8(), Map.class);
                
                // Forward the error response from student-service (includes errorType, message, shouldRefresh)
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
            } catch (Exception parseException) {
                // If can't parse, return generic conflict message
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "errorType", "CONFLICT",
                    "message", "Học phí đang được xử lý. Vui lòng thử lại.",
                    "shouldRefresh", true
                ));
            }
        } catch (FeignException.BadRequest e) {
            // HTTP 400 from customer-service (insufficient balance) or student-service
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> errorBody = mapper.readValue(e.contentUTF8(), Map.class);
                
                // Forward the error response (includes errorType for INSUFFICIENT_BALANCE)
                return ResponseEntity.badRequest().body(errorBody);
            } catch (Exception parseException) {
                // If can't parse, return generic error
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể xử lý thanh toán. Vui lòng thử lại."
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable Long customerId) {
        List<Transaction> transactions = paymentService.getCustomerTransactions(customerId);
        return ResponseEntity.ok(transactions);
    }
}
