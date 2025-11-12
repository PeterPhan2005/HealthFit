package com.soa.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PaymentEvent - Message được gửi từ PAYMENT-SERVICE
 * 
 * GIẢI THÍCH:
 * Đây là format của message trong RabbitMQ queue.
 * PAYMENT-SERVICE sẽ publish object này → JSON
 * NOTIFICATION-SERVICE sẽ consume JSON → object này
 * 
 * VÍ DỤ JSON:
 * {
 *   "transactionId": 1,
 *   "customerId": 1,
 *   "customerName": "John Doe",
 *   "customerEmail": "john@example.com",
 *   "studentId": "523H1001",
 *   "studentName": "Nguyen Van A",
 *   "amount": 3500000.0,
 *   "semesterCode": "HK2_2024",
 *   "createdAt": "2025-10-30T12:00:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    /**
     * Transaction ID
     */
    @JsonProperty("transactionId")
    private Long transactionId;

    /**
     * Customer information
     */
    @JsonProperty("customerId")
    private Long customerId;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("customerEmail")
    private String customerEmail;

    /**
     * Student information
     */
    @JsonProperty("studentId")
    private String studentId;

    @JsonProperty("studentName")
    private String studentName;

    /**
     * Payment details
     */
    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("semesterCode")
    private String semesterCode;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    /**
     * Format amount to VND currency
     */
    public String getFormattedAmount() {
        if (amount == null) return "0 ₫";
        return String.format("%,.0f ₫", amount);
    }

    /**
     * Format datetime
     */
    public String getFormattedDateTime() {
        if (createdAt == null) return "";
        return createdAt.toString();
    }
}
