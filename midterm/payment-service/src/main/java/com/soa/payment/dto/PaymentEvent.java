package com.soa.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent implements Serializable {
    private Long transactionId;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String studentId;
    private String studentName;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String semesterCode;
    private LocalDateTime createdAt;

    /**
     * Format amount to VND currency for email
     */
    public String getFormattedAmount() {
        if (amount == null) return "0 ₫";
        return String.format("%,.0f ₫", amount.doubleValue());
    }

    /**
     * Format datetime for email
     */
    public String getFormattedDateTime() {
        if (createdAt == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return createdAt.format(formatter);
    }
}
